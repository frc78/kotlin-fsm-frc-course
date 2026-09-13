# Applied Intake

This intake has the same shape as `IntakeIntro` from lesson 2. It adds one
state for ejecting and a second driver command.

## Hardware and inputs

| Name              | Type      | Meaning                              |
|-------------------|-----------|--------------------------------------|
| `motor`           | `TalonFX` | roller motor                         |
| `canRange`        | `CANrange`| distance sensor at the intake mouth  |
| `commandedIntake` | `Boolean` | the driver holds the intake button   |
| `commandedEject`  | `Boolean` | the driver holds the eject button    |

## Two thresholds

The sensor reading answers two different questions. Each question has its own
threshold, and only one of them applies in a given state.

| State      | Threshold that applies            | Meaning                  |
|------------|-----------------------------------|--------------------------|
| `INTAKING` | `canRange.getDistance() < 0.05`   | a piece is **detected**  |
| `HOLDING`  | `canRange.getDistance() > 0.10`   | the piece is **lost**    |

Between 0.05 m and 0.10 m nothing changes. This gap is called hysteresis. It
stops the state from flapping when the reading sits near one value.

> **Real Phoenix6 detail:** the real CANrange can do this in firmware.
> `ProximityParamsConfigs.ProximityThreshold` and `ProximityHysteresis` set
> the band, and `getIsDetected()` returns the result. This task writes the
> band in Kotlin so you can see how it works.

## Transitions

Rows are checked from top to bottom. The first matching row wins.

| Current    | Condition          | Next       |
|------------|--------------------|------------|
| `IDLE`     | `commandedEject`   | `EJECTING` |
| `IDLE`     | `commandedIntake`  | `INTAKING` |
| `INTAKING` | piece detected     | `HOLDING`  |
| `INTAKING` | `!commandedIntake` | `IDLE`     |
| `HOLDING`  | `commandedEject`   | `EJECTING` |
| `HOLDING`  | piece lost         | `IDLE`     |
| `EJECTING` | `!commandedEject`  | `IDLE`     |

## Actions

| State      | Motor               |
|------------|---------------------|
| `IDLE`     | `VoltageOut(0.0)`   |
| `INTAKING` | `VoltageOut(8.0)`   |
| `HOLDING`  | `VoltageOut(0.5)`   |
| `EJECTING` | `VoltageOut(-8.0)`  |

## Your task

Implement `stateTransitions()` and `stateActions()` in `src/Intake.kt`.

The state enum, hardware, driver inputs, and `reset()` are provided.
