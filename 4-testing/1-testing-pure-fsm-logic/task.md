# Testing Pure FSM Logic

So far, every test has gone through a subsystem `object` and called `periodic()`
to trigger transitions. That works, but it has friction:

- `object` is a singleton — its state leaks between tests, so you need
  `reset()` boilerplate.
- The hardware stubs run on every tick, even when you only care about the
  state-machine logic.
- "Why did this test fail?" can be hard to answer when actions and transitions
  both ran.

There's a better way for FSM logic: **extract the transition rule as a pure
function.**

```kotlin
fun transition(current: State, input: Input): State { ... }
```

`Input` is a `data class` that bundles every value the transition reads —
driver commands plus sensor reads. The function takes inputs and returns the
next state. No fields, no `var`, no hardware. Just a math function.

You can still drive the subsystem by calling `transition()` from
`stateTransitions()`. But now the FSM logic is also testable on its own —
no setup, no teardown, no race conditions. Just `assertEquals(expected,
transition(state, input))`.

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

If multiple conditions match (e.g., `INTAKING` and both `pieceDetected` and
`!commandedIntake`), prefer the one listed *higher* in the table. Stay in the
current state if no condition matches.

## Why this matters

Pure functions are the easiest code in the world to test. No setup. No mocks.
No "why doesn't this state reset between tests?". Just inputs in, output out.

Every robust FSM-driven robot codebase eventually puts its transitions in pure
functions for exactly this reason.
