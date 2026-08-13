# Check: Pure Transition Functions

In task 1 you pulled the intake's transition rule out into a pure function:

```kotlin
fun transition(current: State, input: Input): State
```

Before that, every test had to go through a subsystem `object`, set up its
fields, call `periodic()`, and remember to `reset()` between tests.

What makes `transition()` easier to test than `periodic()` on a real
subsystem?
