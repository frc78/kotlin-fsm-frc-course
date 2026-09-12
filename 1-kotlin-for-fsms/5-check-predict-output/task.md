# Check: Predict the Output

Read this code. Do not run it.

```kotlin
val volts = 11.2
val hasGamePiece = true

val message = if (volts < 11.5 && !hasGamePiece) {
    "low battery, empty"
} else if (volts < 11.5) {
    "low battery, holding"
} else if (hasGamePiece) {
    "holding"
} else {
    "ready"
}
println(message)
```

What does the program print?
