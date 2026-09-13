# Motor Output: Neutral Mode and Inversion

Almost every real subsystem configures **how the motor behaves at zero
output** and **which direction counts as "positive"**.

## Two essentials

### Neutral mode

When you stop sending output, the motor does one of two things:

- **`Coast`**: it free-wheels. Good for drivetrains, where you want momentum.
- **`Brake`**: the controller connects the motor windings together, so the
  motor resists motion. Good for arms, elevators, and anything that must not
  fall when disabled.

### Inversion

The motor's "positive direction" depends on how the gearbox is mounted.
Often, positive voltage drives the mechanism the wrong way. You fix that
once, in configuration, instead of negating every control request.

- **`CounterClockwise_Positive`** (default)
- **`Clockwise_Positive`** (inverted)

## How configuration works

Phoenix6 configuration has three steps:

1. Create a fresh `TalonFXConfiguration`.
2. Set the fields inside its nested config blocks.
3. Hand the configuration to the motor's configurator. Every `TalonFX`
   exposes a `configurator` property with an `apply(config)` method.

The fields for this task live on the `MotorOutput` block:

| Field                    | Type               | Meaning                          |
|--------------------------|--------------------|----------------------------------|
| `MotorOutput.NeutralMode`| `NeutralModeValue` | what the motor does at zero output |
| `MotorOutput.Inverted`   | `InvertedValue`    | which rotation direction is positive |

These names start with a capital letter because they copy the CTRE Java API.
Kotlin code usually starts property names with a small letter. Phoenix6
config fields are the exception.

Kotlin's `.apply { ... }` lets you set fields on a new object without naming
a variable for each assignment. Inside the block, you write the nested field
path directly:

```kotlin
val cfg = TalonFXConfiguration().apply {
    MotorOutput.NeutralMode = NeutralModeValue.Coast
}
```

Do not confuse the two `apply` names. Kotlin's `.apply { ... }` is a scope
function that changes the object you are building. The configurator's
`apply(config)` method hands the finished configuration to the motor.

## Your task

Open `src/ArmMotor.kt`. An arm needs **brake mode** so it holds position
when disabled, and **inverted direction** so positive voltage raises the arm.

The `motor` is declared on CAN ID 25. Implement `configure()`. The applied
`TalonFXConfiguration` must have:

| Field                     | Value                |
|---------------------------|----------------------|
| `MotorOutput.NeutralMode` | `Brake`              |
| `MotorOutput.Inverted`    | `Clockwise_Positive` |

## Hints

- `NeutralModeValue` and `InvertedValue` are enums imported from
  `frc.stubs.*`. Reference an enum value as `EnumType.VALUE`.
- One `TalonFXConfiguration().apply { ... }` block is enough. Do not apply
  several separate configurations.
- The configurator's `apply(...)` call stores the configuration on the
  stub so the test can inspect it. If you forget that final call, the
  configuration object exists but never reaches the motor.
