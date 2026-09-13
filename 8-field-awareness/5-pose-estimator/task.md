# Pose Estimator: Odometry Updates

A `PoseEstimator` tracks where the robot believes it is. It combines two
inputs:

1. **Odometry**: wheel rotations plus gyro heading. Always available. Error
   builds up over time. This is called drift.
2. **Vision**: AprilTag detections. Available only when a tag is in view.
   Corrects drift, but a single reading can be noisy.

This task is the odometry side. Vision is the next task.

## What an odometry update is

Each periodic, the swerve modules report how far each wheel rolled. The
swerve kinematics turn the four wheel distances into one robot motion since
the last tick: forward `dx` meters, sideways `dy` meters, and a heading
change. The estimator adds that motion to its current pose:

`PoseEstimator.updateWithOdometry(translationDelta: Translation2d, rotationDelta: Rotation2d)`

`translationDelta` is in the **robot frame** at the start of the tick. The
estimator rotates it into the field frame with the current heading and then
adds it. You hand over the robot-frame delta only.

## Your task

Implement `applyOdometry(estimator, forwardMeters, turnDegrees)` in
`src/Odometry.kt`.

| Parameter       | Meaning                                                  |
|-----------------|----------------------------------------------------------|
| `forwardMeters` | motion along the robot's +X axis. No sideways component. |
| `turnDegrees`   | the change in heading since the last tick, in degrees    |

The function calls `updateWithOdometry` once with the two values converted to
the types it takes.

## What the test does

| Step                    | Pose after the step   |
|-------------------------|-----------------------|
| start                   | `(0, 0)` facing 0°    |
| drive forward 1 m       | `(1, 0)` facing 0°    |
| turn 90°                | `(1, 0)` facing 90°   |
| drive forward 1 m       | `(1, 1)` facing 90°   |

The last row shows why the frame matters. The robot faces 90°, so "forward"
is along field +Y.
