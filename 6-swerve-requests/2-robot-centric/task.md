# Robot-Centric Driving

`RobotCentric` is the same shape as `FieldCentric` — `withVelocityX`,
`withVelocityY`, `withRotationalRate` — but the velocities are interpreted in
the **robot's** frame, not the field's:

- `velocityX` = forward, **out the front of the robot**.
- `velocityY` = left, **out the left side of the robot**.

Push forward → robot moves the way it's pointing. If it's pointing backward,
"forward" on the joystick moves the robot toward the driver.

## When to use it

Most of the time, drivers prefer field-centric. Robot-centric matters in a
handful of specific situations:

- **Gyro is unreliable.** During a hard collision, on a tippy game element, or
  if the gyro hasn't been re-zeroed after a roll-over.
- **Auto routines that drive relative to the robot's current pose.** "Back up
  half a meter" is a robot-centric instruction.
- **Driver preference for fine alignment.** Some drivers prefer
  robot-centric for the last 30 cm into a scoring position because there's no
  alliance flipping or gyro drift to think about.
- **Climb / endgame mechanisms.** Once you're partly engaged with a stage or
  cage, the robot's yaw can swing in ways the gyro doesn't fully capture.

A good driver-station setup exposes a button that toggles the mode.

## The builder

```kotlin
RobotCentric()
    .withVelocityX(vx)
    .withVelocityY(vy)
    .withRotationalRate(omega)
```

Identical to `FieldCentric` other than the type name.

## Your task

Implement `teleopDrive(vx, vy, omega, robotRelative)`:

- If `robotRelative` is `true`, apply a `RobotCentric` request.
- Otherwise, apply a `FieldCentric` request.

Use `if (...) ... else ...` and call `setControl` once.

## Hint

Both `FieldCentric` and `RobotCentric` are subclasses of `SwerveRequest`, so
you can apply either to `drivetrain.setControl(...)` without any cast — they
each *are* a `SwerveRequest`.
