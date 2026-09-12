# Sequencing Guards Across Subsystems

Tasks 2 and 3 commanded every subsystem at once: `STOWED → SCORE_L4` told the
elevator to go up *and* the arm to extend on the same tick. On a real robot,
that can break things. The arm extending while the elevator is mid-travel
might smash the arm into a structural beam, or the wrist of the arm might
snag on the elevator carriage.

The fix: **sequence the moves**. Going up:

1. Elevator first, all the way up. Arm stays stowed.
2. Then arm extends.

This task introduces a small **sub-FSM for the transition itself** — three
phases:

```kotlin
sealed class Transition {
    data class WaitingForElevator(val target: RobotState) : Transition()
    data class WaitingForArm(val target: RobotState)      : Transition()
    data class Settled(val at: RobotState)                : Transition()
}
```

Note: this is a *sealed class with parameterized subclasses*, exactly like
the shooter FSM in Lesson 1 Task 12. The data being carried (the target we're
moving toward) lives on the state itself.

## How the phases play

| Phase                      | Elevator command         | Arm command                | Advance when                            |
|----------------------------|--------------------------|----------------------------|-----------------------------------------|
| `Settled(at)`              | `at.elevator`            | `at.arm`                   | (already settled)                       |
| `WaitingForElevator(t)`    | `t.elevator`             | (don't change)             | `elevator.atTarget()`                   |
| `WaitingForArm(t)`         | `t.elevator`             | `t.arm`, intake = `t.intake` | `arm.atTarget() && intake.modeReached()` |

In `WaitingForElevator`, the arm and intake are *not* commanded — they keep
their old commanded values. Since both started at `STOWED`/`IDLE` (matching
the initial settled pose), they'll stay there.

When the user changes `commandedRobotState`, `periodic()` resets the
transition to `WaitingForElevator(newTarget)` (already provided).

> **Scope note:** This task only handles "going up." Going down (from
> `SCORE_L4` back to `STOWED`) needs the *reverse* sequence — arm first, then
> elevator. That is the final task of this lesson. Here, tests only exercise
> ascents.

## Your task

Implement two methods in `src/Superstructure.kt`:

1. **`stateActions()`** — based on the current `Transition`, command the right
   subsystems per the table above.
2. **`advanceTransition()`** — if subsystems have settled, advance to the next
   phase.

Hint for both methods: a `when` on the sealed `Transition` type with one
branch per subclass. Use `is` for the parameterized subclasses (smart-cast
gives you `t.target`).
