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

Phoenix6 configuration follows a three-step shape:

1. Create a fresh `TalonFXConfiguration`.
2. Mutate the fields inside its nested config blocks.
3. Hand the configuration to the motor's configurator (every `TalonFX`
   exposes a `configurator` property with an `apply(config)` method).

The two fields you'll touch in this task live on the `MotorOutput` block:

- `MotorOutput.NeutralMode` — a `NeutralModeValue` enum value.
- `MotorOutput.Inverted` — an `InvertedValue` enum value.

Kotlin's `.apply { ... }` idiom is the idiomatic way to mutate the
config object inline without naming a local variable for every
assignment:

```kotlin
val cfg = SomeType().apply {
    fieldA = ...
    fieldB = ...
}
```

Don't let the shared name trip you up: Kotlin's `.apply { ... }` scope
function and the configurator's `apply(config)` method are unrelated
things that happen to share a name — the first mutates the object
you're building, the second hands the finished configuration to the
motor.

## Your task

Open `src/ArmMotor.kt`. An arm needs **brake mode** (so it holds position
when disabled) and **inverted direction** (so positive voltage raises the
arm).

1. The `motor` is already declared on CAN ID 25.
2. Implement `configure()`. The applied `TalonFXConfiguration` should
   have:
   - `MotorOutput.NeutralMode` set to `Brake`.
   - `MotorOutput.Inverted` set to `Clockwise_Positive`.

## Hints

- `NeutralModeValue` and `InvertedValue` are enums imported from
  `frc.stubs.*`. Reference an enum value the standard Kotlin way:
  `EnumType.VALUE`.
- One `TalonFXConfiguration().apply { ... }` block is enough — you
  don't need to apply multiple separate configurations.
- The configurator's `apply(...)` call stores the configuration on the
  stub so the test can inspect it. Forgetting that final call is a
  common mistake — the configuration object exists but never reaches
  the motor.
