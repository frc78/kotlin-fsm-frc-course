# PointWheelsAt: Pre-rotate Without Driving

`PointWheelsAt` turns every module to a given direction without drive force.
The robot does not move. The modules turn to face the requested heading.

## When to use it

- **Before a quick burst.** Swerve modules cannot pivot instantly. Slewing is
  the time a module takes to turn to a new direction, about 100 ms. If you
  are about to command a hard sideways move, point the wheels first so the
  burst starts clean.
- **Pre-staging an auto.** Aim the wheels along the first auto path's tangent
  (the direction the path starts in) before the match starts.
- **In a "go to here, fast" sequence.** Some teams point wheels for a fraction
  of a second before they apply full velocity from rest.

The driver almost never triggers this directly. Auto routines and
superstructure logic use it as a building block.

## The builder

`PointWheelsAt` is constructed empty and configured with one setter:

| Setter                | Argument                                     |
|-----------------------|----------------------------------------------|
| `withModuleDirection` | direction for every module, degrees (stub)   |

In real Phoenix6 the setter takes a `Rotation2d`, for example
`Rotation2d.fromDegrees(45.0)`.

## Your task

Implement `pointWheels(directionDegrees)`. The drivetrain must receive one
`PointWheelsAt` request whose `moduleDirection` is `directionDegrees`.
