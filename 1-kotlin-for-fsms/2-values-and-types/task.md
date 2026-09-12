# Values and Types

A program stores values under names. Every value has a **type**. The type
tells the compiler what kind of value it is and what you can do with it.

## Four types you will use every day

| Type      | What it holds                  | Examples             |
|-----------|--------------------------------|----------------------|
| `Int`     | a whole number                 | `0`, `150`, `-3`     |
| `Double`  | a number with a decimal point  | `12.6`, `0.0`, `-4.5` |
| `Boolean` | true or false                  | `true`, `false`      |
| `String`  | text, inside double quotes     | `"Kraken"`, `""`     |

`12` and `12.0` are different. `12` is an `Int`. `12.0` is a `Double`. Motor
voltages, positions, and speeds are always `Double`. Counts are `Int`.

## Naming a value

```kotlin
val batteryVolts: Double = 12.6
var matchSeconds: Int = 150
```

- `val` names a value that never changes after it is set.
- `var` names a value that can change. `matchSeconds = matchSeconds - 1`
  makes it one smaller.
- The part after the colon is the type. Then `=` gives the value.

Use `val` unless you know the value must change.

## Putting values into text

A `$` inside a string pulls a value in:

```kotlin
val name = "Kraken"
println("Motor: $name")      // prints  Motor: Kraken
```

This is a **string template**. When a `Double` goes into a template, its
decimal point stays: `12.6` prints as `12.6`.

## Your task

Open `src/RobotFacts.kt`. Set the four properties to these values:

| Property        | Value                        |
|-----------------|------------------------------|
| `batteryVolts`  | `12.6`                       |
| `matchSeconds`  | `150`                        |
| `hasGamePiece`  | `false`                      |
| `robotName`     | any text that is not empty   |

Then make `describe()` return this text, with the three values filled in
from the properties:

```text
<robotName>: <batteryVolts> V, <matchSeconds> s
```

For example, if `robotName` is `"Kraken"`, `describe()` returns
`"Kraken: 12.6 V, 150 s"`.

## Hints

- A function that returns one value can be written on one line:
  `fun describe(): String = "..."`.
- Inside an `object`, a template can use the object's own properties by
  name: `"$batteryVolts"`.
