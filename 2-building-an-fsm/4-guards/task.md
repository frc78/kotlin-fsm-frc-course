# Guards: Conditional Transitions

A **guard** is an extra condition on a transition: "go from A to B *only
if* some condition holds."

Guards keep subsystems safe. Examples from real robots:

- Do not deploy the climber **unless** the elevator is stowed.
- Do not run the shooter **unless** the indexer has a piece ready.
- Do not enter `CLIMBING` **unless** the cage sensor is triggered.

Without a guard, the transition fires on the driver's intent alone. With a
guard, the FSM enforces the precondition itself. Nobody has to remember to
check it somewhere else.

## How guards look in code

A guard is one more `&&` condition inside the transition `when`:

```kotlin
State.STOWED -> if (commandedDeploy && elevatorIsStowed()) State.DEPLOYING else State.STOWED
```

Read it as: "from `STOWED`, go to `DEPLOYING` only if the driver commanded
deploy *and* the elevator is stowed; otherwise stay in `STOWED`."

If the guard is false, the transition does not fire. The driver can keep
holding the button. When the guard becomes true, the transition fires on
the next tick.

## Your task

Open `src/Pivot.kt`. The pivot has three states (`STOWED`, `MOVING`,
`AT_TARGET`) and one guard: it can only move when the elevator is clear.

`stateActions()` is written. In `MOVING` it commands
`PositionVoltage(targetRotations)`, a "go to this position" request. The
motor controller drives to the position on its own. The simulated motor
reaches the target at once, so `motor.getPosition()` reports the target on
the next tick. (In real Phoenix6, `getPosition()` returns a
`StatusSignal`. See the note in task 2.)

Write `stateTransitions()`:

| Current     | Conditions                                               | Next        |
|-------------|----------------------------------------------------------|-------------|
| `STOWED`    | `commandedMove && elevatorClear`                         | `MOVING`    |
| `MOVING`    | the position is within 0.1 rotations of `targetRotations` | `AT_TARGET` |
| `AT_TARGET` | `!commandedMove`                                         | `STOWED`    |

If no condition is met, stay in the current state. If `targetRotations`
changes while `MOVING`, the pivot stays `MOVING` until it reaches the new
target.

`AT_TARGET` to `STOWED` changes only the state. The stub does not model the
return move. A real pivot would add a `RETURNING` state that drives back.

## Hint

"Within 0.1 of the target" means the distance between the two values is
less than 0.1, in either direction. `kotlin.math.abs` returns the absolute
value of a `Double`. It is already imported at the top of the file.
