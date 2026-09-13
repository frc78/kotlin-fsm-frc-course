# Lesson 7 fixes. Design

Date: 2026-09-13. Source: `docs/superpowers/reviews/2026-09-12-lessons-2-8-review.md`,
lesson 7 proposals 1 to 5.

## Goal

Make lesson 7 teach the superstructure pattern the way a real robot uses it:

1. The intake owns "do I have a piece". The superstructure only asks the intake to
   run, stop, or eject.
2. The superstructure follows the two-method pattern taught in lesson 2:
   `stateTransitions()` then `stateActions()`.
3. A mechanism in flight is not at any state. Abort logic must see that.
4. A phase that never settles times out, moves the robot to the safe pose, and
   raises a fault.

Lesson 7 keeps its number and its seven existing tasks. One programming task is
added. The two checks move to directories 7 and 8. Only lesson 7 uses the
superstructure stubs, so stub changes touch nothing else.

## Stub changes (`util/src/main/kotlin/frc/stubs/superstructure/`)

### Intake

```kotlin
class Intake {
    enum class Request { STOP, INTAKE, EJECT }
    enum class Mode { IDLE, INTAKING, HOLDING, EJECTING }

    var request: Request            // set by the superstructure; a change costs spinUpTicks
    val mode: Mode                  // what the rollers do now; read-only outside the stub
    fun simulatePieceDetected(present: Boolean)   // test hook: the beam break
    fun tick()
    fun requestReached(): Boolean   // rollers are doing what was requested
}
```

Rules:

- A change of `request` sets `ticksRemaining = 4`. Rollers take 4 ticks to spin up
  or down.
- `tick()` counts down. When the count is 0, `mode` is derived: `STOP` gives
  `HOLDING` if a piece is present, else `IDLE`; `INTAKE` gives `INTAKING`; `EJECT`
  gives `EJECTING`.
- `requestReached()` is true when the count is 0 and `mode` matches the derived
  mode for `request`.

### Elevator and Arm

One change each. The `commandedTarget` setter today sets `ticksRemaining = 0` when
the new target equals `state`, even mid-flight. That teleports a reversing
mechanism home. New rule:

```kotlin
ticksRemaining = if (value == state) stepsToReach - ticksRemaining else stepsToReach
```

A mechanism that reverses mid-flight needs the time it already traveled to
return. `state` still holds the last settled position. `atTarget()` is unchanged.

## RobotState table (tasks 1 to 6, checks)

| State           | elevator | arm      | intake   |
|-----------------|----------|----------|----------|
| `STOWED`        | `STOWED` | `STOWED` | `STOP`   |
| `INTAKE_GROUND` | `LOW`    | `GROUND` | `INTAKE` |
| `SCORE_L4`      | `HIGH`   | `SCORE`  | `STOP`   |
| `CLIMB_PREP`    | `STOWED` | `CLIMB`  | `STOP`   |

`SCORE_L4` does not ask the intake to hold. The intake holds a piece by itself
when its sensor sees one. Scoring is a driver eject, not part of a pose.

## Task changes

### Task 1, Robot state enum

Page: new table. One paragraph on ownership: a pose says what the rollers should
do, never what the intake should sense. Starter and tests use `Intake.Request`.

### Task 2, Commanding subsystems

`class Superstructure : Subsystem` with `override fun periodic()`. Page: the
subsystem table lists `intake.request: Intake.Request`. Replace the "same
two-method shape" claim with: this task has only `stateActions()`; task 4 adds
`stateTransitions()`. Tests check `intake.request`.

### Task 3, atTarget

Third query is `intake.requestReached()`. Page: the intake takes 4 ticks after a
request change, so for `INTAKE_GROUND` it is the slowest subsystem. New test:
after `INTAKE_GROUND` and 3 ticks, elevator and arm are settled and the intake is
not, so `atTarget()` is false. This catches a solution that omits the intake.

### Task 4, Sequencing guards

`periodic()` becomes:

```kotlin
override fun periodic() {
    stateTransitions()
    stateActions()
    elevator.tick(); arm.tick(); intake.tick()
}
```

`stateTransitions()` replaces `advanceTransition()` and also owns the goal-change
check (given as the first lines of the method, the student adds the advance
rules). The page says the sensors read one tick behind, so every phase costs one
extra tick compared with acting first. Fixed tick counts in tests are
recomputed. Existing eight tests stay.

### Task 5, Bidirectional sequencing

Same `periodic()` and `stateTransitions()` shape. `startTransition()` is given and
uses honest settled checks:

```kotlin
private fun elevatorSettledAt(s: Elevator.State) = elevator.atTarget() && elevator.state == s
private fun armSettledAt(s: Arm.State) = arm.atTarget() && arm.state == s
private fun intakeSettledAt(r: Intake.Request) = intake.requestReached() && intake.request == r

private fun startTransition(target: RobotState): Transition = when {
    !armSettledAt(Arm.State.STOWED) && !elevatorSettledAt(target.elevator) -> RetractArm(target)
    !elevatorSettledAt(target.elevator) -> MoveElevator(target)
    !armSettledAt(target.arm) || !intakeSettledAt(target.intake) -> ExtendArm(target)
    else -> Settled(target)
}
```

Page: delete the "simplification" paragraph. Replace with: a mechanism in flight
is not at any state; `state` is the last settled position; "settled at X" means
`atTarget()` and `state == X`. New test: after a mid-ascent abort the elevator is
not at target on the abort tick and settles two ticks later, so a teleporting
stub or a `state`-only check fails.

### Task 6, Phase timeout (new, `6-phase-timeout`, package `course.l7t6`)

Starter is the task 5 solution with three additions:

```kotlin
val phaseTimer = Timer()                 // frc.stubs.Timer
var faulted: Boolean = false
val phaseTimeoutSeconds = 2.0

private fun enterPhase(next: Transition)  // TODO: set transition; restart the timer if the phase changed
private fun checkTimeout()                // TODO: see rules
fun clearFault()                          // TODO: faulted = false
```

Every place in the given code that assigned `transition` now calls `enterPhase`.
`stateTransitions()` order: goal-change check, `checkTimeout()`, advance rules.

Timeout rules:

| Condition                                                      | Effect                                                                 |
|----------------------------------------------------------------|------------------------------------------------------------------------|
| `transition` is `Settled`                                      | nothing; the timer is ignored                                          |
| any other phase and `phaseTimer.hasElapsed(phaseTimeoutSeconds)` | `faulted = true`, `commandedRobotState = STOWED`, `enterPhase(startTransition(STOWED))` |
| `enterPhase(next)` with `next != transition`                   | `transition = next`, `phaseTimer.restart()`                            |
| `enterPhase(next)` with `next == transition`                   | nothing                                                                |

`faulted` is a flag only. It does not block new goals. A driver who sets a new
goal while faulted is followed. `clearFault()` only resets the flag.

Tests (`PhaseTimeoutTest`, fresh `Superstructure` per test, time advanced with
`s.phaseTimer.simulateAdvance(seconds)` between `periodic()` calls):

- Normal ascent with no time advanced: never faults, settles at `SCORE_L4`.
- `MoveElevator` running, advance 2.1 s, one tick: `faulted` true, goal `STOWED`,
  transition heads to `STOWED`.
- Same but advance 1.9 s: no fault.
- Settled, advance 10 s, one tick: no fault.
- Timer restarts per phase: advance 1.5 s in `MoveElevator`, let it advance to
  `ExtendArm`, advance 1.5 s more: no fault.
- After a fault, `clearFault()` makes `faulted` false and a new goal is followed.
- Reference solution passes; starter fails with `NotImplementedError`.

### Checks

`6-check-attarget` moves to `7-check-attarget`. Text uses `intake.requestReached()`
and "rollers take 4 ticks". `7-check-sequencing` moves to `8-check-sequencing`.
Text uses `intake STOP`. `task-remote-info.yaml` files move with their
directories and are not edited.

## Wiring

- `lesson-info.yaml`: insert `6-phase-timeout` before the checks; checks renamed.
- `build.gradle.kts`: add `6-phase-timeout/src` and `/test`.
- `CLAUDE.md` and `README.md`: 63 tasks, 41 programming.
- `course-info.yaml`: summary unchanged.

## Verification

Each programming task: starter fails with `NotImplementedError`, reference
solution passes, restored starter fails. Full `./gradlew build` compiles; every
failure is a starter placeholder.
