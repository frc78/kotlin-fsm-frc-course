# Transitions from Multiple Inputs

A `stateTransitions()` block reads from whatever inputs the subsystem cares
about. There are two common kinds:

1. **Driver intent** — booleans set by joystick code: `commandedIntake`,
   `commandedShoot`, `commandedReset`. These come *into* the subsystem.
2. **Sensor reads** — values pulled directly from hardware:
   `canRange.getDistance()`, `motor.getPosition()`, `limitSwitch.get()`.

Inside `stateTransitions()` you read both freely. In this task, some
transitions are keyed off driver intent (`commandedIntake`) while others
are keyed off a sensor read (`canRange.getDistance()`) — all in the same
`when` block.

The key idea: **one `stateTransitions()` method watches every kind of
input the subsystem cares about.** Combining driver intent *and* a sensor
read in a single condition with `&&` is called a *guard* — that's the
subject of task 4.

## CANrange

`CANrange` is a CAN-connected time-of-flight distance sensor (Phoenix6). The
relevant call is:

```kotlin
canRange.getDistance(): Double   // meters; smaller means closer
```

In this task, we use it to detect a game piece in the intake: if distance drops
below 0.05 m (5 cm), a piece has arrived.

> **Real Phoenix6 detail:** real sensor reads return `StatusSignal` objects —
> you'd write `canRange.distance.value` or `motor.position.valueAsDouble`. The
> stubs flatten these to plain `Double`s, so the FSM logic reads the same.

## Your task

Open `src/IntakeIntro.kt`. The skeleton is wired up. Implement the two methods.

**`stateActions()`:**

| State      | Motor             |
|------------|-------------------|
| `IDLE`     | `VoltageOut(0.0)` |
| `INTAKING` | `VoltageOut(6.0)` |
| `HOLDING`  | `VoltageOut(1.0)` |

(`HOLDING` runs at 1 V — just enough to keep the piece pressed against the
mechanism without crushing it.)

**`stateTransitions()`:**

| Current     | Condition                          | Next       |
|-------------|------------------------------------|------------|
| `IDLE`      | `commandedIntake`                  | `INTAKING` |
| `INTAKING`  | `canRange.getDistance() < 0.05`    | `HOLDING`  |
| `INTAKING`  | `!commandedIntake`                 | `IDLE`     |
| `HOLDING`   | `!commandedIntake`                 | `IDLE`     |

When `INTAKING` and the driver releases the button while no piece is detected,
return to `IDLE`. When `HOLDING` and the driver releases, also return to `IDLE`
(eject by simply stopping; in lesson 3 we'll add a real eject state).
