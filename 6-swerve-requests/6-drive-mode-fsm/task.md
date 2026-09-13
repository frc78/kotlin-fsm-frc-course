# Drive-Mode FSM

You have built five `SwerveRequest` types in isolation. On a real robot the
drivetrain switches between them from driver inputs and the state of the rest
of the robot. That decision is an FSM.

## States

| State          | Driver intent                     | Request                   |
|----------------|-----------------------------------|---------------------------|
| `TELEOP_FIELD` | Default driving                   | `FieldCentric`            |
| `TELEOP_ROBOT` | Driver toggled robot-relative     | `RobotCentric`            |
| `AIMING`       | Driver pressed "lock heading"     | `FieldCentricFacingAngle` |
| `BRAKED`       | Driver pressed "brake" while still| `SwerveDriveBrake`        |

## Driver inputs (already on the object)

```kotlin
var requestedVx: Double            // m/s, field- or robot-relative
var requestedVy: Double
var requestedOmega: Double         // rad/s, used in TELEOP_FIELD/ROBOT only
var commandedRobotRelative: Boolean
var commandedAim: Boolean
var aimTargetDegrees: Double       // used when commandedAim is true
var commandedBrake: Boolean
```

## Transitions

`stateTransitions()` picks one state per tick. Check the rows in this order.
The first row whose condition is true wins.

| Priority | Condition                                                          | Next state     |
|----------|--------------------------------------------------------------------|----------------|
| 1        | `commandedBrake` and all three requested velocities are still      | `BRAKED`       |
| 2        | `commandedAim`                                                     | `AIMING`       |
| 3        | `commandedRobotRelative`                                           | `TELEOP_ROBOT` |
| 4        | none of the above                                                  | `TELEOP_FIELD` |

"Still" means the magnitude of `requestedVx`, `requestedVy`, and
`requestedOmega` is each below 0.05. The brake never fights a moving stick.
If the driver holds brake while a stick is off center, row 1 does not match
and the lower rows decide.

## Actions

`stateActions()` is a `when (state)` with one branch per state. Each branch
builds the request from the table above and hands it to
`drivetrain.setControl(...)`:

| State          | Request fields                                          |
|----------------|---------------------------------------------------------|
| `TELEOP_FIELD` | `requestedVx`, `requestedVy`, `requestedOmega`          |
| `TELEOP_ROBOT` | `requestedVx`, `requestedVy`, `requestedOmega`          |
| `AIMING`       | `requestedVx`, `requestedVy`, `aimTargetDegrees`        |
| `BRAKED`       | none                                                    |

## Kotlin you need: `when` without a subject

Until now every `when` had a subject, `when (state)`. A `when` with no
subject takes a boolean condition in each branch. The first true branch runs:

```kotlin
val speedName = when {
    speed > 3.0 -> "fast"
    speed > 1.0 -> "medium"
    else -> "slow"
}
```

This is the natural shape for a priority list.

## Your task

Open `src/DriveModeFsm.kt`. Implement `stateTransitions()` and
`stateActions()`.

The starter's `reset()` sends `Idle`, one request you have not built. A real
robot applies `Idle` when no drive command should be active, for example
while disabled.
