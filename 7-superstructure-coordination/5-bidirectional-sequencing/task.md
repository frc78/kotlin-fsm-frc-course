# Bidirectional Sequencing + Abort

Task 4 only handled going up: elevator first, then arm. Going *down* is
asymmetric. You must **retract the arm first**, then lower the elevator.
Otherwise the arm sweeps through everything on the way down.

This final task implements both directions plus mid-transition abort.

## The protocol

A "safe pose" means **arm at `STOWED`**. From the safe pose, anything is
reachable: lower or raise the elevator freely, then re-extend the arm.

So every transition follows the same three-phase sequence:

| Phase             | Commands                                                        | Advance when                              |
|-------------------|-----------------------------------------------------------------|-------------------------------------------|
| `RetractArm(t)`   | Arm to `STOWED`. Do not command the elevator.                   | `arm.atTarget()`                          |
| `MoveElevator(t)` | Elevator to `t.elevator`.                                       | `elevator.atTarget()`                     |
| `ExtendArm(t)`    | Arm to `t.arm`, intake to `t.intake`.                           | `arm.atTarget() && intake.modeReached()`  |
| `Settled(at)`     | All three to `at`'s setpoints, so they stay held.               | (stays)                                   |

When a transition starts, phases that are already complete are skipped:

- If the arm is already at `STOWED`, skip `RetractArm`.
- If the elevator is already at `t.elevator`, skip `MoveElevator`.
- If everything is already at `t`, go straight to `Settled(t)`.

A helper `startTransition(target)` decides which phase to begin in. It is
already implemented for you.

## Abort handling

If the goal state changes mid-transition, the current state is computed
again: `periodic()` calls `startTransition(newGoal)` from the current
subsystem states. That handles "user pressed cancel": the arm retracts to
safe, the elevator returns to `STOWED`, the intake idles. You do not write
the abort detection.

One simplification to be aware of: the stubs report a subsystem's `state` as
its last *settled* position, even while it is mid-flight. A real robot would
also treat in-flight mechanisms as unsafe. It would check `commandedTarget`
and `atTarget()`, not only `state`.

## Your task

Read `startTransition` first. It decides what should happen.
`stateActions` and `advanceTransition` route control flow.

Two methods in `src/Superstructure.kt`:

1. **`stateActions()`**: for each phase, command the right subsystems per
   the table above. Use `when (val t = transition)` and the four
   sealed-class branches.

2. **`advanceTransition()`**: when a phase completes, advance to the next
   phase with `startTransition(t.target)`. Calling `startTransition` again
   skips phases that are now complete.

## Test scenarios

Tests cover:

- Going up (`STOWED → SCORE_L4`): elevator before arm.
- Going down (`SCORE_L4 → STOWED`): arm retracts before the elevator descends.
- Mid-ascent abort: command back to `STOWED` before reaching `SCORE_L4`.
- Cycle: `STOWED → INTAKE_GROUND → STOWED → SCORE_L4 → STOWED` returns home.
