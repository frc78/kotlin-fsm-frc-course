# ChassisSpeeds and Frame Conversion

`ChassisSpeeds` is a velocity vector for the whole drivetrain:

```kotlin
data class ChassisSpeeds(
    val vxMetersPerSecond: Double,
    val vyMetersPerSecond: Double,
    val omegaRadiansPerSecond: Double,
)
```

It's what kinematics modules consume to produce per-module setpoints. There's
*one* gotcha: `ChassisSpeeds` is **always interpreted in the robot's frame**.

So if a driver wants to push the robot "north on the field" at 1 m/s,
regardless of which way the robot is facing, you can't pass `(1.0, 0.0,
0.0)` directly — that's "1 m/s along the robot's forward axis," which only
matches "field north" if the robot happens to be facing north right now.

## The conversion

`ChassisSpeeds` exposes a static factory that does the rotation for
you: `ChassisSpeeds.fromFieldRelativeSpeeds`. It takes the two
field-relative linear velocities, the desired angular velocity, and
the robot's current heading, and returns a `ChassisSpeeds` ready to be
consumed in the robot frame.

Sanity check: if the robot is facing 0° (east), feeding in a field
velocity of 1 m/s east leaves the robot-frame `vx` at 1.0 and `vy` at
0.0. If it's facing 90°, the same field velocity translates to
`vx = 0.0, vy = -1.0` in the robot's frame (because "field east" is
now "robot right," which is `-y` for a robot that treats `+y` as left).

## Why you'd write this yourself

Phoenix6's `FieldCentric` request handles the conversion internally — you
typically don't need to do it manually. But you *will* need it for:

- Auto routines that compute desired field-relative motion.
- AdvantageKit replay where you log `ChassisSpeeds` and want to know what the
  field-frame intent was.
- Custom kinematics or non-CTRE swerve stacks.

## Your task

Implement `fieldToRobotSpeeds(vxField, vyField, omega, robotAngle)`.
It's a one-liner that delegates to the static factory described above,
forwarding the four parameters straight through.
