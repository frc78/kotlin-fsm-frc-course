# Field-Centric and Robot-Centric Driving

Phoenix6's swerve API works through **request objects**. You do not call
`drivetrain.drive(vx, vy, omega)`. You build a `SwerveRequest` and hand it to
`drivetrain.setControl(...)`. The drivetrain reads the request each tick.

Each request type expresses a different intent. This task uses the two you
will send most: `FieldCentric` and `RobotCentric`. They have the same three
setters. They differ in the frame the velocities are read in.

## The frame convention

WPILib and Phoenix6 use one convention for every velocity and angle:

```
        far alliance wall
   +-----------------------+
   |          +X ^         |
   |             |         |
   |   +Y <------+         |   counterclockwise = positive
   |                       |
   +-----------------------+
        your driver station
```

- **+X** is forward, toward the far alliance wall.
- **+Y** is left.
- **Counterclockwise is positive** for every rotation and angle.

Every `with*` setter in this lesson follows that convention.

## Field-centric

The driver's joystick maps to the **field's** axes, not the robot's.

- Push forward: the robot moves toward the far alliance wall, no matter which
  way the robot faces.
- Push left: the robot moves to the field's left.

The drivetrain uses the gyro to convert field axes to wheel commands. Most
drivers use field-centric for the whole match.

## Robot-centric

`RobotCentric` reads the same velocities in the **robot's** frame:

- `velocityX` = forward, out the front of the robot.
- `velocityY` = left, out the left side of the robot.

Push forward: the robot moves the way it points. If it points backward,
"forward" on the joystick moves the robot toward the driver.

Robot-centric matters in a few situations:

- **The gyro is unreliable:** after a hard collision, on a tipping game
  element, or after a roll-over with no re-zero.
- **Auto routines relative to the robot:** "Back up half a meter" is a
  robot-centric instruction.
- **Fine alignment:** some drivers prefer robot-centric for the last 30 cm into
  a scoring position.
- **Climb and endgame:** when the robot is partly engaged with a field
  element, its yaw (heading angle) can swing in ways the gyro does not track.

A good driver-station setup has a button that toggles the mode.

## The builder pattern

Both requests use a fluent builder API. You construct an empty request, then
chain `with*` setters to fill its fields:

| Setter               | Argument units                     |
|----------------------|------------------------------------|
| `withVelocityX`      | forward (+X), m/s                  |
| `withVelocityY`      | left (+Y), m/s                     |
| `withRotationalRate` | counterclockwise rotation, rad/s   |

Each `with*` call returns a request you can keep chaining on. The final
expression is the request you pass to `drivetrain.setControl(...)`. Both
types are subclasses of `SwerveRequest`, so `setControl` accepts either one
without a cast.

Both requests also have `withDeadband(...)` and `withRotationalDeadband(...)`.
Task 4 sets them.

> **Note on real Phoenix6:** The real CTRE classes change themselves on each
> `with*` call and return `this`. This stub returns a copy each time. The
> call shape is identical. In real code you create one request as a field
> and reuse it every tick.

## Your task

Open `src/Drive.kt`. Implement `teleopDrive(vx, vy, omega, robotRelative)`.
The drivetrain must receive one request per call:

| `robotRelative` | Request the drivetrain receives          |
|-----------------|------------------------------------------|
| `false`         | `FieldCentric` with `vx`, `vy`, `omega`  |
| `true`          | `RobotCentric` with `vx`, `vy`, `omega`  |

| Parameter | Request field    |
|-----------|------------------|
| `vx`      | `velocityX`      |
| `vy`      | `velocityY`      |
| `omega`   | `rotationalRate` |

Call `setControl(...)` once per call to `teleopDrive`.

## Aside: alliance flipping

In a real match, "forward" for the red driver points along -X. CTRE has
`setOperatorPerspectiveForward()` for this. The stub does not model alliance.
