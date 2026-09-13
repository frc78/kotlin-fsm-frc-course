# Robot State Enum

So far, every FSM you built lived inside one subsystem. The robot itself is
also an FSM. Its state describes the whole machine: where the elevator sits,
how the arm is angled, what the intake does.

Three separate variables (`elevatorTarget`, `armTarget`, `intakeMode`)
updated from many places make it easy to reach a mid-state that means
nothing, such as "elevator high, arm stowed, intake intaking".

The fix: name the coherent configurations. Each named configuration is a
robot state. Each robot state carries the per-subsystem setpoints that make
it valid. This is enum-with-properties from Lesson 1, scaled up: each value
carries setpoints for several subsystems instead of one.

## Your task

Open `src/RobotState.kt`. The four state names and their constructor calls
are present. Every state has the same placeholder setpoints. Update each one
per this table:

| State           | elevator                 | arm                  | intake                  |
|-----------------|--------------------------|----------------------|-------------------------|
| `STOWED`        | `Elevator.State.STOWED`  | `Arm.State.STOWED`   | `Intake.Mode.IDLE`      |
| `INTAKE_GROUND` | `Elevator.State.LOW`     | `Arm.State.GROUND`   | `Intake.Mode.INTAKING`  |
| `SCORE_L4`      | `Elevator.State.HIGH`    | `Arm.State.SCORE`    | `Intake.Mode.HOLDING`   |
| `CLIMB_PREP`    | `Elevator.State.STOWED`  | `Arm.State.CLIMB`    | `Intake.Mode.IDLE`      |

`STOWED`'s placeholder is already correct. The other three need updating.

## Aside

Naming things matters. `SCORE_L4` reads like an intent. `(HIGH, SCORE,
HOLDING)` reads like setpoints. The state name is what auto code, driver
binding code, and dashboards reference. The setpoints are the implementation
detail that lives on the enum.
