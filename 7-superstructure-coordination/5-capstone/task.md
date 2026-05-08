# Capstone: Bidirectional Sequencing + Abort

Task 4 only handled going up — elevator first, then arm. Going *down* is
asymmetric: you must **retract the arm first**, then lower the elevator,
otherwise the arm flails through everything on the way down.

This capstone implements both directions plus mid-transition abort.

## The protocol

A "safe pose" means **arm at `STOWED`**. From the safe pose, anything is
reachable: lower or raise the elevator freely, then re-extend the arm.

So every transition follows the same three-phase sequence:

| Phase            | What happens                                                    |
|------------------|-----------------------------------------------------------------|
| `RetractArm(t)`  | Command arm to `STOWED`. Wait for arm to reach it.              |
| `MoveElevator(t)`| Command elevator to `t.elevator`. Wait.                         |
| `ExtendArm(t)`   | Command arm to `t.arm`, intake to `t.intake`. Wait.             |
| `Settled(at)`    | All three subsystems at their commanded values.                 |

When a transition starts, we *skip* phases that are already complete:

- If arm is already at `STOWED`, skip `RetractArm`.
- If elevator is already at `t.elevator`, skip `MoveElevator`.
- If everything's already at `t`, go straight to `Settled(t)`.

A helper `startTransition(target)` decides which phase to begin in. It's
already implemented for you — read it.

## Abort handling

If `commandedRobotState` changes mid-transition, we re-derive: call
`startTransition(newCommandedState)` again from the *current* subsystem
states. That naturally handles "user pressed cancel" — arm retracts to safe,
elevator returns to STOWED, intake idles.

`periodic()` already handles the abort detection — you don't need to write
that. It calls `startTransition` for you when needed.

## Your task

Two methods in `src/Superstructure.kt`:

1. **`stateActions()`** — for each phase, command the right subsystems per the
   table above. Use `when (val t = transition)` and the four sealed-class
   branches. (`Settled` should command all three to keep them held.)

2. **`advanceTransition()`** — when a phase completes, advance to the next
   appropriate phase using `startTransition(t.target)`. (Calling
   `startTransition` again is the cleanest way to skip phases that are now
   trivially done.)

## Test scenarios

Tests cover:

- Going up (`STOWED → SCORE_L4`): elevator before arm.
- Going down (`SCORE_L4 → STOWED`): arm retracts before elevator descends.
- Mid-ascent abort: command back to `STOWED` before reaching `SCORE_L4`.
- Cycle: `STOWED → INTAKE_GROUND → STOWED → SCORE_L4 → STOWED` returns home.

## A pedagogical note

This is the most complex single piece of code in the course. If you find
yourself stuck, the trick is to read `startTransition` carefully — it's the
brain of the whole thing. `stateActions` and `advanceTransition` mostly route
control flow; `startTransition` decides what should happen.
