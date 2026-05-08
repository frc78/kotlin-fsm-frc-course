# Guards: Conditional Transitions

A **guard** is an extra condition on a transition: "go from A to B *only if*
some predicate holds."

Guards are how subsystems stay safe and well-behaved. Examples from real robots:

- Don't deploy the climber **unless** the elevator is stowed.
- Don't drive the shooter **unless** the indexer has a piece ready.
- Don't enter `CLIMBING` **unless** the cage sensor is triggered.

Without a guard, the transition fires on the driver's intent alone. With a
guard, the FSM enforces the precondition itself — no need to remember to check
elsewhere.

## How guards look in code

A guard is just an extra `&&` condition inside the transition `when`:

```kotlin
State.STOWED -> if (commandedDeploy && elevatorIsStowed()) State.DEPLOYING else State.STOWED
```

Read as: "from `STOWED`, go to `DEPLOYING` only if the driver commanded deploy
*and* the elevator is stowed; otherwise stay in `STOWED`."

If the guard is false, the transition silently doesn't fire. The driver can
keep holding the button — once the guard becomes true, the transition will fire
on the next tick.

## Your task

Open `src/Pivot.kt`. The pivot has three states (`STOWED`, `MOVING`,
`AT_TARGET`) and a guard: it can only move when the elevator is clear.

`stateActions()` is already implemented. Your job is `stateTransitions()`:

| Current     | Conditions                                              | Next        |
|-------------|---------------------------------------------------------|-------------|
| `STOWED`    | `commandedMove && elevatorClear`                        | `MOVING`    |
| `MOVING`    | `kotlin.math.abs(motor.getPosition() - targetRotations) < 0.1` | `AT_TARGET` |
| `AT_TARGET` | `!commandedMove`                                        | `STOWED`    |

If no condition is met, stay in the current state.

## Hint

`kotlin.math.abs` returns the absolute value of a `Double`. Import it at the
top of the file (it's already there).
