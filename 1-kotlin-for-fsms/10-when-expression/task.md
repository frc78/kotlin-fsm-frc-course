# The when Expression

In task 9 you made an enum: a fixed list of named values. Now you pick a
different result for each value. An `if` / `else if` chain can do it, but
Kotlin has a cleaner tool: **`when`**.

## when as a value

```kotlin
enum class Gear { LOW, HIGH }

fun maxSpeed(gear: Gear): Double = when (gear) {
    Gear.LOW -> 2.0
    Gear.HIGH -> 4.5
}
```

`when (gear)` looks at the value. Each line is a **branch**:
`value -> result`. The branch that matches gives the result of the whole
`when`. A function can return a `when` directly, the same way it returns
an `if`.

## Exhaustive: every value must have a branch

When `when` is used as a value, the compiler counts the branches against
the enum. If one value has no branch, the code does not compile. This is
a feature. Add a fourth `Gear` later, and the compiler points at every
`when` that needs a new line. You never ship a state the code forgot.

An `else ->` branch catches everything not listed. It also switches this
check off. In this course, `when` over an enum lists every value and has
no `else`.

## Your task

Open `src/RollerState.kt`. The enum is written. Complete `voltsFor` so
that each state gives its voltage:

| State     | Volts  |
|-----------|--------|
| `STOPPED` | `0.0`  |
| `FORWARD` | `8.0`  |
| `REVERSE` | `-4.0` |

Write one branch per state. Then delete the `else` line. With all three
branches present, the `when` is exhaustive and compiles without it.

## Hint

A branch names the enum value with its type: `RollerState.STOPPED -> ...`.
