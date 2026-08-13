# `atTarget()` Rolls Up

In Lesson 3 your `Elevator` exposed `atTarget(): Boolean`. It was a useful
question: "is the elevator settled where I asked it to go?" Auto routines
checked it before progressing to the next step.

The same question is meaningful at the superstructure layer:

> Is the **whole robot** at its commanded state?

The answer is just the conjunction of the per-subsystem answers — the
robot is "at target" iff *every* contributing subsystem reports that it
is. Higher-level FSMs naturally compose this way. A robot is "at"
`SCORE_L4` only when each subsystem that contributes to that pose has
settled.

The three queries you'll combine:

- `elevator.atTarget(): Boolean`
- `arm.atTarget(): Boolean`
- `intake.modeReached(): Boolean` (note the different name — the intake
  doesn't have a continuous "position," it just reaches a roller mode)

## Why subsystems take time

The stubs simulate physical motion. Elevator takes 3 ticks to reach a new
target. Arm takes 2. Intake snaps immediately (it's just a roller mode).
`stateActions()` (already written for you) commands all three subsystems on
the *same* tick, so they move concurrently — the superstructure is at target
once the **slowest** subsystem settles. After commanding `SCORE_L4` from
`STOWED`, that's the elevator: 3 periodic ticks before
`Superstructure.atTarget()` becomes true. Note that they all move at the
same time — making them take turns is the next task.

## Your task

Implement `atTarget()` in `src/Superstructure.kt`. The body is one line
— a single boolean expression combining the three per-subsystem queries
listed above.
