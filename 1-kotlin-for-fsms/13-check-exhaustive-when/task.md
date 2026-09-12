# Check: Exhaustive when

A teammate writes this over a three-value enum:

```kotlin
enum class ClimberState { STOWED, CLIMBING, HANGING }

fun volts(state: ClimberState): Double = when (state) {
    ClimberState.STOWED -> 0.0
    ClimberState.CLIMBING -> 6.0
}
```

There is no branch for `HANGING` and no `else`. What happens?
