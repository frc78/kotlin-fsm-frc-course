# Per-Tick vs One-Shot: Entry Side Effects

So far, `stateActions()` runs *every tick*. That's correct for things like
"set the motor voltage" — sending the same setpoint every 20 ms is harmless and
keeps the controller fresh.

But some work should run **once on entering a state**, not every tick:

- Logging "transitioned to DEPLOYED" — you want one line, not 50 per second.
- Resetting an encoder when entering a homing state.
- Publishing a SmartDashboard event.
- Starting a one-shot timer.

The pattern: track the previous state. Each tick, if the current state differs
from the previous, run the entry side effect for the *new* state, then update
the previous-state pointer.

```kotlin
private var previousState: State? = null

private fun runEntrySideEffects() {
    if (state != previousState) {
        when (state) {
            State.A -> // do once-on-entry work for A
            // ...
        }
        previousState = state
    }
}

override fun periodic() {
    stateTransitions()
    runEntrySideEffects()
    stateActions()
}
```

Why `previousState: State?` (nullable)? On the very first tick, there's no
previous state. Comparing `state != null` triggers the entry effect for the
initial state too — which is what you want.

## Your task

Open `src/Climber.kt`. `stateTransitions()` and `stateActions()` are already
implemented (read them — they're a good example). Your job is to fill in
`runEntrySideEffects()`.

For each new state, append a log line to `deploymentLogs`:

| New state    | Log entry      |
|--------------|----------------|
| `STOWED`     | `"stowed"`     |
| `DEPLOYING`  | `"deploying"`  |
| `DEPLOYED`   | `"deployed"`   |
| `CLIMBING`   | `"climbing"`   |

Make sure each entry appears **exactly once per transition** — repeated periodic
ticks in the same state should not produce duplicates.
