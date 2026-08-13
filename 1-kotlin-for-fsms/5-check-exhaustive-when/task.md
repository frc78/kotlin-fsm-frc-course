# Check: Exhaustive when

Task 2 claimed the compiler knows an enum's complete list of values — forget a
case and the code won't compile. Time to pin down exactly what that means.
A teammate writes this over a three-value enum:

```kotlin
enum class ClimberState { STOWED, CLIMBING, HANGING }

fun volts(state: ClimberState): Double = when (state) {
    ClimberState.STOWED -> 0.0
    ClimberState.CLIMBING -> 6.0
}
```

There is no branch for `HANGING` and no `else`. What happens?
