# SwerveDriveBrake: The X-Lock

`SwerveDriveBrake` is the **X-pattern lock**. Each module turns so the wheels
point inward in an X shape and apply zero drive force. The robot then resists
a shove from another robot far better than zero-velocity field-centric does.

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

## When to use it

- Defensive play, while an opponent pushes on you.
- Holding pose at the end of auto while you wait for the buzzer.
- Holding still while a flywheel spins up before a shot.

## The builder (or lack of it)

In this stub, `SwerveDriveBrake` is a `data object`. You reference it by
name, with no `()` and no `with*` calls, because the stub has nothing to
configure. In real Phoenix6 it is a class you construct with
`SwerveDriveBrake()`. It accepts a little configuration
(`withDriveRequestType`, `withSteerRequestType`), but you almost always use it
as is.

## Your task

Implement `applyBrake()`. The drivetrain must receive the `SwerveDriveBrake`
request. One call, no construction.
