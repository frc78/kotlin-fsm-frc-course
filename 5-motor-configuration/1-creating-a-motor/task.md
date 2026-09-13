# Creating a TalonFX

Every FSM you built since Lesson 2 commanded a TalonFX. This lesson shows
how a motor is constructed and configured before the first control request
reaches it.

Every motorized subsystem on the robot starts the same way: declare a motor
controller bound to a CAN ID, then send it control requests.

This course uses **TalonFX**, the Phoenix6 controller for Falcon 500 and
Kraken X60 motors. Your team's robot has many of them. Each one has a
unique CAN ID set in Phoenix Tuner.

## Constructing a TalonFX

```kotlin
val motor = TalonFX(canId = 3)
```

`canId` matches the device ID on the CAN bus. Two motors with the same ID
fight each other without any error message, so every TalonFX in a project
gets a different number.

> **Real Phoenix6:** the real constructor is `TalonFX(deviceId)` or
> `TalonFX(deviceId, canBus)`. The name `canId` exists only in this
> course's stub. Real code writes `TalonFX(3)` or `TalonFX(3, "canivore")`.

## Driving the motor

The simplest control mode is **voltage**:

```kotlin
motor.setControl(VoltageOut(2.0))   // 2 volts forward (positive)
```

`VoltageOut` takes any `Double`. Positive runs the motor in its configured
"forward" direction. Negative runs it the other way. `DutyCycleOut` instead
takes a *fraction* of battery voltage, from -1.0 to 1.0. To release the
motor, call `motor.stopMotor()`. It sends a `NeutralOut` request.

## Your task

Open `src/RollerMotor.kt`. The `RollerMotor` object needs to:

| Member         | Requirement                                   |
|----------------|-----------------------------------------------|
| `motor`        | a `TalonFX` on **CAN ID 17**                  |
| `runForward()` | drive the motor forward at **6 volts**        |
| `runReverse()` | drive the motor backward at **6 volts**       |
| `stop()`       | release the motor with `stopMotor()`          |

`reset()` is written for you. The tests call it before each check.

## Hints

- `TalonFX`, `VoltageOut`, and the rest are imported from `frc.stubs.*`.
- In this course, write `TalonFX(canId = …)` so the number has a name.
