# The Climber Reads the Superstructure

The climber is the smallest FSM in the course, on purpose. It shows the one
rule that makes three peer machines cooperate: a machine that depends on
another one **reads** that machine's state. Nobody commands the climber. It
watches the superstructure and acts when the pose is right.

## Hardware

One `TalonFX` (`canId = 16`) turns a lead screw. The screw has 12 threads per
inch, so 6 inches of travel is `12 * 6 = 72.0` rotations. Both states carry
their own `PositionVoltage` request, the enum-with-properties pattern from
lesson 1.

## Transitions

| Current     | Condition                                                                 | Next       |
|-------------|---------------------------------------------------------------------------|------------|
| `RETRACTED` | `SuperStructure.state == Pose.FULLY_CLIMBED && SuperStructure.atPosition` | `EXTENDED` |
| `EXTENDED`  | none                                                                      | stay       |

`SuperStructure.atPosition` is the same property the intake read in task 4.
`FULLY_CLIMBED` alone is not enough: the arm is still swinging down to 5
degrees for several ticks after the pose changes. The climber waits until the
superstructure reports that it has arrived.

There is no way back. A climber that retracts under load drops the robot.

## Actions

| State       | Motor                     |
|-------------|---------------------------|
| `RETRACTED` | `PositionVoltage(0.0)`    |
| `EXTENDED`  | `PositionVoltage(72.0)`   |

Every tick, `stateActions()` sends the current state's `control` request to
`motor`.

## Your task

Open `src/Climber.kt`. Implement `stateTransitions()` and `stateActions()`.
`SuperStructure`, `Intake`, and `Pose` are complete in this task; read them,
do not change them.
