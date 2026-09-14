# Lesson 7 rebuild: peer state machines. Design

Date: 2026-09-14. Replaces the lesson 7 of `2026-09-13-lesson-7-fixes-design.md`.
Model: `frc78/2025-robot`, branch `tim/stateMachine` (`StateMachineManager.kt`,
`SuperStructure.kt`, `Intake.kt`, `Climber.kt`, `OI.kt`).

## Goal

Tim: "there is no robot-wide state machine. I think this is better." Lesson 7 stops
teaching a coordinator that owns a goal state and commands subsystems. It teaches
what the team's robot does:

1. Every mechanism group is a peer FSM: `SuperStructure`, `Intake`, `Climber`.
2. The superstructure's states are named poses. Each pose carries setpoints for the
   mechanisms that must move together (elevator and arm). It has no intake field.
3. Sequencing is a stateless rule on sensor readings inside `stateActions()`, not a
   phase sub-FSM.
4. A peer reads another peer's `state` and `atPosition` like a sensor. No peer
   commands another.
5. Driver input arrives through an `OI` object of booleans.
6. A scheduler calls each FSM's `periodic()` in a fixed order. Producer before
   consumer.

Lesson 7 keeps eight tasks and its eight directories (renamed with `git mv` so the
seven marketplace IDs survive). Packages stay `course.l7t1` to `course.l7t6`.
Each FSM keeps the course split: `stateTransitions()` then `stateActions()`.

## Stubs

### Deleted

`util/src/main/kotlin/frc/stubs/superstructure/Intake.kt`. The student writes the
intake FSM.

### `frc/stubs/OI.kt` (new)

```kotlin
object OI {
    var home = false
    var intake = false
    var scoreL2 = false
    var scoreL4 = false
    var score = false
    var prepareClimb = false
    var climb = false
    fun reset()        // all false
}
```

Header comment: the real `OI` reads an `XboxController` and a button board and
exposes functions such as `OI.home()`; the stub exposes booleans that tests set.

### `frc/stubs/Debouncer.kt` (new)

```kotlin
class Debouncer(private val ticks: Int) {
    fun calculate(input: Boolean): Boolean   // true once input has been true for `ticks` consecutive calls
    fun reset()
}
```

Header comment: WPILib's `Debouncer(seconds, DebounceType.kRising)` counts time.
This stub counts calls, one per tick.

### `frc/stubs/superstructure/Elevator.kt` (rewritten)

```kotlin
object Elevator {
    val position: Double          // rotations, simulated
    val target: Double            // last goTo() argument, starts 0.0
    val atPosition: Boolean       // abs(position - target) < 0.1
    fun goTo(rotations: Double)
    fun simulationPeriodic()      // position moves toward target by at most 3.0 rotations
    fun reset()                   // position 0.0, target 0.0
}
```

### `frc/stubs/superstructure/Arm.kt` (rewritten)

Same shape in degrees: `angle`, `target` (starts 90.0), `atPosition`
(`abs(angle - target) < 2.0`), `goTo(degrees)`, `simulationPeriodic()` moves at most
45.0 degrees, `reset()` sets angle and target to 90.0.

Both stubs mirror the real `Elevator.goTo(height)` / `Pivot.goTo(angle)` and
`atPosition` properties. Motion is a fixed step per tick, so mid-flight is honest:
`atPosition` is false until arrival, and a reversal takes the distance back.

## Pose table (every task)

| Pose             | `elevatorRotations` | `armDegrees` |
|------------------|---------------------|--------------|
| `HOME`           | 0.0                 | 90.0         |
| `CORAL_STATION`  | 2.0                 | 30.0         |
| `L2`             | 4.0                 | 45.0         |
| `L4`             | 14.5                | 45.0         |
| `READY_TO_CLIMB` | 0.0                 | 180.0        |
| `FULLY_CLIMBED`  | 0.0                 | 5.0          |

`enum class Pose(val elevatorRotations: Double, val armDegrees: Double)`, top level
in each task package. `SAFE_ARM_DEGREES = 90.0`.

## SuperStructure (tasks 2 to 6)

```kotlin
object SuperStructure : Subsystem {
    var state: Pose = Pose.HOME
    val atPosition: Boolean get() =
        Elevator.target == state.elevatorRotations && Arm.target == state.armDegrees &&
            Elevator.atPosition && Arm.atPosition
    // The target checks matter. Under the sequencing rule the elevator is not
    // commanded until the arm arrives, so for one tick it is "at" its old target.
    // Without the checks the intake could eject at L4 with the elevator at HOME.
    override fun periodic() {
        stateTransitions()
        stateActions()
        Elevator.simulationPeriodic()
        Arm.simulationPeriodic()
    }
    private fun stateTransitions()
    private fun stateActions()
    fun reset()      // state HOME, Elevator.reset(), Arm.reset()
}
```

Transition table. Inside each state the rows are checked top to bottom; the first
match wins. `holding` means `Intake.state == Intake.State.HOLDING`.

| Current          | Condition                        | Next             |
|------------------|----------------------------------|------------------|
| `HOME`           | `OI.intake && !holding`          | `CORAL_STATION`  |
| `HOME`           | `holding && OI.scoreL2`          | `L2`             |
| `HOME`           | `holding && OI.scoreL4`          | `L4`             |
| `HOME`           | `OI.prepareClimb`                | `READY_TO_CLIMB` |
| `CORAL_STATION`  | `OI.home`                        | `HOME`           |
| `L2`             | `OI.home`                        | `HOME`           |
| `L2`             | `holding && OI.scoreL4`          | `L4`             |
| `L4`             | `OI.home`                        | `HOME`           |
| `L4`             | `holding && OI.scoreL2`          | `L2`             |
| `READY_TO_CLIMB` | `OI.climb`                       | `FULLY_CLIMBED`  |
| `READY_TO_CLIMB` | `OI.home`                        | `HOME`           |
| `FULLY_CLIMBED`  | none                             | stay             |

Actions without sequencing (task 2): `Elevator.goTo(state.elevatorRotations)` and
`Arm.goTo(state.armDegrees)` every tick.

Actions with the sequencing rule (tasks 3 to 6), the frc78 rule with the course's
mechanisms:

| Case                                        | Elevator command            | Arm command                                                            |
|---------------------------------------------|-----------------------------|------------------------------------------------------------------------|
| going down: `state.elevatorRotations < Elevator.position` | `state.elevatorRotations` every tick | `SAFE_ARM_DEGREES` until `abs(Elevator.position - state.elevatorRotations) < 0.5`, then `state.armDegrees` |
| otherwise (up or level)                     | `state.elevatorRotations` only when `abs(Arm.angle - state.armDegrees) < 2.0`; else no call, so the last target holds | `state.armDegrees` every tick |

## Intake (tasks 4 to 6)

```kotlin
object Intake : Subsystem {
    enum class State(val control: ControlRequest) {
        IDLE(NeutralOut), INTAKING(VoltageOut(6.0)), HOLDING(VoltageOut(1.0)), EJECTING(VoltageOut(-6.0))
    }
    val motor = TalonFX(canId = 15)
    var state: State = State.IDLE
    private val stallDebounce = Debouncer(5)
    override fun periodic() { stateTransitions(); stateActions() }
    private fun stateTransitions()
    private fun stateActions()     // motor.setControl(state.control)
    fun reset()                    // state IDLE, stallDebounce.reset(), motor.stopMotor()
}
```

`stalled` is `stallDebounce.calculate(motor.getStatorCurrent() > 10.0)`, evaluated
once per tick at the top of `stateTransitions()` so the debouncer counts every tick.
`scoringPose` is `SuperStructure.state == Pose.L2 || SuperStructure.state == Pose.L4`.

| Current    | Condition                                              | Next       |
|------------|--------------------------------------------------------|------------|
| `IDLE`     | `OI.intake`                                            | `INTAKING` |
| `INTAKING` | `OI.home`                                              | `IDLE`     |
| `INTAKING` | `stalled`                                              | `HOLDING`  |
| `HOLDING`  | `OI.score && scoringPose && SuperStructure.atPosition` | `EJECTING` |
| `EJECTING` | `!OI.score`                                            | `IDLE`     |

The intake decides that it holds a piece. The intake decides when it is safe to
eject, by reading the superstructure. Nobody commands the intake.

## Climber (tasks 5 and 6)

```kotlin
object Climber : Subsystem {
    enum class State(val control: ControlRequest) {
        RETRACTED(PositionVoltage(0.0)), EXTENDED(PositionVoltage(72.0))
    }
    val motor = TalonFX(canId = 16)
    var state: State = State.RETRACTED
    override fun periodic() { stateTransitions(); stateActions() }
    ...
}
```

| Current     | Condition                                                          | Next       |
|-------------|--------------------------------------------------------------------|------------|
| `RETRACTED` | `SuperStructure.state == Pose.FULLY_CLIMBED && SuperStructure.atPosition` | `EXTENDED` |
| `EXTENDED`  | none                                                               | stay       |

72.0 rotations is a 12 turns-per-inch lead screw extended 6 inches.

## Task table

| # | Directory (old, `git mv`)                                  | Package       | Type   | Student writes                                   |
|---|------------------------------------------------------------|---------------|--------|--------------------------------------------------|
| 1 | `1-pose-enum` (from `1-robot-state-enum`)                  | `course.l7t1` | edu    | the six `Pose` values from the table             |
| 2 | `2-superstructure-fsm` (from `2-commanding-subsystems`)    | `course.l7t2` | edu    | `SuperStructure.stateTransitions()` and the plain `stateActions()` |
| 3 | `3-sequencing-rule` (from `3-at-target`)                   | `course.l7t3` | edu    | `stateActions()` with the sequencing rule        |
| 4 | `4-intake-fsm` (from `4-sequencing-guards`)                | `course.l7t4` | edu    | `Intake.stateTransitions()` and `stateActions()` |
| 5 | `5-climber-fsm` (from `5-bidirectional-sequencing`)        | `course.l7t5` | edu    | `Climber.stateTransitions()` and `stateActions()` |
| 6 | `6-robot-loop` (from `6-phase-timeout`)                    | `course.l7t6` | edu    | `StateMachineManager.teleopPeriodic()` and `reset()` |
| 7 | `7-check-who-decides` (from `7-check-attarget`)            | none          | choice | ownership question                               |
| 8 | `8-check-update-order` (from `8-check-sequencing`)         | none          | choice | scheduler order question                         |

Given code per package: task 2 needs `Intake.state` for `holding`, so tasks 2 and 3
include a given minimal
`object Intake { enum class State { IDLE, INTAKING, HOLDING, EJECTING }; var state = IDLE }`
so tests can set `Intake.state = HOLDING`. Task 4 replaces it with the real FSM and
includes the solved task 3 `SuperStructure`. Tasks 5 and 6 include the solved
`SuperStructure` and `Intake`.

## Task pages

### Task 1, Pose enum

Why poses: three setpoint variables updated from many places reach meaningless
mid-states. Name the coherent configurations. A pose carries setpoints for the
mechanisms that move together. It says nothing about the intake: the intake owns
its own state. Table of poses. Student fills five placeholder rows (`HOME` is
correct). Tests: six values, every setpoint.

### Task 2, The superstructure is one peer FSM

Two ideas. First, the superstructure is an FSM like the lesson 3 subsystems, with
`Pose` as its state type. Second, its inputs are the driver (`OI`) and another
FSM's state (`Intake.state`), read like a sensor. Transition table above. Actions:
both `goTo` calls every tick. `atPosition` is given. Tests: every row of the table
including priority (`OI.intake` and `holding && OI.scoreL4` both true in `HOME`
with `holding` true gives `L4`, because row 1 needs `!holding`), stay in
`FULLY_CLIMBED`, both targets commanded on the first tick, `atPosition` false while
moving and true after enough ticks (`L4` from `HOME`: arm 1 tick, elevator 5 ticks).

### Task 3, Sequencing as a rule

Why: the arm swings through the elevator's path. Going up, swing the arm first;
going down, lower the elevator first while the arm waits at the safe angle. The
rule reads sensors every tick and needs no extra state. Present the two-row rule
table. Tests: `HOME` to `L4`: after tick 1 `Arm.target == 45.0` and
`Elevator.target == 0.0`; after tick 2 `Elevator.target == 14.5`. `L4` to `L2`
from settled `L4` (`holding && OI.scoreL2`): tick 1 `Elevator.target == 4.0`,
`Arm.target == 90.0`; keep ticking until `abs(Elevator.position - 4.0) < 0.5`, then
next tick `Arm.target == 45.0`. Level move `HOME` to `READY_TO_CLIMB`: arm commanded
180 at once, elevator target stays 0. Reversal mid-flight: `HOME` to `L4`, 3 ticks,
`OI.home`, then `Elevator.target == 0.0` and `Arm.target == 90.0` on the next tick
and the elevator is not at position yet.

### Task 4, The intake owns its piece

The intake FSM with `State(val control: ControlRequest)`, the lesson 1 enum
property pattern. Hold detection by stall current with a `Debouncer(5)`. Eject only
when the superstructure is at a scoring pose and settled: the intake reads
`SuperStructure.state` and `SuperStructure.atPosition`. Transition table above.
Tests: `OI.intake` gives `INTAKING`; stall for 4 ticks stays `INTAKING`, fifth tick
`HOLDING`; a one-tick current dropout resets the count; `OI.home` while intaking
gives `IDLE`; `OI.score` in `HOLDING` at `HOME` does nothing; at `L4` while the arm
still moves does nothing; at `L4` settled gives `EJECTING` and `VoltageOut(-6.0)`;
release gives `IDLE` and `NeutralOut`; `stateActions` sends `state.control` every
tick.

### Task 5, The climber reads the superstructure

Smallest FSM in the course, on purpose. Table above. The climber never receives a
command from the superstructure; it watches. Tests: stays `RETRACTED` at
`READY_TO_CLIMB`; stays `RETRACTED` at `FULLY_CLIMBED` while `atPosition` is false;
`EXTENDED` with `PositionVoltage(72.0)` once settled; never returns to `RETRACTED`
when the superstructure leaves.

### Task 6, The robot loop

`object StateMachineManager { fun teleopPeriodic(); fun reset() }`. Order rule from
the 2056 page: producer before consumer, or the consumer reacts one tick late.
`Intake` and `Climber` read `SuperStructure`, so `SuperStructure.periodic()` runs
first, then `Intake.periodic()`, then `Climber.periodic()`. `SuperStructure` also
reads `Intake.state`; a pose change one tick late is harmless, an eject one tick
early is not, so the superstructure goes first. `reset()` resets `OI`,
`SuperStructure`, `Intake`, `Climber`. Tests: a full match scenario through the
manager (intake, stall, `L4`, wait for settle, score, release, home, prepare climb,
climb, climber extends); the climber extends on the same tick the superstructure
first reports `FULLY_CLIMBED` and `atPosition` (fails when the climber runs first);
`reset()` restores every FSM.

### Check 7, Who decides

The driver holds score while the superstructure is at `L4` with the arm still
moving. Nothing ejects. Which FSM makes that decision, and how? Options: the intake,
by reading `SuperStructure.atPosition` (correct); the superstructure, by
withholding a command to the intake; the scheduler, by skipping the intake this
tick; the arm, by reporting `HOLDING`. Feedback names the peer-read pattern.

### Check 8, Update order

A teammate writes `teleopPeriodic()` as `Climber`, `Intake`, `SuperStructure`. On
tick N the superstructure first reports `FULLY_CLIMBED` and `atPosition`. On which
tick does the climber leave `RETRACTED`? Options: tick N; tick N+1 (correct); never,
because the climber must be commanded; tick N-1. Feedback: a consumer that runs
before its producer reads last tick's value.

## Wiring

- `build.gradle.kts`: rename the six lesson 7 `src` and `test` paths.
- `lesson-info.yaml`: the eight new directory names.
- `course-info.yaml`: remove `superstructure/Intake.kt`, add `OI.kt` and
  `Debouncer.kt` under `additional_files`; summary line 7 becomes "Coordinating
  subsystems — peer state machines that read each other's state, a pose-enum
  superstructure, and the robot loop that runs them."
- `README.md` line 19: same idea.
- Lesson 8 task 8 page: unchanged; it already describes this pattern.

## Verification

Each programming task: starter fails with `NotImplementedError`, reference solution
passes, restored starter fails. Full build compiles; every failure is a starter
placeholder. `grep -rn 'Transition\|commandedRobotState\|requestReached\|phaseTimer'`
over lesson 7 returns nothing.
