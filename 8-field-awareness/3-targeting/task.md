# Targeting Math: Connecting Pose to Swerve

In Lesson 6 you wrote `aimWhileDriving(vx, vy, targetDegrees)` — a
`FieldCentricFacingAngle` request that aims at a static angle. But on a real
robot, the target heading isn't static: it depends on **where the robot is**
and **where the goal is**. As the robot moves around, the angle to the goal
changes.

This task connects pose math (Lesson 8 task 1) to the swerve API (Lesson 6).
The result is one of the most-used utility functions on a competitive FRC
robot.

## The math

To face a goal at field position `goalPosition`, the robot needs to point in
the direction *from its own location* toward `goalPosition`:

```kotlin
val targetHeading = (goalPosition - robotPose.translation).getAngle()
```

That heading is a `Rotation2d`. To pass it to `FieldCentricFacingAngle`'s
`withTargetDirection(degrees)`, take `.degrees` from it:

```kotlin
.withTargetDirection(targetHeading.degrees)
```

The driver still controls translation via `vx` and `vy`. The heading PID
handles rotation.

## Your task

Implement `aimAtGoal(robotPose, goalPosition, vx, vy)` in `src/Targeting.kt`.
It should return a `SwerveRequest` (specifically a `FieldCentricFacingAngle`)
configured with the driver's translation and an auto-aimed target heading.

## Putting it together in real code

In a real `teleopPeriodic`:

```kotlin
val request = aimAtGoal(
    robotPose = poseEstimator.currentPose,   // from Lesson 8 task 5
    goalPosition = blueAllianceGoalPosition,
    vx = joystick.leftX * MAX_SPEED,
    vy = joystick.leftY * MAX_SPEED,
)
drivetrain.setControl(request)
```

That's three lines: pose estimator gives you where you are, this function
turns it into a request, drivetrain executes. The whole "aim at the goal
while driving" feature is captured in those three lines because each layer
does its job.
