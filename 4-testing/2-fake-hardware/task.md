# Fake Hardware via Dependency Injection

Pure functions are great for FSM logic, but a subsystem still has hardware
side effects: motor commands, sensor reads. To test those, you need to control
the hardware *from the test*.

## Pass hardware in

So far we have written subsystems as `object` singletons that *create* their
own motors and sensors:

```kotlin
object Intake : Subsystem {
    private val motor = TalonFX(canId = 22)   // hard-coded
}
```

That is the normal shape for a real robot. You only ever have one intake. But
it makes test reuse awkward. The singleton's state persists across tests, and
you cannot have two of them.

Alternative: make the subsystem a `class` that takes its hardware via the
**constructor**:

```kotlin
class LinearActuator(
    private val motor: TalonFX,
    private val limit: DigitalInput,
) : Subsystem {
    // ...
}
```

Now a test can create one, give it stub hardware, and assert behavior. The
test also controls the sensor: `limit.simulateValue(true)` makes the next
`limit.get()` return `true`.

```kotlin
val motor = TalonFX(canId = 99)
val limit = DigitalInput(channel = 9)
val actuator = LinearActuator(motor, limit)

actuator.commandedExtend = true
actuator.periodic()
limit.simulateValue(true)
actuator.periodic()

assertEquals(LinearActuator.State.EXTENDED, actuator.state)
assertEquals(VoltageOut(0.0), motor.lastRequest)
```

You can also create *two*, say a left and a right actuator, without their
state colliding.

This is **dependency injection** (DI): pass collaborators in instead of
building them inside. An FRC team that runs several instances of one
subsystem (left and right climber, four swerve modules) needs it.

## Your task

Implement the periodic logic in `src/LinearActuator.kt`. The class is set up
with constructor injection. You fill in the FSM.

States: `RETRACTED`, `EXTENDING`, `EXTENDED`. The actuator starts in
`RETRACTED`.

Transitions:

| Current     | Condition                | Next       |
|-------------|--------------------------|------------|
| `RETRACTED` | `commandedExtend`        | `EXTENDING`|
| `EXTENDING` | `limit.get()`            | `EXTENDED` |
| `EXTENDED`  | `!commandedExtend`       | `RETRACTED`|

If no row matches, stay in the current state.

Actions:

| State       | Motor               |
|-------------|---------------------|
| `RETRACTED` | `VoltageOut(0.0)`   |
| `EXTENDING` | `VoltageOut(6.0)`   |
| `EXTENDED`  | `VoltageOut(0.0)`   |

The `motor` and `limit` parameters are `private val`. They are stored on the
instance and available inside `periodic()`.
