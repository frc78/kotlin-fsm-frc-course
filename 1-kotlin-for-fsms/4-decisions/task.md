# Decisions

Robot code makes decisions on every tick. Is the elevator at its target?
Should the intake run? Is this voltage safe? Each decision is a `Boolean`
question with a `true` or `false` answer.

## Comparisons

A comparison takes two values and gives a `Boolean`:

| Operator | Meaning                  |
|----------|--------------------------|
| `<`      | less than                |
| `<=`     | less than or equal       |
| `>`      | greater than             |
| `>=`     | greater than or equal    |
| `==`     | equal                    |
| `!=`     | not equal                |

`volts > 12.0` is `true` when `volts` is 15.0 and `false` when it is 6.0.

## Combining Booleans

| Operator | Meaning                                | Example                          |
|----------|----------------------------------------|----------------------------------|
| `&&`     | and: both must be true                 | `commanded && hasPiece`          |
| `\|\|`   | or: at least one must be true          | `tooHot \|\| tooFast`             |
| `!`      | not: flips true and false              | `!hasPiece`                      |

## if / else as a value

```kotlin
val mode = if (volts > 11.5) "OK" else "LOW"
```

`if` tests a `Boolean`. When it is `true`, the value is the first branch.
When it is `false`, the value is the `else` branch. A function can return
an `if` directly:

```kotlin
fun signOf(x: Double): Double = if (x < 0.0) -1.0 else 1.0
```

You can chain them: `if (a) x else if (b) y else z`.

## Absolute value

`abs(x)` from `kotlin.math` gives the distance of `x` from zero.
`abs(-0.3)` is `0.3`. The starter file already imports it.

## Your task

Open `src/Decisions.kt`. Complete three functions:

| Function                                                                   | Returns `true` when, or value                                    |
|----------------------------------------------------------------------------|-------------------------------------------------------------------|
| `isAtTarget(position: Double, target: Double, tolerance: Double): Boolean` | the distance between `position` and `target` is less than or equal to `tolerance` |
| `shouldRunIntake(commanded: Boolean, hasGamePiece: Boolean): Boolean`      | the intake is commanded and there is no game piece                |
| `clampVolts(volts: Double): Double`                                        | `volts`, but never above `12.0` and never below `-12.0`           |

Examples: `isAtTarget(5.1, 5.0, 0.1)` is `true`. `isAtTarget(5.2, 5.0, 0.1)`
is `false`. `clampVolts(15.0)` is `12.0`. `clampVolts(-20.0)` is `-12.0`.

## Hints

- `isAtTarget` is one comparison on an `abs(...)`.
- `shouldRunIntake` is two Booleans joined with one operator.
- `clampVolts` is an `if` / `else if` / `else` chain with three branches.
