# ChassisSpeeds and Frame Conversion

`ChassisSpeeds` is a velocity for the whole drivetrain:

```kotlin
data class ChassisSpeeds(
    val vxMetersPerSecond: Double,
    val vyMetersPerSecond: Double,
    val omegaRadiansPerSecond: Double,
)
```

The swerve kinematics turn a `ChassisSpeeds` into a speed and angle for each
module. A `ChassisSpeeds` is **always in the robot frame**. `vx` is along the
robot's nose. `vy` is to the robot's left.

A driver thinks in the field frame. "Drive along field +X at 1 m/s" is only
`ChassisSpeeds(1.0, 0.0, 0.0)` when the robot also faces +X. When the robot
faces 90°, field +X is to the robot's right, so the same motion is
`ChassisSpeeds(0.0, -1.0, 0.0)`.

## The conversion

`ChassisSpeeds` has a companion factory that rotates field speeds into the
robot frame:

`ChassisSpeeds.fromFieldRelativeSpeeds(vxField, vyField, omega, robotAngle)`

| Robot heading | Field speeds `(vx, vy)` | Robot-frame `(vx, vy)` |
|---------------|-------------------------|------------------------|
| 0°            | `(1.0, 0.5)`            | `(1.0, 0.5)`           |
| 90°           | `(1.0, 0.0)`            | `(0.0, -1.0)`          |
| 180°          | `(1.0, 0.5)`            | `(-1.0, -0.5)`         |

`omega` is the same in both frames. A rotation rate does not depend on which
way the robot faces.

Phoenix6's `FieldCentric` request does this conversion inside the drivetrain.
You write it yourself when an autonomous routine computes a field-relative
motion and you need the robot-frame speeds.

## Your task

Implement `fieldToRobotSpeeds(vxField, vyField, omega, robotAngle)` in
`src/Speeds.kt`. It returns the `ChassisSpeeds` in the robot frame.
