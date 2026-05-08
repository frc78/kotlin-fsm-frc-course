# Current Limits

A Falcon 500 will happily pull 200+ amps if you ask it to — long enough to
trip a breaker, brown out the rio, or burn a motor. **Current limits are
not optional** on a real robot.

There are two limits worth knowing:

- **Supply current limit** — caps current drawn from the *battery*. This
  is what protects breakers and prevents brownouts. Most teams set this
  somewhere around 40 A on intakes, rollers, and indexers.
- **Stator current limit** — caps current through the *motor windings*.
  This is what protects the motor itself from cooking.

For most subsystems, a supply limit is enough. Set it, enable it, done.

## Setting a supply limit

```kotlin
val config = TalonFXConfiguration().apply {
    CurrentLimits.SupplyCurrentLimit = 40.0
    CurrentLimits.SupplyCurrentLimitEnable = true
}
motor.configurator.apply(config)
```

The `Enable` flag is easy to forget — Phoenix6 ships with limits
*disabled* by default, so setting the value alone does nothing. **Both
fields must be set.**

You can layer current limits on top of motor-output config in the same
`TalonFXConfiguration`:

```kotlin
val config = TalonFXConfiguration().apply {
    MotorOutput.NeutralMode = NeutralModeValue.Brake
    CurrentLimits.SupplyCurrentLimit = 40.0
    CurrentLimits.SupplyCurrentLimitEnable = true
}
```

## Your task

Open `src/IntakeMotor.kt`. The `motor` is already declared on CAN ID 22.
Implement `configure()` so the applied configuration has:

- `MotorOutput.NeutralMode = Coast` (intakes free-spin when disabled).
- `CurrentLimits.SupplyCurrentLimit = 40.0`.
- `CurrentLimits.SupplyCurrentLimitEnable = true`.

## Hints

- One `TalonFXConfiguration().apply { ... }` is enough — you don't need
  multiple configs.
- Leaving `SupplyCurrentLimitEnable` as `false` is the most common
  configuration bug. The test will catch it.
