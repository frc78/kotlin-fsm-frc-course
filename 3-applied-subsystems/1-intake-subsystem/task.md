# Applied Intake

Time to build a complete intake subsystem the way your team would write it.

This is the same shape as `IntakeIntro` from Lesson 2, with one extra state for
ejecting and proper handling of two driver commands.

## States

```
IDLE        --commandedIntake-->     INTAKING
INTAKING    --piece detected-->      HOLDING
INTAKING    --!commandedIntake-->    IDLE
HOLDING     --commandedEject-->      EJECTING
HOLDING     --piece lost-->          IDLE
EJECTING    --!commandedEject-->     IDLE
IDLE        --commandedEject-->      EJECTING
```

A piece is **detected** when `canRange.getDistance() < 0.05`.
A piece is **lost**     when `canRange.getDistance() > 0.10`.

(The 0.05 / 0.10 hysteresis prevents flapping around a single threshold.)

## Actions

| State      | Motor               |
|------------|---------------------|
| `IDLE`     | `VoltageOut(0.0)`   |
| `INTAKING` | `VoltageOut(8.0)`   |
| `HOLDING`  | `VoltageOut(0.5)`   |
| `EJECTING` | `VoltageOut(-8.0)`  |

## Your task

Implement both `stateTransitions()` and `stateActions()` in `src/Intake.kt`.

The state enum, hardware, driver inputs, and `reset()` are provided.
