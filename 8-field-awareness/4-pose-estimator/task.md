# Pose from the Drivetrain

A pose estimate tracks where the robot believes it is. It combines two
inputs:

1. **Odometry**: wheel rotations plus gyro heading. Always available. Error
   builds up over time. This is called drift.
2. **Vision**: AprilTag detections. Available only when a tag is in view.
   Corrects drift, but a single reading can be noisy.

## The drivetrain owns the estimate

On a CTRE swerve robot the `SwerveDrivetrain` runs the pose estimator. You
do not build one. You ask the drivetrain:

| You want                        | Call                                                              |
|---------------------------------|-------------------------------------------------------------------|
| where am I                      | `drivetrain.state.pose`                                           |
| tell it where auto starts       | `drivetrain.resetPose(Pose2d)`                                    |
| feed a camera reading           | `drivetrain.addVisionMeasurement(pose, timestampSeconds)` (task 5) |

Odometry runs inside the drivetrain on every loop. You never call it. Real
code reads `getState().Pose`. In Kotlin that is `state.pose`.

## What one odometry step computes

Each loop the swerve modules report how far each wheel rolled. The swerve
kinematics turn the four wheel distances into one robot motion since the
last tick: forward, sideways, and a heading change. That motion is in the
**robot frame**. The estimator rotates it into the field frame by the
current heading, then adds it to the pose.

You will write that step once, as a pure function, to see what the
drivetrain computes on every loop.

## Your task

Implement `poseAfterOdometry(current, forwardMeters, turnDegrees): Pose2d`
in `src/Odometry.kt`.

| Parameter       | Meaning                                                     |
|-----------------|-------------------------------------------------------------|
| `current`       | the pose at the start of the tick                           |
| `forwardMeters` | motion along the robot's +X axis, at the start heading      |
| `turnDegrees`   | heading change during the tick, in degrees                  |

Motion is along the robot's +X only. Apply the heading change after the
translation.

## What the test does

| Step                    | Pose after the step   |
|-------------------------|-----------------------|
| start                   | `(0, 0)` facing 0°    |
| drive forward 1 m       | `(1, 0)` facing 0°    |
| turn 90°                | `(1, 0)` facing 90°   |
| drive forward 1 m       | `(1, 1)` facing 90°   |

The last row shows why the frame matters. The robot faces 90°, so "forward"
is along field +Y.
