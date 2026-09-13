# Transitions from Multiple Inputs

A `stateTransitions()` block reads every input the subsystem cares about.
There are two common kinds:

1. **Driver intent**: booleans set by joystick code, such as
   `commandedIntake`, `commandedShoot`, `commandedReset`. These come *into*
   the subsystem.
2. **Sensor reads**: values read from hardware, such as
   `canRange.getDistance()`, `motor.getPosition()`, `limitSwitch.get()`.

In this task, some transitions fire on driver intent (`commandedIntake`)
and others on a sensor read (`canRange.getDistance()`), all in the same
`when` block. One `stateTransitions()` method watches every kind of input.
A condition that combines driver intent *and* a sensor read with `&&` is
called a *guard*. Task 4 covers guards.

## CANrange

`CANrange` is a CAN-connected time-of-flight distance sensor (Phoenix6). The
call you need is:

```kotlin
canRange.getDistance(): Double   // meters; smaller means closer
```

If the distance drops below 0.05 m (5 cm), a game piece has arrived.

> **Real Phoenix6 detail:** real sensor reads return `StatusSignal` objects.
> You would write `canRange.distance.value` or
> `motor.position.valueAsDouble`. The stubs flatten these to plain `Double`
> values, so the FSM logic reads the same.

## Your task

Open `src/IntakeIntro.kt`. This time only the declarations are written:

| Declaration             | Meaning                                  |
|-------------------------|------------------------------------------|
| `enum class State`      | `IDLE`, `INTAKING`, `HOLDING`            |
| `motor`                 | `TalonFX` on CAN id 22                   |
| `canRange`              | `CANrange` on CAN id 33                  |
| `state`                 | the current state, starts `IDLE`         |
| `commandedIntake`       | driver intent, starts `false`            |

Write the bodies of all four methods.

**`periodic()`:** call `stateTransitions()`, then `stateActions()`, in that
order, as in task 1.

**`stateActions()`:**

| State      | Motor             |
|------------|-------------------|
| `IDLE`     | `VoltageOut(0.0)` |
| `INTAKING` | `VoltageOut(6.0)` |
| `HOLDING`  | `VoltageOut(1.0)` |

`HOLDING` runs at 1 V. That is enough to keep the piece pressed against the
mechanism without crushing it.

**`stateTransitions()`** (first matching row wins):

| Current     | Condition                          | Next       |
|-------------|------------------------------------|------------|
| `IDLE`      | `commandedIntake`                  | `INTAKING` |
| `INTAKING`  | `canRange.getDistance() < 0.05`    | `HOLDING`  |
| `INTAKING`  | `!commandedIntake`                 | `IDLE`     |
| `HOLDING`   | `!commandedIntake`                 | `IDLE`     |

If the driver releases the button while `INTAKING` and no piece is
detected, return to `IDLE`. If the driver releases while `HOLDING`, also
return to `IDLE`. Lesson 3 adds a real eject state.

**`reset()`:** the hidden tests call this before every test. It must put
the object back to its starting values:

| Field or device   | Reset to                                              |
|-------------------|-------------------------------------------------------|
| `state`           | `State.IDLE`                                          |
| `commandedIntake` | `false`                                               |
| `motor`           | `motor.stopMotor()`                                   |
| `canRange`        | `canRange.simulateDistance(Double.POSITIVE_INFINITY)` |

`Double.POSITIVE_INFINITY` is the largest possible distance. It means "no
piece in sight".
