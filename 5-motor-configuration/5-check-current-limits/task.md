# Check: Two Applies, One Winner

A teammate configures an intake motor in two separate steps:

```kotlin
motor.configurator.apply(TalonFXConfiguration().apply {
    CurrentLimits.SupplyCurrentLimit = 40.0
    CurrentLimits.SupplyCurrentLimitEnable = true
})
motor.configurator.apply(TalonFXConfiguration().apply {
    CurrentLimits.StatorCurrentLimit = 80.0
    CurrentLimits.StatorCurrentLimitEnable = true
})
```

After the second `apply(...)`, what supply current limit is the motor
actually enforcing (with this course's stub defaults)?
