# Translation2d and Rotation2d

WPILib's geometry types are the foundation everything else stands on. Once
you've internalized them, the rest of field awareness — pose estimation,
vision integration, swerve aiming — is just stacking these primitives.

## `Translation2d`

A 2D point or offset. Has `x` and `y` (both meters by FRC convention) and
operator-overloaded math:

```kotlin
val a = Translation2d(1.0, 0.0)
val b = Translation2d(4.0, 0.0)

a + b                // Translation2d(5.0, 0.0)
b - a                // Translation2d(3.0, 0.0)
a.getDistance(b)     // 3.0
a.getNorm()          // 1.0  (distance from origin)
a.getAngle()         // Rotation2d at 0 rad (a points along +x)
```

`rotateBy(rotation)` rotates a translation around the origin. Useful for
"this offset is in the robot's body frame; what is it in the field frame?"

## `Rotation2d`

An angle. Stored internally in radians; expose either with `.radians` or
`.degrees`. Construct via `Rotation2d.fromDegrees(...)` or
`Rotation2d.fromRadians(...)`.

Operator-overloaded: `r1 + r2`, `r1 - r2`, `-r1`. (Real WPILib's `Rotation2d`
also has `cos()`/`sin()` accessors — for this stub, just multiply by
`fromDegrees(...)` or do the trig yourself if you need to.)

## FRC field convention

- **+X** is forward (toward the opposing alliance for blue, toward your own
  for red — and FRC's convention is "+X is always toward red, regardless of
  which alliance you're on" until you apply alliance flipping).
- **+Y** is to the left from blue's perspective.
- **0°** is along +X. **90°** is along +Y (counterclockwise positive).

## Your task

Implement three functions in `src/Geometry.kt`:

1. **`distanceBetween(a, b)`** — return how far apart two field positions are.
2. **`headingFromTo(start, end)`** — return the heading (Rotation2d) you'd
   need to face to point from `start` toward `end`.
3. **`rotatePoint(point, by)`** — return `point` rotated around the origin by
   the given angle.

Each is a one-liner using methods on `Translation2d` shown in the
operator-overloaded math section above.
