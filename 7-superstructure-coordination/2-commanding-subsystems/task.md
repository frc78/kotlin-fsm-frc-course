# Commanding Subsystems from a Higher-Level FSM

You named the coherent robot configurations as a `RobotState` enum. Now
something must act on a `RobotState` selection. When the driver picks
`SCORE_L4`, the elevator, arm, and intake must each receive their setpoint.

That something is the **`Superstructure`**, the top-level coordinator. It
holds a reference to every subsystem, reads the goal, and on each tick pushes
each per-subsystem setpoint into the right place.

## Two terms for the rest of this lesson

- The **goal state** is `commandedRobotState`: the `RobotState` the driver
  or auto code asked for.
- The **current state** is where the robot is now. In this task the robot
  reaches the goal state as soon as every subsystem settles. Tasks 4 and 5
  add a `transition` field that tracks the current state while the robot
  moves.

This is the same two-method-pattern shape as a per-subsystem FSM. The
difference is what `stateActions()` writes to:

- A subsystem's `stateActions()` writes to a motor
  (`motor.setControl(...)`).
- A superstructure's `stateActions()` writes to subsystems
  (`elevator.commandedTarget = ...`).

Each FSM operates on the layer below it.

## The subsystems

`Superstructure` owns three children, already wired up in the file:

| Child                | Writable field                       |
|----------------------|--------------------------------------|
| `elevator: Elevator` | `commandedTarget: Elevator.State`    |
| `arm: Arm`           | `commandedTarget: Arm.State`         |
| `intake: Intake`     | `commandedMode: Intake.Mode`         |

The goal state carries a matching property for each subsystem
(`commandedRobotState.elevator`, `.arm`, `.intake`).

## Note: class, not object

Earlier subsystems were `object` singletons. Here it is a `class`. Several
test cases each want a fresh `Superstructure`, and a class gives a clean
instance per test. On a real robot you have one instance either way.

## Your task

Open `src/Superstructure.kt`. Implement `stateActions()` so that on each
tick it forwards each per-subsystem property of the goal state into the
matching subsystem's writable field.

`RobotState` is pre-declared in this file with the correct setpoints from
Task 1. The subsystems' `tick()` is already wired into `periodic()`.
