# Check: Two Applies, One Winner

A teammate configures an arm motor in two separate steps:

```kotlin
motor.configurator.apply(TalonFXConfiguration().apply {
    MotorOutput.NeutralMode = NeutralModeValue.Brake
    MotorOutput.Inverted = InvertedValue.Clockwise_Positive
})
motor.configurator.apply(TalonFXConfiguration().apply {
    CurrentLimits.SupplyCurrentLimit = 40.0
    CurrentLimits.SupplyCurrentLimitEnable = true
})
```

After the second `apply(...)`, what neutral mode is the motor in?
