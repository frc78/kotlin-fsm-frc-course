# Robot State Enum

So far, every FSM you built lived inside one subsystem. The robot itself is
also an FSM. Its state describes the whole machine: where the elevator sits,
how the arm is angled, what the intake rollers do.

Three separate variables (`elevatorTarget`, `armTarget`, `intakeRequest`)
updated from many places make it easy to reach a mid-state that means
nothing, such as "elevator high, arm stowed, rollers intaking".

Name the coherent configurations instead. Each named configuration is a
robot state. Each robot state carries the per-subsystem setpoints that make
it valid. This is enum-with-properties from Lesson 1, scaled up: each value
carries setpoints for several subsystems instead of one.

## Who owns what

A robot state tells each subsystem what to do. It never tells a subsystem
what to sense. The intake is the example:

| Owner          | Decides                                   | Type             |
|----------------|-------------------------------------------|------------------|
| superstructure | run the rollers, stop them, or eject      | `Intake.Request` |
| intake         | idle, intaking, holding, or ejecting now  | `Intake.Mode`    |

The intake reads its own beam-break sensor. If the rollers are stopped and a
piece is present, the intake reports `HOLDING` by itself. No robot state asks
for `HOLDING`. `SCORE_L4` asks the rollers to `STOP`, and the intake holds
the piece because it has one. Scoring is a driver eject, not part of a pose.

## Your task

Open `src/RobotState.kt`. The four state names and their constructor calls
are present. Every state has the same placeholder setpoints. Update each one
per this table:

| State           | elevator                 | arm                  | intake                  |
|-----------------|--------------------------|----------------------|-------------------------|
| `STOWED`        | `Elevator.State.STOWED`  | `Arm.State.STOWED`   | `Intake.Request.STOP`   |
| `INTAKE_GROUND` | `Elevator.State.LOW`     | `Arm.State.GROUND`   | `Intake.Request.INTAKE` |
| `SCORE_L4`      | `Elevator.State.HIGH`    | `Arm.State.SCORE`    | `Intake.Request.STOP`   |
| `CLIMB_PREP`    | `Elevator.State.STOWED`  | `Arm.State.CLIMB`    | `Intake.Request.STOP`   |

`STOWED`'s placeholder is already correct. The other three need updating.

## Aside

`SCORE_L4` reads like an intent. `(HIGH, SCORE, STOP)` reads like
setpoints. The state name is what auto code, driver
binding code, and dashboards reference. The setpoints are the implementation
detail that lives on the enum.
