# Current Limits

A Falcon 500 pulls more than 200 amps if you ask it to. That is enough to
trip a breaker, brown out the roboRIO (the robot controller resets when
battery voltage drops too far), or burn a motor. **Current limits are not
optional** on a real robot.

There are two limits:

| Limit                    | Caps current...              | Protects                                   |
|--------------------------|------------------------------|--------------------------------------------|
| **Supply current limit** | drawn from the *battery*     | breakers and the battery; prevents brownouts |
| **Stator current limit** | through the *motor windings* | the motor from overheating; also caps torque |

Most teams set a supply limit near 40 A on intakes, rollers, and indexers.
Set a stator limit when you want to cap torque, for example so a jammed
intake does not strip its gears.

## The CurrentLimits config block

Current limits live on `TalonFXConfiguration.CurrentLimits`:

| Field                                     | Type      | Meaning                        |
|-------------------------------------------|-----------|--------------------------------|
| `CurrentLimits.SupplyCurrentLimit`        | `Double`  | the supply cap, in amps        |
| `CurrentLimits.SupplyCurrentLimitEnable`  | `Boolean` | turns the supply limit on      |
| `CurrentLimits.StatorCurrentLimit`        | `Double`  | the stator cap, in amps        |
| `CurrentLimits.StatorCurrentLimitEnable`  | `Boolean` | turns the stator limit on      |

The `Enable` flag is the trap. The limit value means nothing unless the
matching `Enable` flag is `true`. Do not depend on defaults. **Set the limit
and its enable flag explicitly, in the same configuration.**

`MotorOutput`, `CurrentLimits`, and every other config block live on the
same `TalonFXConfiguration` object. Set them all inside one `.apply { ... }`.

## Your task

Open `src/IntakeMotor.kt`. The `motor` is declared on CAN ID 22. Implement
`configure()` so the applied configuration has:

| Field                                    | Value   |
|------------------------------------------|---------|
| `MotorOutput.NeutralMode`                | `Coast` |
| `CurrentLimits.SupplyCurrentLimit`       | `40.0`  |
| `CurrentLimits.SupplyCurrentLimitEnable` | `true`  |

Intakes free-spin when disabled, so they use `Coast`.

## Hints

- One `TalonFXConfiguration().apply { ... }` block is enough.
- Setting the limit value but forgetting the `Enable` flag is the most
  common configuration bug in this API. The test catches it.
