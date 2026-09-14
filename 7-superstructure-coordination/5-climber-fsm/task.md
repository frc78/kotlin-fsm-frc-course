# Bidirectional Sequencing + Abort

Task 4 only handled going up: elevator first, then arm. Going *down* is
asymmetric. You must **retract the arm first**, then lower the elevator.
Otherwise the arm sweeps through everything on the way down.

This task implements both directions plus mid-transition abort.

## The protocol

A "safe pose" means **arm at `STOWED`**. From the safe pose, anything is
reachable: lower or raise the elevator freely, then re-extend the arm.

So every transition follows the same three-phase sequence:

| Phase             | Commands                                                        | Advance when                                |
|-------------------|-----------------------------------------------------------------|---------------------------------------------|
| `RetractArm(t)`   | Arm to `STOWED`. Do not command the elevator.                   | `arm.atTarget()`                            |
| `MoveElevator(t)` | Elevator to `t.elevator`.                                       | `elevator.atTarget()`                       |
| `ExtendArm(t)`    | Arm to `t.arm`, intake to `t.intake`.                           | `arm.atTarget() && intake.requestReached()` |
| `Settled(at)`     | All three to `at`'s setpoints, so they stay held.               | (stays)                                     |

When a transition starts, phases that are already complete are skipped:

- If the arm is already settled at `STOWED`, skip `RetractArm`.
- If the elevator is already settled at `t.elevator`, skip `MoveElevator`.
- If everything is already settled at `t`, go straight to `Settled(t)`.

A helper `startTransition(target)` decides which phase to begin in. It is
already implemented for you.

## In flight is not a state

A mechanism that is still moving is not at any state. The stubs keep
`state` as the **last settled position** while the mechanism moves, and
`atTarget()` returns `false` until it arrives. So "settled at X" means both
`atTarget()` and `state == X`. `startTransition` uses three helpers,
`elevatorSettledAt`, `armSettledAt`, and `intakeSettledAt`, that check
exactly that. An arm that is halfway back to `STOWED` is not safe yet, and
the helpers say so.

## Two methods, in order

`periodic()` calls `stateTransitions()` first and `stateActions()` second,
the order from lesson 2. `stateTransitions()` reads the sensors and picks the
phase. `stateActions()` commands the subsystems for that phase. The sensors
it reads were updated by the previous tick, so each phase costs one extra
tick compared with acting first.

## Abort handling

If the goal state changes mid-transition, the current state is computed
again: the first lines of `stateTransitions()` (already written) call
`startTransition(newGoal)` from the current subsystem states. That handles
"user pressed cancel": the arm retracts to safe, the elevator returns to
`STOWED`, the intake stops. A mechanism that reverses mid-flight needs the
time it already traveled to get back. You do not write the abort detection.

## Your task

Read `startTransition` first. It decides what should happen.
`stateTransitions` and `stateActions` route control flow.

Two methods in `src/Superstructure.kt`:

1. **`stateTransitions()`**: after the given goal-change lines, check the
   current phase's "Advance when" condition from the table. When it holds,
   move on with `startTransition(t.target)`. Calling `startTransition` again
   skips phases that are now complete.

2. **`stateActions()`**: for each phase, command the right subsystems per
   the table above. Use `when (val t = transition)` and the four
   sealed-class branches.

## Test scenarios

Tests cover:

- Going up (`STOWED → SCORE_L4`): elevator before arm.
- Going down (`SCORE_L4 → STOWED`): arm retracts before the elevator descends.
- Mid-ascent abort: command back to `STOWED` before reaching `SCORE_L4`. The
  elevator takes real ticks to come back.
- Cycle: `STOWED → INTAKE_GROUND → STOWED → SCORE_L4 → STOWED` returns home.
