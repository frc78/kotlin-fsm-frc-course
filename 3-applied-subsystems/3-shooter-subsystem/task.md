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

If more than one condition holds at once, the row listed *higher* wins —
clearing the target always takes priority over reaching speed.

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

## Hints

**Capturing `state` for smart casts.** When the right-hand side of a
transition needs to read data carried by the current state (for example
`targetRpm` from `SpinningUp`), use the `when (val s = state)` form. That
binds `s` to the current state, and inside `is FsmState.SpinningUp -> …`
the compiler smart-casts `s` so `s.targetRpm` is readable without an
explicit cast.

**Returning the same state.** Inside a branch, return the bound value
(`s`) to mean "stay where I am." Don't try to write `state` again — the
result of the `when` expression is what gets assigned.

**Reading `commandedTargetRpm`.** It's a `Double?`. Smart-cast it once
to a non-null local (`val rpm = commandedTargetRpm; if (rpm != null) …`)
when you need to construct a state that requires the rpm value.

**Velocity units.** `VelocityVoltage` takes rotations *per second*, but
the shooter's targets are rpm. Use the `.rpm` extension property to
convert: `4500.0.rpm == 75.0`.
