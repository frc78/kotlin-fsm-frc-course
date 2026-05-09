# Closed-Loop Control: Slot0 PID

`VoltageOut` and `DutyCycleOut` are *open-loop* — you say "6 volts" and
the motor spins however fast 6 volts happens to spin it that day. For a
flywheel that needs to hit a specific RPM, that's not enough. You want
**closed-loop** control: tell the motor a target velocity, and let it
adjust voltage to get there.

Phoenix6 supports closed-loop control on the motor controller itself.
You configure gains once, then send `VelocityVoltage` (or
`PositionVoltage`) requests to drive a target.

## PID gains live in slots

A TalonFX has multiple gain slots (`Slot0`, `Slot1`, `Slot2`). Most
subsystems only ever use `Slot0`.

The gains you'll see in real code:

| Gain | Meaning                                                        |
|------|----------------------------------------------------------------|
| `kP` | Proportional — react to error, larger = more aggressive        |
| `kI` | Integral — react to *accumulated* error (often 0)              |
| `kD` | Derivative — react to error *rate* (often 0)                   |
| `kS` | Static feedforward — voltage to overcome static friction       |
| `kV` | Velocity feedforward — voltage per unit of target velocity     |
| `kA` | Acceleration feedforward — voltage per unit of acceleration    |

For a flywheel, **`kV` does most of the work** (it predicts what voltage
*should* sustain a given RPM), and `kP` cleans up the rest.

## The Slot0 config block

PID gains live on `TalonFXConfiguration.Slot0`. Each gain is a `Double`
field with the same name as in the table above (`kP`, `kI`, `kD`, `kS`,
`kV`, `kA`). Set the gains you care about; leave the rest at their
default of `0.0`.

## Running closed loop

Once gains are configured, drive the motor with `VelocityVoltage(rps)`
where `rps` is the target velocity in rotations per second. Phoenix
uses the configured gains to pick the voltage; you just specify the
target.

`VelocityVoltage` is a `data class`, so you can construct it
positionally or with the named argument `rotationsPerSecond`.

## Your task

Open `src/Flywheel.kt`. The `motor` is already declared on CAN ID 60.

1. Implement `configure()` so the applied configuration has the
   following Slot0 gains:
   - `kV = 0.12`
   - `kP = 0.25`
2. Implement `runAtRps(rps: Double)` so it commands the motor to that
   target velocity using a `VelocityVoltage` request.

## Hints

- The Slot0 fields you don't mention default to `0.0` — that's the
  standard starting point for `kI`, `kD`, `kS`, and `kA` on a flywheel.
