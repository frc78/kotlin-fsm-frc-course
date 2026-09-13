# `atTarget()` Rolls Up

In Lesson 3 your `Elevator` exposed `atTarget(): Boolean`. It answered a
useful question: "is the elevator settled where I asked it to go?" Auto
routines checked it before they moved to the next step.

The same question is meaningful at the superstructure layer:

> Is the **whole robot** at its goal state?

The robot is at target only if every contributing subsystem reports that it
is. Higher-level FSMs compose this way. A robot is "at" `SCORE_L4` only when
each subsystem that contributes to that pose has settled.

The three queries you will combine:

- `elevator.atTarget(): Boolean`
- `arm.atTarget(): Boolean`
- `intake.modeReached(): Boolean` (the intake has no continuous position,
  it only reaches a roller mode, so the name differs)

## Why subsystems take time

The stubs simulate physical motion. The elevator takes 3 ticks to reach a
new target. The arm takes 2. The intake snaps immediately.
`stateActions()` (already written for you) commands all three subsystems on
the same tick, so they move at the same time. The superstructure is at
target once the **slowest** subsystem settles. After commanding `SCORE_L4`
from `STOWED`, that is the elevator: 3 periodic ticks before
`Superstructure.atTarget()` becomes true. Making them take turns is the
next task.

## Your task

Implement `atTarget()` in `src/Superstructure.kt`. It returns `true` only
when all three per-subsystem queries above return `true`.
