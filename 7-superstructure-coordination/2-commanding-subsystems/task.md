# Commanding Subsystems from a Higher-Level FSM

You've named the coherent robot configurations as a `RobotState` enum. Now
something needs to *act* on a `RobotState` selection: when the driver picks
`SCORE_L4`, the elevator, arm, and intake all need to receive their respective
setpoints.

That something is the **`Superstructure`** — the top-level coordinator. It
holds references to every subsystem, watches `commandedRobotState`, and on
each tick pushes each per-subsystem setpoint into the right place.

```kotlin
class Superstructure {
    val elevator = Elevator()
    val arm = Arm()
    val intake = Intake()

    var commandedRobotState: RobotState = RobotState.STOWED

    fun periodic() {
        stateActions()
        elevator.tick()
        arm.tick()
        intake.tick()
    }

    private fun stateActions() {
        elevator.commandedTarget = commandedRobotState.elevator
        arm.commandedTarget = commandedRobotState.arm
        intake.commandedMode = commandedRobotState.intake
    }
}
```

Look familiar? It's the same two-method-pattern shape as a per-subsystem FSM.
The difference is what `stateActions()` does:

- A subsystem's `stateActions()` writes to a *motor* (`motor.setControl(...)`).
- A superstructure's `stateActions()` writes to *subsystems*
  (`elevator.commandedTarget = ...`).

Subsystems all the way down. Each FSM operates on the layer below it.

## Note: class, not object

Earlier subsystems were `object` singletons. Here it's a `class`. Reason:
several test cases each want a fresh `Superstructure`; classes let us `new` a
clean instance per test. On a real robot, you'd typically still have one — a
top-level `object Superstructure` — but for teaching, the class form keeps
test isolation simple.

## Your task

Open `src/Superstructure.kt`. Implement `stateActions()` to write each
subsystem's commanded value from `commandedRobotState`'s per-subsystem
property.

(`RobotState` is pre-declared in this file with the correct setpoints from
Task 1. The subsystems' `tick()` is already wired into `periodic()`.)
