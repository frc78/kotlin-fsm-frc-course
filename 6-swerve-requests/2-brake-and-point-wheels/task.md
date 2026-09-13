# Brake and Point Wheels

Two requests hold the robot still. Both drive zero. They differ in what the
modules do while they wait.

## SwerveDriveBrake: the X-lock

`SwerveDriveBrake` turns each module so the wheels point inward in an X shape
and applies zero drive force. The robot then resists a shove from another
robot far better than zero-velocity field-centric does.

```
   /     \
  /       \
  \       /
   \     /
```

It is a separate request type from "drive at zero velocity" because the two
mean different things:

- `FieldCentric(0.0, 0.0, 0.0)`: modules stay in their last orientation and
  drive zero. Push the robot and it slides.
- `SwerveDriveBrake`: modules turn to the X pattern and drive zero. Push the
  robot and the wheel geometry resists.

In this stub, `SwerveDriveBrake` is a `data object`. You reference it by
name, with no `()` and no `with*` calls, because the stub has nothing to
configure. In real Phoenix6 it is a class you construct with
`SwerveDriveBrake()`. It accepts a little configuration
(`withDriveRequestType`, `withSteerRequestType`), but you almost always use it
as is.

## PointWheelsAt: pre-rotate without driving

`PointWheelsAt` turns every module to one direction and drives zero. The robot
does not move.

Swerve modules cannot pivot instantly. **Slewing** is the time a module takes
to turn to a new direction, about 100 ms. If you are about to command a hard
move from rest, point the wheels first so the burst starts clean. Auto
routines aim the wheels along the first path's start direction before the
match starts. The driver almost never triggers this request directly.

`PointWheelsAt` is constructed empty and configured with one setter:

| Setter                | Argument                                          |
|-----------------------|---------------------------------------------------|
| `withModuleDirection` | direction for every module, a `Rotation2d`        |

Build the `Rotation2d` from degrees with `Rotation2d.fromDegrees(...)`. It
lives in `frc.stubs.geometry`. Real Phoenix6 takes a `Rotation2d` here too.

## Which one

| Request            | Use it when                                           | Robot moves? |
|--------------------|-------------------------------------------------------|--------------|
| `SwerveDriveBrake` | you must hold position against a push                 | no           |
| `PointWheelsAt`    | you are about to burst in a known direction from rest | no           |

## Your task

Implement two functions in `src/Drive.kt`. Each sends one request to the
drivetrain.

| Function                                | Request sent                                       |
|-----------------------------------------|----------------------------------------------------|
| `applyBrake()`                          | `SwerveDriveBrake`                                 |
| `pointWheels(directionDegrees: Double)` | `PointWheelsAt` with `moduleDirection` at that angle |
