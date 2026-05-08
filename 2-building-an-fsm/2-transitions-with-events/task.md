# Transitions from Multiple Inputs

A `stateTransitions()` block reads from whatever inputs the subsystem cares
about. There are two common kinds:

1. **Driver intent** — booleans set by joystick code: `commandedIntake`,
   `commandedShoot`, `commandedReset`. These come *into* the subsystem.
2. **Sensor reads** — values pulled directly from hardware:
   `canRange.getDistance()`, `motor.getPosition()`, `limitSwitch.get()`.

Inside `stateTransitions()` you mix both freely:

```kotlin
State.INTAKING -> if (canRange.getDistance() < 0.05) State.HOLDING else State.INTAKING
```

The key idea: **each transition is a logical OR of conditions, but each
condition can mix driver intent AND sensor reads.**

## CANrange

`CANrange` is a CAN-connected time-of-flight distance sensor (Phoenix6). The
relevant call is:

```kotlin
canRange.getDistance(): Double   // meters; smaller means closer
```

In this task, we use it to detect a game piece in the intake: if distance drops
below 0.05 m (5 cm), a piece has arrived.

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
