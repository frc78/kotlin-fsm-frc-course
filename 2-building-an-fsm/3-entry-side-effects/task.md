# Per-Tick vs One-Shot: Entry Side Effects

So far, `stateActions()` runs *every tick*. That is correct for "set the
motor voltage". Sending the same setpoint every 20 ms is harmless and keeps
the controller fresh.

Some work must run **once, when the FSM enters a state**, not every tick:

- Logging "transitioned to DEPLOYED". You want one line, not 50 per second.
- Resetting an encoder when entering a homing state.
- Sending a one-time event to the driver dashboard.
- Starting a one-shot timer.

The pattern is: act on the *change* of state, not on the state itself.

1. Keep a `previousState` field next to `state`. It starts as `null`.
2. Each tick, compare `state` to `previousState`. If they differ, the FSM
   has just transitioned. Run the entry side effect for the *new* state.
   Then store `state` into `previousState` so the effect does not fire
   again on the next tick.
3. If they are the same, do nothing.

`periodic()` is wired so that `runEntrySideEffects()` runs *between*
`stateTransitions()` and `stateActions()`. That order makes the effect fire
in the same tick as the transition.

## Kotlin you need

**Nullable types.** `State?` (with a question mark) is a type that holds a
`State` *or nothing*. The value for "nothing" is `null`. A plain `State`
can never be `null`; the compiler rejects it. `previousState` is `State?`
so that on the very first tick, when nothing has run yet, it is `null`.
Then `state != previousState` is `true` and the entry effect runs for the
initial state too. You compare a nullable value with `==` and `!=` as
usual.

**Mutable lists.** `deploymentLogs` is a `MutableList<String>`. Lesson 1
used read-only lists. A mutable list can grow: `deploymentLogs.add("text")`
appends one entry at the end.

## Your task

Open `src/Climber.kt`. `stateTransitions()` and `stateActions()` are
written. Read them; they are a good example. Fill in
`runEntrySideEffects()`.

For each new state, append one log line to `deploymentLogs`:

| New state    | Log entry      |
|--------------|----------------|
| `STOWED`     | `"stowed"`     |
| `DEPLOYING`  | `"deploying"`  |
| `DEPLOYED`   | `"deployed"`   |
| `CLIMBING`   | `"climbing"`   |

Each entry must appear **exactly once per transition**. Repeated ticks in
the same state must not add duplicates.
