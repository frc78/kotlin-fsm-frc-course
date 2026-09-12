# Functions

A **function** is a named block of code that takes values in and gives one
value back. You write it once and call it as many times as you need.

## The parts of a function

```kotlin
fun metersToFeet(meters: Double): Double = meters * 3.28084
```

| Part              | Meaning                                              |
|-------------------|------------------------------------------------------|
| `fun`             | starts a function                                    |
| `metersToFeet`    | the name                                             |
| `meters: Double`  | a **parameter**: a named input with its type         |
| `: Double`        | the **return type**: the type of the value it gives back |
| `= meters * 3.28084` | the **body**: the value it returns                |

A function with more than one parameter separates them with commas:
`fun add(a: Double, b: Double): Double = a + b`.

## Calling a function

```kotlin
val feet = metersToFeet(2.0)     // feet is 6.56168
```

You give a value for each parameter, in order. The call becomes the value
the function returns. A function can call another function inside its body.

## Arithmetic

`+`, `-`, `*` (multiply), `/` (divide). On two `Double` values, `/` gives
the exact decimal result.

## Gear ratios, in one paragraph

Motors turn fast. Mechanisms turn slowly. A gearbox between them has a
**ratio**. A 4:1 ratio means the motor turns four times for one turn of
the mechanism. So mechanism rotations = motor rotations divided by the
ratio. You will use this idea in every lesson about motors.

## Your task

Open `src/Conversions.kt`. Complete three functions:

| Function                                                                | Returns                              |
|-------------------------------------------------------------------------|--------------------------------------|
| `fractionToVolts(fraction: Double): Double`                             | `fraction` times 12.0                |
| `mechanismRotations(motorRotations: Double, gearRatio: Double): Double` | `motorRotations` divided by `gearRatio` |
| `rotationsToDegrees(rotations: Double): Double`                         | `rotations` times 360.0              |

Examples: `fractionToVolts(0.5)` is `6.0`. `mechanismRotations(10.0, 4.0)`
is `2.5`. `rotationsToDegrees(0.25)` is `90.0`.

## Hint

Each function is one line. Replace `TODO()` with the arithmetic.
