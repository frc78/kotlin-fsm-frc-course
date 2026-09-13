# Translation2d and Rotation2d

WPILib has three geometry types: `Translation2d`, `Rotation2d`, and `Pose2d`.
Pose estimation, vision, and swerve aiming all use them. This task covers the
first two.

## The field frame

WPILib uses one field frame for every position:

- The origin is the corner of the field at the blue alliance wall, on the
  right side as blue looks down the field.
- **+X** points away from the blue wall, toward the red wall.
- **+Y** points to blue's left.
- **0°** is along +X. **90°** is along +Y. Counterclockwise is positive.

```
        +Y
        ^
        |          red wall
        |   ---------------------
        |   |                   |
        |   |   90°             |
        |   |    ^              |
        |   |    |              |
        |   |    o----> 0°      |
        |   |                   |
        |   ---------------------
        |          blue wall
  origin o-------------------------> +X
```

Field elements are published in blue coordinates. A robot on red mirrors them
around the field center. This course leaves that step out. Every position in
these tasks is in the one frame above.

## Two Kotlin features you have not seen

**Operator overloading.** A class can give a meaning to `+`, `-`, and `*` for
its own values. `Translation2d(1.0, 2.0) + Translation2d(3.0, 4.0)` calls the
class's `plus` function and returns `Translation2d(4.0, 6.0)`. The class
author writes `operator fun plus(...)` once. You write `a + b`.

**Companion object factories.** A function that lives on the class instead of
on one value. You call it with the class name in front:
`Rotation2d.fromDegrees(90.0)`. It builds and returns a new `Rotation2d`. You
do not need an existing `Rotation2d` to call it.

## `Translation2d`

A 2D point or offset. `x` and `y` are in meters.

| Operation             | Result                                                     |
|-----------------------|------------------------------------------------------------|
| `a + b`, `a - b`      | component-wise add or subtract                             |
| `a.getDistance(b)`    | straight-line distance from `a` to `b`, in meters          |
| `a.getNorm()`         | distance from the origin to `a`                            |
| `a.getAngle()`        | the direction of `a` from the origin, as a `Rotation2d`    |
| `a.rotateBy(r)`       | `a` rotated around the origin by `r`                       |

## `Rotation2d`

An angle. Read it with `.radians` or `.degrees`. Build one with
`Rotation2d.fromDegrees(...)` or `Rotation2d.fromRadians(...)`.

Operators: `r1 + r2`, `r1 - r2`, `-r1`.

Real WPILib `Rotation2d` also has `getCos()` and `getSin()`. This stub does
not. No function in this task needs them.

## Your task

Implement three functions in `src/Geometry.kt`.

| Function                    | Input                              | Output                                                  |
|-----------------------------|------------------------------------|---------------------------------------------------------|
| `distanceBetween(a, b)`     | two field positions                | how far apart they are, in meters                       |
| `headingFromTo(start, end)` | two field positions                | the `Rotation2d` a robot at `start` faces to look at `end` |
| `rotatePoint(point, by)`    | a position and an angle            | `point` rotated around the origin by `by`               |

Examples the tests use:

| Call                                                      | Result              |
|-----------------------------------------------------------|---------------------|
| `distanceBetween((0, 0), (3, 4))`                         | `5.0`               |
| `headingFromTo((0, 0), (0, 5))`                           | 90°                 |
| `headingFromTo((2, 0), (-1, 0))`                          | 180°                |
| `headingFromTo((0, 0), (-1, -1))`                         | -135°               |
| `rotatePoint((1, 0), 90°)`                                | `(0, 1)`            |
