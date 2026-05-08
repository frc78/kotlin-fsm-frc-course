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

`stateTransitions()`:

```kotlin
state = when {
    commandedBrake          -> State.BRAKED
    commandedAim            -> State.AIMING
    commandedRobotRelative  -> State.TELEOP_ROBOT
    else                    -> State.TELEOP_FIELD
}
```

(Note this is a `when` *without* a subject — each branch is a boolean
condition. The first matching branch wins.)

`stateActions()`: a `when (state)` block that builds and applies the right
request per the table above.

## Hint

For the request constructors, use the same builder shape from earlier tasks.
Pull the velocity values from the `requested*` fields on the object.
