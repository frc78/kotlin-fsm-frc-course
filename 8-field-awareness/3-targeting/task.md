# Targeting Math: Connecting Pose to Swerve

In lesson 6 task 3 you wrote `aimWhileDriving(vx, vy, targetDegrees)`. It
builds a `FieldCentricFacingAngle` request for a fixed heading. On a real
robot the target heading is not fixed. It depends on where the robot is and
where the goal is. As the robot moves, the angle to the goal changes.

This task connects the pose math of task 1 to the swerve API of lesson 6.

## The inputs

| Input          | Type             | Meaning                                   |
|----------------|------------------|-------------------------------------------|
| `robotPose`    | `Pose2d`         | where the robot is, from the drivetrain   |
| `goalPosition` | `Translation2d`  | where the goal is, in the field frame     |
| `vx`, `vy`     | `Double`         | the driver's field-relative speeds, m/s   |

## The output

A `FieldCentricFacingAngle` with:

| Setter                    | Value                                                    |
|---------------------------|----------------------------------------------------------|
| `withVelocityX(...)`      | `vx`, unchanged                                          |
| `withVelocityY(...)`      | `vy`, unchanged                                          |
| `withTargetDirection(...)`| the heading from the robot to the goal, as a `Rotation2d` |

`headingFromTo` from task 1 gives that heading as a `Rotation2d`, so it goes
into the setter as it is.

| Robot pose             | Goal       | Target direction |
|------------------------|------------|------------------|
| `(0, 0)` facing 45°    | `(5, 0)`   | 0°               |
| `(0, 0)` facing 0°     | `(0, 5)`   | 90°              |
| `(1, 1)` facing 0°     | `(4, 5)`   | 53.13°           |
| `(5, 0)` facing 0°     | `(0, 0)`   | 180°             |

The robot's own heading does not appear in the result. The heading
controller inside the drivetrain turns the robot from where it faces to the
target.

## Angle wrap

Real WPILib `Rotation2d` stores an angle as a cosine and a sine, so 370° and
10° are the same value. This stub stores raw radians and does not wrap.
`headingFromTo` returns values in the range -180° to 180° because it uses
`atan2` inside, so the tests in this task do not hit the difference.

## Frames inside the drivetrain

A `ChassisSpeeds` is the robot-frame velocity `(vxMetersPerSecond,
vyMetersPerSecond, omegaRadiansPerSecond)` that the kinematics turn into
module speeds. `FieldCentric` requests convert your field-frame sticks into
it inside the drivetrain, so you do not write that math.

## Your task

Implement `aimAtGoal(robotPose, goalPosition, vx, vy)` in `src/Targeting.kt`.
It returns a `SwerveRequest`. The tests check that it is a
`FieldCentricFacingAngle` with the three fields above.

## In real code

```kotlin
val request = aimAtGoal(
    robotPose = drivetrain.state.pose,   // task 4
    goalPosition = blueAllianceGoalPosition,
    vx = joystick.leftX * MAX_SPEED,
    vy = joystick.leftY * MAX_SPEED,
)
drivetrain.setControl(request)
```

The drivetrain reports where you are, this function turns that into a
request, and the drivetrain executes the request.
