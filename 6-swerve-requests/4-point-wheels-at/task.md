# PointWheelsAt — Pre-rotate Without Driving

`PointWheelsAt` rotates every module to a given direction without applying
drive force. The robot doesn't move — the modules just turn to face the
requested heading.

```kotlin
PointWheelsAt()
    .withModuleDirection(45.0)   // degrees
```

## When to use it

- **Before a quick burst.** Swerve modules can't pivot instantly. If you're
  about to command a hard sideways move, point the wheels first so the burst
  starts cleanly without a 100 ms slewing delay.
- **Pre-staging an auto.** Aim the wheels at the first auto path's tangent
  before the match starts.
- **As part of a "go to here, fast" sequence.** Some teams point wheels for a
  fraction of a second before applying full velocity, especially when the
  robot was previously at rest.

The driver almost never triggers this directly. It's a building block used by
auto routines and superstructure logic.

## The builder

`PointWheelsAt` is constructed empty and configured with a single
setter, `withModuleDirection`. In this stub it takes a `Double` (degrees);
in real Phoenix6 it takes a `Rotation2d`, e.g.
`withModuleDirection(Rotation2d.fromDegrees(45.0))`.

## Your task

Implement `pointWheels(directionDegrees)`. It should construct a
`PointWheelsAt`, set its module direction to the parameter value, and
hand it to `drivetrain.setControl(...)`.
