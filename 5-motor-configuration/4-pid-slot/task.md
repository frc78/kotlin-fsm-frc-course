# Closed-Loop Control: Slot0 PID

`VoltageOut` and `DutyCycleOut` are *open-loop* — you say "6 volts" and
the motor spins however fast 6 volts happens to spin it that day. For a
flywheel that needs to hit a specific RPM, that's not enough. You want
**closed-loop** control: tell the motor a target velocity, and let it
adjust voltage to get there.

Phoenix6 supports closed-loop control on the motor controller itself.
You configure gains once, then send `VelocityVoltage` (or
`PositionVoltage`) requests to drive a target.

## PID gains live in slots

A TalonFX has multiple gain slots (`Slot0`, `Slot1`, `Slot2`). Most
subsystems only ever use `Slot0`.

The gains you'll see in real code:

| Gain | Meaning                                                        |
|------|----------------------------------------------------------------|
| `kP` | Proportional — react to error, larger = more aggressive        |
| `kI` | Integral — react to *accumulated* error (often 0)              |
| `kD` | Derivative — react to error *rate* (often 0)                   |
| `kS` | Static feedforward — voltage to overcome static friction       |
| `kV` | Velocity feedforward — voltage per unit of target velocity     |
| `kA` | Acceleration feedforward — voltage per unit of acceleration    |

For a flywheel, **`kV` does most of the work** (it predicts what voltage
*should* sustain a given RPM), and `kP` cleans up the rest.

## Putting it together

```kotlin
val config = TalonFXConfiguration().apply {
    Slot0.kV = 0.12
    Slot0.kP = 0.25
}
motor.configurator.apply(config)

// Later, in your control code:
motor.setControl(VelocityVoltage(rotationsPerSecond = 80.0))
```

## Your task

Open `src/Flywheel.kt`. The `motor` is already declared on CAN ID 60.

1. Implement `configure()` so the applied configuration has
   `Slot0.kV = 0.12` and `Slot0.kP = 0.25`.
2. Implement `runAtRps(rps: Double)` to send
   `VelocityVoltage(rps)` to the motor.

## Hints

- `VelocityVoltage` is a `data class` — you can construct it positionally
  (`VelocityVoltage(rps)`) or named (`VelocityVoltage(rotationsPerSecond = rps)`).
- You don't need to set `kI`, `kD`, `kS`, or `kA` — defaulting them to
  `0.0` is the standard starting point.
