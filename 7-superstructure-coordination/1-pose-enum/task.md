# Pose Enum

So far, every FSM you built lived inside one subsystem. This lesson builds the
FSMs that make a whole robot work together, the way FRC team 78's 2025 robot
does. Nothing in it is a "robot state machine". There are three peer state
machines: a superstructure, an intake, and a climber. Each one runs its own
`stateTransitions()` and `stateActions()` every tick. Each one reads the
driver's buttons and, when it needs to, another machine's `state`. None of
them commands another.

The superstructure owns the mechanisms that must move together: the elevator
and the arm. Its states are **poses**.

## Why poses

Two separate variables, `elevatorTarget` and `armTarget`, updated from many
places, make it easy to reach a position that means nothing, such as
"elevator high, arm folded into the frame". A pose names each coherent
position and carries the setpoints that make it valid. This is
enum-with-properties from lesson 1, with two setpoints per value instead of
one.

A pose says nothing about the intake. The intake owns its own state: it
decides by itself whether it holds a piece. Task 4 builds it.

## Your task

Open `src/Pose.kt`. The six pose names are present. Every value has the same
placeholder setpoints. Update each one per this table:

| Pose             | `elevatorRotations` | `armDegrees` |
|------------------|---------------------|--------------|
| `HOME`           | `0.0`               | `90.0`       |
| `CORAL_STATION`  | `2.0`               | `30.0`       |
| `L2`             | `4.0`               | `45.0`       |
| `L4`             | `14.5`              | `45.0`       |
| `READY_TO_CLIMB` | `0.0`               | `180.0`      |
| `FULLY_CLIMBED`  | `0.0`               | `5.0`        |

`HOME` is already correct. The other five need updating.

## Aside

`L4` reads like an intent. `(14.5, 45.0)` reads like setpoints. The pose name
is what the driver bindings, the auto code, and the dashboard refer to. The
setpoints are the detail that lives on the enum.
