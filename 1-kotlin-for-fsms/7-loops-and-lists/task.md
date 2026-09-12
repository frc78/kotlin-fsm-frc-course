# Loops and Lists

Sometimes one name holds many values. A swerve drive has four modules. A
sensor filter keeps the last few readings. Kotlin puts many values of one
type into a **List**.

## Lists

```kotlin
val moduleTemps: List<Double> = listOf(31.5, 30.0, 33.2, 29.8)
```

| Expression          | Meaning                               |
|---------------------|---------------------------------------|
| `listOf(a, b, c)`   | makes a list with those values        |
| `emptyList()`       | a list with nothing in it             |
| `list.size`         | how many values it holds, as an `Int` |
| `list[0]`           | the first value. Counting starts at 0 |
| `list.isEmpty()`    | `true` when `size` is 0               |

`List<Double>` reads as "a list of Doubles". The type inside the angle
brackets is the type of every element.

## Loops

A **loop** runs one block once for each element:

```kotlin
var total = 0.0
for (temp in moduleTemps) {
    total = total + temp
}
```

Each time through, `temp` is the next element. After the loop, `total`
holds the sum. The `var` outside the loop is how the loop remembers a
result.

`repeat(n) { ... }` runs a block `n` times when there is no list. You saw
it in task 1.

## Where loops live in robot code

Loops are rare in state machine code. The robot loop itself, the one that
runs 50 times a second, is the loop that matters. It is written for you by
WPILib. You will see `for` when code touches all swerve modules or all
motors of a mechanism at once.

## Your task

Open `src/Readings.kt`. Complete two functions:

| Function                                            | Returns                                                       |
|-----------------------------------------------------|---------------------------------------------------------------|
| `averageVolts(readings: List<Double>): Double`      | the sum of the readings divided by their count. `0.0` when the list is empty. |
| `anyModuleFaulted(faults: List<Boolean>): Boolean`  | `true` when at least one element is `true`. `false` for an empty list. |

Examples: `averageVolts(listOf(12.6, 11.8, 11.6))` is `12.0`.
`anyModuleFaulted(listOf(false, true, false, false))` is `true`.

## Hints

- Check for an empty list first with an `if`. Dividing by zero gives a
  strange value, not an error.
- For `anyModuleFaulted`, start a `var found = false` before the loop and
  set it to `true` inside the loop when you see a `true`.
- Kotlin also has `readings.average()` and `faults.any { it }`. They do
  the same job in one call. Write the loop version first so you know what
  they do.
