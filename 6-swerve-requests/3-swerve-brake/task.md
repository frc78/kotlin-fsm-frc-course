# SwerveDriveBrake — The X-Lock

`SwerveDriveBrake` is the **X-pattern lock**. Each module rotates so the
wheels point inward in an X shape and apply zero drive force. The result: the
robot resists being shoved by another robot far better than zero-velocity
field-centric would.

```
   /     \
  /       \
  \       /
   \     /
```

It's its own request type *separate from* "drive at zero velocity" because the
two mean different things:

- `FieldCentric(0.0, 0.0, 0.0)` — modules stay in their last orientation,
  drives zero. Push the robot — it slides.
- `SwerveDriveBrake` — modules rotate to the X pattern, drives zero. Push the
  robot — modules resist by virtue of geometry alone.

## When to use it

- Defensive play (waiting for an opponent to disengage from a contact).
- End-of-auto pose-hold while waiting for the buzzer.
- Pre-shot stability while a flywheel spins up.
- Any "I really do not want to slide right now" moment.

## The builder (or lack of it)

`SwerveDriveBrake` has no parameters. It's a `data object` — a singleton
you reference *by name*, with no `()` and no `with*` calls. There's only
one possible brake request, so there's nothing to configure.

## Your task

Implement `applyBrake()`. It should hand the singleton brake request to
`drivetrain.setControl(...)`. One call, no construction.
