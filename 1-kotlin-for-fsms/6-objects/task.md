# Objects

So far, values and functions have stood alone. Real code groups them. An
**object** is one named thing that owns its own values and its own
functions.

```kotlin
object Compressor {
    var pressurePsi: Double = 0.0
    fun isFull(): Boolean = pressurePsi >= 115.0
}
```

- `object Compressor` creates exactly one `Compressor`. There is no second
  one. A robot has one compressor, one intake, one elevator. Each gets one
  object. Every subsystem in this course is an `object`.
- Values inside an object are called **properties**. Functions inside are
  called **methods**. They can use each other by name.
- Code outside the object reaches inside with a dot:
  `Compressor.pressurePsi = 120.0` then `Compressor.isFull()`.

## The reset() habit

An object keeps its values between uses. A test that changes `volts`
would leak that change into the next test. So every object in this course
has a `reset()` method that puts the values back to their start. The
tests call it before each check. You will see `reset()` in every subsystem
from Lesson 2 on.

## Your task

Open `src/Battery.kt`. The object has one property, `volts`, and a
`reset()` that is already written. Complete two methods:

| Method      | Returns                                                                              |
|-------------|--------------------------------------------------------------------------------------|
| `isLow()`   | `true` when `volts` is below `11.5`                                                  |
| `percent()` | charge as a whole number from 0 to 100. `10.0` V is 0. `12.6` V is 100. Values in between are on a straight line. Round to the nearest whole number. Never return less than 0 or more than 100. |

Examples: at `11.3` V, `percent()` is `50`. At `11.0` V it is `38`. At
`13.2` V it is `100`. At `9.0` V it is `0`.

## Hints

- `isLow()` is one comparison on `volts`.
- For `percent()`: first find the fraction of the way from 10.0 to 12.6,
  as a `Double`. Multiply by 100. Then `.roundToInt()` turns a `Double`
  into the nearest `Int`. The starter imports it. Then
  `.coerceIn(0, 100)` limits an `Int` to a range.
