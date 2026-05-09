# Drive-Mode FSM

You've built five different `SwerveRequest` types in isolation. In a real
robot, the drivetrain switches between them based on driver inputs and the
state of the rest of the robot — exactly the kind of decision an FSM is good
at.

This task brings the FSM theme of the course back to the drivetrain. The
result is the swerve request piece of a real teleop control loop.

## States

| State          | Driver intent                              | Request                       |
|----------------|--------------------------------------------|-------------------------------|
| `TELEOP_FIELD` | Default driving                            | `FieldCentric`                |
| `TELEOP_ROBOT` | Driver toggled robot-relative              | `RobotCentric`                |
| `AIMING`       | Driver pressed "lock heading"              | `FieldCentricFacingAngle`     |
| `BRAKED`       | Driver pressed "brake"                     | `SwerveDriveBrake`            |

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

## Transition priority

When multiple buttons are held at once, prefer the **higher-priority** mode:

```
brake > aim > robot-relative > field
```

So if the driver holds both brake and aim, the robot brakes. If they hold
both robot-relative and aim, the robot aims (field-centric facing-angle).

This is a classic FSM use of priority: `stateTransitions()` picks one state
per tick by checking conditions in priority order.

## Your task

Open `src/DriveModeFsm.kt`. Implement `stateTransitions()` and
`stateActions()`.

**`stateTransitions()`**: assign `state` based on which command flag is
set. Use a `when` *without* a subject — each branch is a boolean
condition, and the first matching branch wins. Order the branches by
priority (`brake` → `aim` → `robot-relative` → `field`) so the highest
priority that's currently asserted is the one that takes effect. If no
command flag is set, fall through to `TELEOP_FIELD`.

**`stateActions()`**: a `when (state)` block with one branch per state.
Each branch builds the appropriate `SwerveRequest` (per the table
above) and hands it to `drivetrain.setControl(...)`.

## Hint

For the request builders, reuse the shapes from earlier tasks: the
`Field`/`Robot`Centric requests want `vx`/`vy`/`omega` from the
`requested*` fields, the `FieldCentricFacingAngle` wants `vx`/`vy` plus
`aimTargetDegrees`, and `SwerveDriveBrake` is a singleton.
