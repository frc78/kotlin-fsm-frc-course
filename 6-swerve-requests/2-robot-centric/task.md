# Robot-Centric Driving

`RobotCentric` has the same setters as `FieldCentric`: `withVelocityX`,
`withVelocityY`, `withRotationalRate`. The drivetrain reads the velocities in
the **robot's** frame, not the field's:

- `velocityX` = forward, out the front of the robot.
- `velocityY` = left, out the left side of the robot.

Push forward: the robot moves the way it points. If it points backward,
"forward" on the joystick moves the robot toward the driver.

## When to use it

Most drivers prefer field-centric. Robot-centric matters in a few situations:

- **The gyro is unreliable.** After a hard collision, on a tipping game
  element, or after a roll-over with no re-zero.
- **Auto routines relative to the robot.** "Back up half a meter" is a
  robot-centric instruction.
- **Fine alignment.** Some drivers prefer robot-centric for the last 30 cm into
  a scoring position.
- **Climb and endgame.** When the robot is partly engaged with a field
  element, its yaw (heading angle) can swing in ways the gyro does not track.

A good driver-station setup has a button that toggles the mode.

## Your task

Implement `teleopDrive(vx, vy, omega, robotRelative)`:

| `robotRelative` | Request the drivetrain receives          |
|-----------------|------------------------------------------|
| `true`          | `RobotCentric` with `vx`, `vy`, `omega`  |
| `false`         | `FieldCentric` with `vx`, `vy`, `omega`  |

Call `setControl(...)` once per call. An `if/else` whose result is the request
value is a clean way to write this.

## Hint

Both `FieldCentric` and `RobotCentric` are subclasses of `SwerveRequest`. You
can pass either one to `drivetrain.setControl(...)` without a cast.
