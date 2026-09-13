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

| Subsystem | Query                              | True when                                  |
|-----------|------------------------------------|--------------------------------------------|
| elevator  | `elevator.atTarget(): Boolean`     | the carriage is settled at its target      |
| arm       | `arm.atTarget(): Boolean`          | the arm is settled at its target           |
| intake    | `intake.requestReached(): Boolean` | the rollers do what was last requested     |

## Why subsystems take time

The stubs simulate physical motion. The elevator takes 3 ticks to reach a
new target. The arm takes 2. The intake rollers take 4 ticks to spin up or
down after a request change. If the request does not change, the intake is
already there.

`stateActions()` (already written for you) commands all three subsystems on
the same tick, so they move at the same time. The superstructure is at
target once the **slowest** subsystem settles. After commanding `SCORE_L4`
from `STOWED`, that is the elevator: 3 periodic ticks. After commanding
`INTAKE_GROUND`, that is the intake: 4 ticks. Making them take turns is the
next task.

## Your task

Implement `atTarget()` in `src/Superstructure.kt`. It returns `true` only
when all three queries in the table return `true`.
