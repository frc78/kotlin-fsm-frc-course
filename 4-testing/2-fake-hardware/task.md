# Fake Hardware via Dependency Injection

Pure functions are great for FSM logic, but a subsystem still has hardware
side effects: motor commands, sensor reads. To test those, you need to control
the hardware *from the test*.

## The trick: pass hardware in

So far we've written subsystems as `object` singletons that *create* their own
motors and sensors:

```kotlin
object Intake : Subsystem {
    private val motor = TalonFX(canId = 22)   // hard-coded
}
```

That's idiomatic for a real robot — you only ever have one intake. But it
makes test reuse awkward (the singleton's state persists across tests, and you
can't have two of them).

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

Now a test can create one, give it stub hardware, and assert behavior:

```kotlin
val motor = TalonFX(canId = 99)
val limit = DigitalInput(channel = 9)
val actuator = LinearActuator(motor, limit)

actuator.commandedExtend = true
actuator.periodic()

assertEquals(LinearActuator.State.EXTENDING, actuator.state)
assertEquals(VoltageOut(6.0), motor.lastRequest)
```

You can also create *two* — say a left and right actuator — without their
state colliding.

This is **dependency injection** (DI), which is just a fancy name for "pass
collaborators in instead of building them inside." For a real FRC team that
runs multiple instances of the same subsystem (left + right climber, four
swerve modules, etc.), DI is essential.

## Your task

Implement the periodic logic in `src/LinearActuator.kt`. The class is set up
with constructor injection; you fill in the FSM.

States: `RETRACTED`, `EXTENDING`, `EXTENDED`.

Transitions:

| Current     | Condition                | Next       |
|-------------|--------------------------|------------|
| `RETRACTED` | `commandedExtend`        | `EXTENDING`|
| `EXTENDING` | `limit.get()`            | `EXTENDED` |
| `EXTENDED`  | `!commandedExtend`       | `RETRACTED`|

Actions:

| State       | Motor               |
|-------------|---------------------|
| `RETRACTED` | `VoltageOut(0.0)`   |
| `EXTENDING` | `VoltageOut(6.0)`   |
| `EXTENDED`  | `VoltageOut(0.0)`   |

The `motor` and `limit` parameters are `private val` — they're stored on the
instance, accessible inside `periodic()`.
