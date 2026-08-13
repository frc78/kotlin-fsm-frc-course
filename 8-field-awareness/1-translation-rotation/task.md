# Translation2d and Rotation2d

WPILib's geometry types are the foundation everything else stands on. Once
you've internalized them, the rest of field awareness — pose estimation,
vision integration, swerve aiming — is just stacking these primitives.

## `Translation2d`

A 2D point or offset. Has `x` and `y` (both meters by FRC convention) and
operator-overloaded math. The operations available on a `Translation2d`:

- `+` and `-` between two `Translation2d`s (component-wise add / subtract).
- `getDistance(other)` — Euclidean distance to another `Translation2d`.
- `getNorm()` — distance from the origin.
- `getAngle()` — the angle of this point as a vector from the origin,
  returned as a `Rotation2d`.
- `rotateBy(rotation)` — rotates the point around the origin by the
  given angle. Useful for "this offset is in the robot's body frame;
  what is it in the field frame?"

## `Rotation2d`

An angle. Stored internally in radians; expose either with `.radians` or
`.degrees`. Construct via `Rotation2d.fromDegrees(...)` or
`Rotation2d.fromRadians(...)`.

Operator-overloaded: `r1 + r2`, `r1 - r2`, `-r1`. (Real WPILib's `Rotation2d`
also has `cos()`/`sin()` accessors — this stub doesn't. If you ever need
the components of an angle, use `Translation2d.rotateBy(...)` or do
`kotlin.math` trig on `.radians` yourself; none of this task's functions
require it.)

## FRC field convention

- **+X** is forward (toward the opposing alliance for blue, toward your own
  for red — and FRC's convention is "+X is always toward red, regardless of
  which alliance you're on" until you apply alliance flipping).
- **+Y** is to the left from blue's perspective.
- **0°** is along +X. **90°** is along +Y (counterclockwise positive).

What is alliance flipping? Field-element coordinates are usually published
for the blue alliance; when you're on red, you mirror (or rotate) those
blue-alliance coordinates around the field center so they describe the same
spot from your side. Real team code does this with small helper utilities.
This course leaves the implementation out of scope — every position in these
tasks is already in one consistent field frame.

## Your task

Implement three functions in `src/Geometry.kt`:

1. **`distanceBetween(a, b)`** — return how far apart two field positions are.
2. **`headingFromTo(start, end)`** — return the heading (Rotation2d) you'd
   need to face to point from `start` toward `end`.
3. **`rotatePoint(point, by)`** — return `point` rotated around the origin by
   the given angle.

Each is a one-liner using methods on `Translation2d` shown in the
operator-overloaded math section above.
