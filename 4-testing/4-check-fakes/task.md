# Check: Fake Hardware

In task 2, `LinearActuator` receives its hardware through the constructor
instead of building it:

```kotlin
class LinearActuator(
    private val motor: TalonFX,
    private val limit: DigitalInput,
) : Subsystem
```

Focus on the limit switch. What does the **test** gain from having the
`DigitalInput` passed in instead of created inside the class?
