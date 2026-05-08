# Pose Estimator: Odometry Updates

A `PoseEstimator` tracks where the robot believes it is. The estimator
combines two streams of input:

1. **Odometry** — wheel rotations + gyro yaw. Always available, accumulates
   error over time (drift).
2. **Vision** — AprilTag detections. Intermittently available, corrects
   drift but can be noisy.

This task is just the odometry side. Vision is the next task.

## What an odometry update means

Each periodic, the swerve modules report how far each wheel has rolled. The
kinematics module turns those wheel deltas into a single robot delta —
"forward `dx` meters, sideways `dy` meters, rotated `dθ` radians" since last
tick. The estimator integrates that delta into its current pose.

```kotlin
estimator.updateWithOdometry(
    translationDelta = Translation2d(dx, dy),     // robot-frame
    rotationDelta = Rotation2d.fromRadians(dθ),
)
```

`translationDelta` is in the **robot's body frame** at the start of the
tick. The estimator rotates it into the field frame using the current
heading, then adds. (The stub does this rotation for you — call sites just
hand over the body-frame delta.)

## Your task

Implement `applyOdometry(estimator, forwardMeters, turnDegrees)` in
`src/Odometry.kt`. Treat `forwardMeters` as motion along the robot's +X axis
(forward in the body frame), with no sideways component. `turnDegrees` is
the change in heading.

```kotlin
estimator.updateWithOdometry(
    Translation2d(forwardMeters, 0.0),
    Rotation2d.fromDegrees(turnDegrees),
)
```

## What the test exercises

A canonical odometry-replay scenario:

1. Start at origin facing 0°.
2. Drive forward 1 m → expect `(1, 0)` facing 0°.
3. Turn 90° → expect `(1, 0)` facing 90°.
4. Drive forward 1 m → expect `(1, 1)` facing 90°.

If your function is right, this trace falls out. Watch the body-frame vs
field-frame mental model — that's what trips most students.
