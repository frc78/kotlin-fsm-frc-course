# Motor Output: Neutral Mode and Inversion

A bare `TalonFX(canId = X)` is rarely enough. Almost every real subsystem
also configures **how the motor behaves at zero output** and **which
direction counts as "positive"**.

## Two essentials

### Neutral mode

When you stop sending output, the motor either:

- **`Coast`** — free-wheels. Good for drivetrains where you want momentum.
- **`Brake`** — shorts the windings to act as a brake. Good for arms,
  elevators, and anything that shouldn't fall when disabled.

### Inversion

The Falcon's "positive direction" depends on how the gearbox is mounted.
Half the time, positive voltage drives the mechanism the wrong way. You
fix that in software once, in configuration, instead of negating every
control request.

- **`CounterClockwise_Positive`** (default)
- **`Clockwise_Positive`** (inverted)

## How configuration works

Phoenix6 builds a `TalonFXConfiguration` object, mutates its nested config
blocks, and applies it through the motor's configurator:

```kotlin
val config = TalonFXConfiguration().apply {
    MotorOutput.NeutralMode = NeutralModeValue.Brake
    MotorOutput.Inverted = InvertedValue.Clockwise_Positive
}
motor.configurator.apply(config)
```

The `.apply { ... }` Kotlin idiom lets you mutate the config object inline
without naming a local variable for every field.

## Your task

Open `src/ArmMotor.kt`. An arm needs **brake mode** (so it holds position
when disabled) and **inverted direction** (so positive voltage raises the
arm).

1. The `motor` is already declared on CAN ID 25.
2. Implement `configure()` to build a `TalonFXConfiguration`, set
   `NeutralMode = Brake` and `Inverted = Clockwise_Positive`, and apply
   it through `motor.configurator`.

## Hints

- `NeutralModeValue` and `InvertedValue` are enums imported from
  `frc.stubs.*`.
- `motor.configurator.apply(config)` stores the configuration on the stub
  so the test can inspect it.
