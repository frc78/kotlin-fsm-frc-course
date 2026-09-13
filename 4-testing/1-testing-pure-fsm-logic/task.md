# Testing Pure FSM Logic

So far, every test has gone through a subsystem `object` and called `periodic()`
to trigger transitions. That works, but it has friction:

- `object` is a singleton. Its state leaks between tests, so every test class
  needs a `reset()` call.
- The hardware stubs run on every tick, even when you only care about the
  state-machine logic.
- "Why did this test fail?" is hard to answer when actions and transitions
  both ran.

For FSM logic there is another option: **extract the transition rule as a
pure function.**

```kotlin
fun transition(current: State, input: Input): State { ... }
```

`Input` is a `data class` that bundles every value the transition reads:
driver commands plus sensor reads. The function takes inputs and returns the
next state. No fields, no `var`, no hardware.

This is not a third pattern. It is the body of `stateTransitions()` moved into
a function you can call on its own. A subsystem that uses it looks like this:

```kotlin
private fun stateTransitions() {
    state = transition(state, readInputs())
}
```

`readInputs()` builds one `Input` from the buttons and sensors. Lessons 5 to 8
keep the `when` inside `stateTransitions()`. Both forms express the same
transition table.

## Your task

In `src/IntakeFsm.kt`, complete the `transition` function. The states and
input shape are provided.

Transitions:

| Current     | Condition                       | Next       |
|-------------|---------------------------------|------------|
| `IDLE`      | `commandedIntake`               | `INTAKING` |
| `IDLE`      | `commandedEject`                | `EJECTING` |
| `INTAKING`  | `pieceDetected`                 | `HOLDING`  |
| `INTAKING`  | `!commandedIntake`              | `IDLE`     |
| `HOLDING`   | `commandedEject`                | `EJECTING` |
| `HOLDING`   | `pieceLost`                     | `IDLE`     |
| `EJECTING`  | `!commandedEject`               | `IDLE`     |

If multiple conditions match (for example, `INTAKING` with both `pieceDetected`
and `!commandedIntake`), prefer the one listed *higher* in the table. Stay in
the current state if no condition matches.

## How the hidden test uses it

Each test builds one `Input`, calls `transition()` once, and compares the
result with `assertEquals`. The first argument is the expected state. The
second is the value your function returned.
