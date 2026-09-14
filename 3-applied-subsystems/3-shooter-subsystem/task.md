# Applied Shooter

The flywheel target depends on the shot distance. It changes on every shot, so
it is runtime data, not a fixed setpoint. The shooter uses a sealed class, and
each state that needs the target carries it.

## States (sealed class)

```kotlin
sealed class FsmState {
    data object Idle : FsmState()
    data class SpinningUp(val targetRpm: Double) : FsmState()
    data class Ready(val targetRpm: Double)      : FsmState()
    data class Feeding(val targetRpm: Double)    : FsmState()
}
```

(Already provided.)

## Driver inputs

- `commandedTargetRpm: Double?` is the requested speed in rpm. `null` means
  "no shot pending, spin down".
- `commandedFire: Boolean` releases the piece into the flywheel.

## Kotlin you need

**Nullable types:** `Double?` holds a `Double` or `null`. You cannot use it as
a `Double` until you check it. Copy it to a local first:
`val rpm = commandedTargetRpm`. Inside `if (rpm != null) { ... }` the compiler
treats `rpm` as a plain `Double`. This is called a smart cast.

**Binding the subject of `when`:** `when (val s = state) { ... }` stores the
current state in `s`. Inside `is FsmState.SpinningUp -> ...` the compiler
smart-casts `s`, so `s.targetRpm` is readable. Return `s` from a branch to
stay in the same state. The result of the `when` expression is the value you
assign to `state`.

**Extension properties:** `4500.0.rpm` looks like a property on a number. It
is a small function that Kotlin lets you call with dot syntax. `.rpm` converts
rotations per minute to rotations per second, so `4500.0.rpm == 75.0`.
`VelocityVoltage` takes rotations per second.

## Transitions

Rows are checked from top to bottom. The first matching row wins.

| Current         | Condition                                          | Next                  |
|-----------------|----------------------------------------------------|-----------------------|
| `Idle`          | `commandedTargetRpm != null`                       | `SpinningUp(rpm)`     |
| `SpinningUp(s)` | `commandedTargetRpm == null`                       | `Idle`                |
| `SpinningUp(s)` | `flywheel.getVelocity() >= 0.95 * s.targetRpm.rpm` | `Ready(s.targetRpm)`  |
| `Ready(s)`      | `commandedTargetRpm == null`                       | `Idle`                |
| `Ready(s)`      | `commandedFire`                                    | `Feeding(s.targetRpm)`|
| `Feeding(s)`    | `!commandedFire`                                   | `Ready(s.targetRpm)`  |

Clearing the target has priority over reaching speed.

A changed, non-null `commandedTargetRpm` while in `SpinningUp`, `Ready`, or
`Feeding` does nothing. The state keeps the target it was created with. Only
`Idle` reads a new target.

The 95 % threshold gives a small ready band, so jitter near the target does
not bounce the shooter out of `Ready`.

## Actions

| State           | flywheel                            | feeder             |
|-----------------|-------------------------------------|--------------------|
| `Idle`          | `stopMotor()`                       | `stopMotor()`      |
| `SpinningUp(s)` | `VelocityVoltage(s.targetRpm.rpm)`  | `stopMotor()`      |
| `Ready(s)`      | `VelocityVoltage(s.targetRpm.rpm)`  | `stopMotor()`      |
| `Feeding(s)`    | `VelocityVoltage(s.targetRpm.rpm)`  | `VoltageOut(8.0)`  |

`stopMotor()` sends a `NeutralOut` request. That is a different request from
`VoltageOut(0.0)`. The tests check for `NeutralOut`.

## Your task

Open `src/Shooter.kt`. Implement `stateTransitions()` and `stateActions()`.
