# Closed-Loop Control: Slot0 PID

`VoltageOut` and `DutyCycleOut` are *open-loop*. You say "6 volts" and the
motor spins as fast as 6 volts spins it that day. A flywheel that must hit a
specific speed needs **closed-loop** control: you give the motor a target
velocity, and the motor adjusts its own voltage to reach it.

Phoenix6 runs the closed loop on the motor controller itself. You configure
gains once, then send `VelocityVoltage` (or `PositionVoltage`) requests that
carry a target.

## PID gains live in slots

A TalonFX has several gain slots (`Slot0`, `Slot1`, `Slot2`). Most
subsystems only use `Slot0`.

| Gain | Meaning                                                        | Unit (voltage requests)      |
|------|----------------------------------------------------------------|------------------------------|
| `kP` | Proportional: output per unit of error                         | V per rotation (or per rps)  |
| `kI` | Integral: output per unit of *accumulated* error (often 0)     | V per rotation-second        |
| `kD` | Derivative: output per unit of error *rate* (often 0)          | V per rps                    |
| `kS` | Static feedforward: voltage to overcome friction               | V                            |
| `kV` | Velocity feedforward: voltage per unit of target velocity      | V per rps                    |
| `kA` | Acceleration feedforward: voltage per unit of acceleration     | V per rps²                   |
| `kG` | Gravity feedforward: voltage to hold against gravity           | V                            |

Two worked lines show what the numbers do. With `kV = 0.12`, a target of
80 rotations per second (rps) gets `80 × 0.12 = 9.6 V` of feedforward
before any error exists. With `kP = 0.25`, an error of 10 rps adds
`10 × 0.25 = 2.5 V` on top.

For a flywheel, **`kV` does most of the work**. It predicts the voltage
that sustains a given speed. `kP` corrects the rest.

## The Slot0 config block

PID gains live on `TalonFXConfiguration.Slot0`. Each gain is a `Double`
field with the same name as in the table (`kP`, `kI`, `kD`, `kS`, `kV`,
`kA`, `kG`). Set the gains you need. Leave the rest at their default `0.0`.

## Running closed loop

After the gains are configured, command a velocity with a `VelocityVoltage`
request. Its argument is the target velocity in **rotations per second**.
Phoenix uses the configured gains to pick the voltage. You only give the
target.

In this course's stub, `VelocityVoltage` is a `data class` with one
parameter: the target velocity.

## Your task

Open `src/Flywheel.kt`. The `motor` is declared on CAN ID 60.

| Member                   | Requirement                                              |
|--------------------------|----------------------------------------------------------|
| `configure()`            | apply a configuration with `Slot0.kV = 0.12`, `Slot0.kP = 0.25` |
| `runAtRps(rps: Double)`  | send a `VelocityVoltage` request with `rps` as the target |

## Hints

- The Slot0 fields you do not set default to `0.0`. That is the normal
  starting point for `kI`, `kD`, `kS`, `kA`, and `kG` on a flywheel.
