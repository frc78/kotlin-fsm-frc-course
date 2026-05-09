# Field-Centric Driving

Phoenix6's swerve API works through **request objects**. Instead of calling
`drivetrain.drive(vx, vy, omega)` directly, you build a `SwerveRequest` and
hand it to `drivetrain.setControl(...)`. The drivetrain interprets the request
each tick.

There are several request types — each one expresses a different *intent*.
We'll start with the most common: **`FieldCentric`**.

## What field-centric means

The driver's joystick maps to the **field's** axes, not the robot's.

- Pushing forward → robot moves toward the opposite alliance wall, no matter
  which way the robot is facing.
- Pushing right → robot moves to the field's right.

The drivetrain handles the rotation conversion using the gyro. This is what
your driver wants 99% of the time once they're comfortable.

(The 1% case — when the gyro isn't trustworthy — is handled by `RobotCentric`,
which is the next task.)

## The builder pattern

`FieldCentric` uses a fluent builder API. You construct an empty
`FieldCentric()`, then chain `with*` setters to populate its fields:

| Setter             | Argument units                                     |
|--------------------|----------------------------------------------------|
| `withVelocityX`    | forward in field frame, m/s                        |
| `withVelocityY`    | left in field frame, m/s                           |
| `withRotationalRate` | counterclockwise rotation, rad/s                 |

Each `with*` call returns a `FieldCentric` you can keep chaining on. The
final expression is the request you pass to `drivetrain.setControl(...)`.

> **Note on real Phoenix6:** The actual CTRE classes mutate themselves on each
> `with*` call and return `this`. This stub returns a copy each time. The call
> shape is identical — your code reads the same — but the underlying
> semantics differ. In real code, you typically create one `FieldCentric` as a
> field and reuse it; here, you can make a fresh one each tick without any
> performance worry.

## Your task

Open `src/Drive.kt`. Implement `teleopDrive(vx, vy, omega)`:

1. Build a `FieldCentric` request whose `velocityX` is `vx`, `velocityY`
   is `vy`, and rotational rate is `omega`.
2. Apply that request to `drivetrain`.

That's the entire body of the function — three chained `with*` calls
followed by a single `setControl(...)`.

## Aside: alliance flipping

In real FRC, "field-centric forward" depends on the alliance. The driver
station's "forward" is toward the opposing alliance — which is +X on blue
side, -X on red side. CTRE has `setOperatorPerspectiveForward()` for this.
You can ignore it for now; the stub doesn't model alliance.
