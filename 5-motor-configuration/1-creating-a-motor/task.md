# Creating a TalonFX

Every motorized subsystem on the robot starts the same way: declare a motor
controller bound to a CAN ID, then send it control requests.

This course uses **TalonFX** — the Phoenix6 controller for Falcon 500 and
Kraken X60 motors. Your team's robot will have many of them, each with a
unique CAN ID set in Phoenix Tuner.

## Constructing a TalonFX

```kotlin
val motor = TalonFX(canId = 3)
```

`canId` matches the device ID on the CAN bus. Two motors with the same ID
silently fight each other, so every TalonFX in a project gets a different
number.

## Driving the motor

The simplest control mode is **voltage**:

```kotlin
motor.setControl(VoltageOut(2.0))   // 2 volts forward (positive)
```

`VoltageOut` takes any `Double` — positive runs the motor in its
configured "forward" direction, negative runs it the other way. To
release the motor, use `motor.stopMotor()`, which is shorthand for
sending a `NeutralOut` request.

## Your task

Open `src/RollerMotor.kt`. The `RollerMotor` object needs to:

1. Declare a `TalonFX` named `motor` on **CAN ID 17**.
2. `runForward()` should drive the motor forward at **6 volts**.
3. `runReverse()` should drive it backward at **6 volts**.
4. `stop()` should release the motor (`stopMotor()`).

## Hints

- `TalonFX`, `VoltageOut`, and the rest are imported from `frc.stubs.*`.
- Use named arguments when constructing the TalonFX — `TalonFX(canId = 17)`
  reads better than the bare positional form.
