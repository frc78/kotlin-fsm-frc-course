# Sequencing Guards Across Subsystems

Tasks 2 and 3 commanded every subsystem at once: `STOWED → SCORE_L4` told the
elevator to go up *and* the arm to extend on the same tick. On a real robot,
that can break things. The arm extending while the elevator is mid-travel
can smash the arm into a structural beam, or the wrist can snag on the
elevator carriage.

Sequence the moves instead. Going up:

1. Elevator first, all the way up. Arm stays stowed.
2. Then arm extends.

This task introduces a small **sub-FSM for the transition itself**. Its
value is the current state of the robot while it moves toward the goal
state:

```kotlin
sealed class Transition {
    data class WaitingForElevator(val target: RobotState) : Transition()
    data class WaitingForArm(val target: RobotState)      : Transition()
    data class Settled(val at: RobotState)                : Transition()
}
```

This is a sealed class with parameterized subclasses, like the shooter FSM
in Lesson 1 Task 12. The data carried (the goal state we move toward) lives
on the state itself.

## Back to two methods

The superstructure now has a current state of its own, so it gets the full
two-method pattern from lesson 2:

```kotlin
override fun periodic() {
    stateTransitions()  // read the subsystems, decide the next phase
    stateActions()      // command the subsystems for the current phase
    elevator.tick(); arm.tick(); intake.tick()
}
```

`stateTransitions()` reads the subsystems before this tick's actions run.
A phase therefore ends one tick after its mechanism settles, and each phase
costs one tick more than if the actions ran first. That is the normal price
of the pattern: a sensor is always read one tick behind the command.

The first lines of `stateTransitions()` are given. They detect a new goal
and restart the sequence at `WaitingForElevator(newGoal)`.

## The phases

| Phase                   | Elevator command | Arm command                    | Advance when                                    |
|-------------------------|------------------|--------------------------------|-------------------------------------------------|
| `Settled(at)`           | `at.elevator`    | `at.arm`, intake = `at.intake` | (already settled)                               |
| `WaitingForElevator(t)` | `t.elevator`     | (do not change)                | elevator settled at `t.elevator`                |
| `WaitingForArm(t)`      | `t.elevator`     | `t.arm`, intake = `t.intake`   | `arm.atTarget() && intake.requestReached()`     |

"Elevator settled at `t.elevator`" means both `elevator.atTarget()` and
`elevator.state == t.elevator`. On the tick the goal changes, the elevator has not received its new command yet, so
`atTarget()` alone is still `true` for the old target. Without the position
check the sequence would skip the elevator phase.

In `WaitingForElevator`, the arm and intake are not commanded. They keep
their old commanded values. Both start at `STOWED`/`STOP`, which matches the
initial settled pose, so they stay there.

> **Scope note:** This task only handles "going up." Going down (from
> `SCORE_L4` back to `STOWED`) needs the reverse sequence: arm first, then
> elevator. That is task 5. Here, tests only exercise ascents.

## Your task

Implement two methods in `src/Superstructure.kt`:

1. **`stateTransitions()`**: after the given goal check, advance to the next
   phase when the current phase's "Advance when" condition holds.
2. **`stateActions()`**: based on the current phase, command the right
   subsystems per the table above.

Both are a `when` on the sealed `Transition` type with one branch per
subclass. Use `is` for the parameterized subclasses.
