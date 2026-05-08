# Robot State Enum

So far, every FSM you've built has lived inside *one* subsystem. But the robot
itself is also an FSM — a higher-level one whose "state" describes the entire
configuration of the machine: where the elevator sits, how the arm is angled,
what the intake is doing.

A naïve approach would be three separate variables (`elevatorTarget`,
`armTarget`, `intakeMode`) updated from various places. That makes it easy to
end up with weird mid-states like "elevator high, arm stowed, intake
intaking" — a configuration that's not anything coherent.

The fix: name the *coherent* configurations. Each named configuration is a
robot state, and each robot state knows the per-subsystem setpoints that
together make it valid:

```kotlin
enum class RobotState(
    val elevator: Elevator.State,
    val arm: Arm.State,
    val intake: Intake.Mode,
) {
    STOWED       (elevator = Elevator.State.STOWED, arm = Arm.State.STOWED, intake = Intake.Mode.IDLE),
    INTAKE_GROUND(elevator = Elevator.State.LOW,    arm = Arm.State.GROUND, intake = Intake.Mode.INTAKING),
    // ...
}
```

This is enum-with-properties from Lesson 1, but "scaled up" — each value
carries setpoints for *multiple* subsystems instead of one.

## Your task

Open `src/RobotState.kt`. The four state names and their constructor calls
are present, but every state currently has the same (wrong) placeholder
setpoints. Update each one per this table:

| State           | elevator                 | arm                  | intake                  |
|-----------------|--------------------------|----------------------|-------------------------|
| `STOWED`        | `Elevator.State.STOWED`  | `Arm.State.STOWED`   | `Intake.Mode.IDLE`      |
| `INTAKE_GROUND` | `Elevator.State.LOW`     | `Arm.State.GROUND`   | `Intake.Mode.INTAKING`  |
| `SCORE_L4`      | `Elevator.State.HIGH`    | `Arm.State.SCORE`    | `Intake.Mode.HOLDING`   |
| `CLIMB_PREP`    | `Elevator.State.STOWED`  | `Arm.State.CLIMB`    | `Intake.Mode.IDLE`      |

`STOWED`'s placeholder happens to already be correct — its test will pass as
delivered. The other three need updating.

## Aside

Naming things matters. `SCORE_L4` reads like an intent. `(HIGH, SCORE,
HOLDING)` reads like setpoints. The state name is what auto code, driver
binding code, and dashboards reference; the setpoints are the implementation
detail that lives on the enum.
