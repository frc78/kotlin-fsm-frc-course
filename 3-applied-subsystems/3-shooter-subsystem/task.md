# Applied Shooter

The shooter is where sealed classes pay off. The flywheel target RPM depends on
**shot distance** — different on every shot — so it's runtime data, not a
fixed setpoint.

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

The flywheel passes its target through every state that needs it. When you
smart-cast inside an `is`-branch, you can read `s.targetRpm` directly.

## Driver inputs

- `commandedTargetRpm: Double?` — the requested RPM. `null` means "no shot
  pending; spin down."
- `commandedFire: Boolean` — release the piece into the flywheel.

## Transitions

```
Idle           + commandedTargetRpm != null                 -> SpinningUp(rpm)
SpinningUp(r)  + commandedTargetRpm == null                 -> Idle
SpinningUp(r)  + flywheel.getVelocity() >= 0.95 * r.rpm     -> Ready(r)
Ready(r)       + commandedTargetRpm == null                 -> Idle
Ready(r)       + commandedFire                              -> Feeding(r)
Feeding(r)     + !commandedFire                             -> Ready(r)
```

The 95% threshold gives a small ready-band so jitter near the target doesn't
bounce us out of `Ready`.

## Actions

| State          | flywheel                            | feeder              |
|----------------|-------------------------------------|---------------------|
| `Idle`         | `stopMotor()`                       | `stopMotor()`       |
| `SpinningUp(r)`| `VelocityVoltage(r.rpm)`            | `stopMotor()`       |
| `Ready(r)`     | `VelocityVoltage(r.rpm)`            | `stopMotor()`       |
| `Feeding(r)`   | `VelocityVoltage(r.rpm)`            | `VoltageOut(8.0)`   |

`r.rpm` is the unit extension property: it converts an `rpm` value (rotations
per minute) into rotations per second, which is what `VelocityVoltage` expects.
So `4500.0.rpm == 75.0`.

## Your task

Open `src/Shooter.kt`. Implement `stateTransitions()` and `stateActions()`.

## Hint — pattern matching with `when`

Inside `when (val s = state)`, branches that use `is FsmState.SpinningUp`
smart-cast `s` to that type, so you can read `s.targetRpm`:

```kotlin
state = when (val s = state) {
    is FsmState.Idle -> {
        val rpm = commandedTargetRpm
        if (rpm != null) FsmState.SpinningUp(rpm) else s
    }
    is FsmState.SpinningUp -> {
        if (commandedTargetRpm == null) FsmState.Idle
        else if (flywheel.getVelocity() >= 0.95 * s.targetRpm.rpm) FsmState.Ready(s.targetRpm)
        else s
    }
    // ...
}
```
