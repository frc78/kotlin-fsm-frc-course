# Creating a TalonFX

Every motorized subsystem on the robot starts the same way: declare a motor
controller bound to a CAN ID, then send it control requests.

This course uses **TalonFX** — the Phoenix6 controller for Falcon 500 and
Kraken X60 motors. Your team's robot will have many of them, each with a
unique CAN ID set in Phoenix Tuner.

## The basics

```kotlin
val motor = TalonFX(canId = 17)
```

`canId` matches the device ID on the CAN bus. Two motors with the same ID
silently fight each other, so every TalonFX in a project gets a different
number.

Once you have a motor, you drive it with `setControl(...)`:

```kotlin
motor.setControl(VoltageOut(6.0))   // 6 volts forward
motor.setControl(VoltageOut(-6.0))  // 6 volts reverse
motor.stopMotor()                   // shorthand for NeutralOut
```

`VoltageOut` is the simplest control mode: a fixed voltage, regardless of
load. Later tasks layer configuration on top.

## Your task

Open `src/RollerMotor.kt`. The `RollerMotor` object needs to:

1. Declare a `TalonFX` named `motor` on **CAN ID 17**.
2. Implement `runForward()` to send `VoltageOut(6.0)`.
3. Implement `runReverse()` to send `VoltageOut(-6.0)`.
4. Implement `stop()` to call `motor.stopMotor()`.

## Hints

- `TalonFX` and `VoltageOut` are imported from `frc.stubs.*`.
- Use named arguments — `TalonFX(canId = 17)` reads better than the bare
  positional form.
