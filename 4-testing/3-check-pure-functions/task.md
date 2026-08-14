# Check: Pure Transition Functions

In task 1 you pulled the intake's transition rule out into a pure function:

```kotlin
fun transition(current: State, input: Input): State
```

What makes `transition()` easier to test than `periodic()` on a real
subsystem?
