# Field-Centric Driving

Phoenix6's swerve API works through **request objects**. You do not call
`drivetrain.drive(vx, vy, omega)`. You build a `SwerveRequest` and hand it to
`drivetrain.setControl(...)`. The drivetrain reads the request each tick.

Each request type expresses a different intent. The most common one is
**`FieldCentric`**.

## What field-centric means

The driver's joystick maps to the **field's** axes, not the robot's.

- Push forward: the robot moves toward the far alliance wall, no matter which
  way the robot faces.
- Push left: the robot moves to the field's left.

The drivetrain uses the gyro to convert field axes to wheel commands. Most
drivers use field-centric for the whole match. `RobotCentric`, in the next
task, covers the cases where the gyro cannot be trusted.

## The frame convention

WPILib and Phoenix6 use one convention for every velocity and angle:

- **+X** is forward, toward the far alliance wall.
- **+Y** is left.
- **Counterclockwise is positive** for every rotation and angle.

Every `with*` setter in this lesson follows that convention.

## The builder pattern

`FieldCentric` uses a fluent builder API. You construct an empty
`FieldCentric()`, then chain `with*` setters to fill its fields:

| Setter               | Argument units                     |
|----------------------|------------------------------------|
| `withVelocityX`      | forward in field frame, m/s        |
| `withVelocityY`      | left in field frame, m/s           |
| `withRotationalRate` | counterclockwise rotation, rad/s   |

Each `with*` call returns a `FieldCentric` you can keep chaining on. The
final expression is the request you pass to `drivetrain.setControl(...)`.

> **Note on real Phoenix6:** The real CTRE classes change themselves on each
> `with*` call and return `this`. This stub returns a copy each time. The
> call shape is identical. In real code you create one `FieldCentric` as a
> field and reuse it every tick. Real `FieldCentric` and `RobotCentric` also
> have `withDeadband(...)` and `withRotationalDeadband(...)`. A deadband is
> the small stick movement the request ignores, so a stick that rests a
> little off center does not creep the robot. The stub omits both setters.

## Your task

Open `src/Drive.kt`. Implement `teleopDrive(vx, vy, omega)`. The drivetrain
must receive one `FieldCentric` request with these fields:

| Parameter | Request field    |
|-----------|------------------|
| `vx`      | `velocityX`      |
| `vy`      | `velocityY`      |
| `omega`   | `rotationalRate` |

Call `setControl(...)` once per call to `teleopDrive`.

## Aside: alliance flipping

In a real match, "forward" for the red driver points along -X. CTRE has
`setOperatorPerspectiveForward()` for this. The stub does not model alliance.
