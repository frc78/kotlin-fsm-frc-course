# Per-Tick vs One-Shot: Entry Side Effects

So far, `stateActions()` runs *every tick*. That's correct for things like
"set the motor voltage" — sending the same setpoint every 20 ms is harmless and
keeps the controller fresh.

But some work should run **once on entering a state**, not every tick:

- Logging "transitioned to DEPLOYED" — you want one line, not 50 per second.
- Resetting an encoder when entering a homing state.
- Publishing a SmartDashboard event.
- Starting a one-shot timer.

The pattern is "on edge, not on level":

1. Keep a `previousState` field alongside `state`. It starts `null`.
2. Each tick, compare `state` to `previousState`. If they differ, the
   FSM just transitioned, so run the entry side effect for the *new*
   state. Then store `state` into `previousState` so the side effect
   doesn't re-fire on the next tick.
3. If they're the same, do nothing — periodic ticks within the same
   state are quiet.

`previousState` is nullable (`State?`) so that on the very first tick —
when `previousState` is still `null` — `state != previousState` is true
and the entry effect runs for the initial state too, which is what you
want for things like "log the starting state."

`periodic()` is already wired so that `runEntrySideEffects()` runs
*between* `stateTransitions()` and `stateActions()` — that ordering
makes the effect fire in the same tick the transition happens.

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
