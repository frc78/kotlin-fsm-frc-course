# The Intake Owns Its Piece

The superstructure knows where the elevator and arm are. It knows nothing
about game pieces. The intake decides by itself whether it holds one, and
it decides by itself when to let go. Nobody commands the intake. It reads
the driver and the superstructure, and it acts.

## States carry their motor request

`Intake.State` is an enum with a property, the lesson 1 pattern. Each state
carries the `ControlRequest` the roller motor gets while the intake is in
it:

| State      | `control`           | Rollers               |
|------------|---------------------|-----------------------|
| `IDLE`     | `NeutralOut`        | off                   |
| `INTAKING` | `VoltageOut(6.0)`   | pull a piece in       |
| `HOLDING`  | `VoltageOut(1.0)`   | light grip on a piece |
| `EJECTING` | `VoltageOut(-6.0)`  | push the piece out    |

`stateActions()` sends `state.control` to `motor` every tick. That is the
whole method.

## Detecting a piece by stall current

The intake has no beam-break. A roller that has pulled a piece in stalls
against it, and a stalled motor draws more current. One noisy reading is not
enough. `stallDebounce` is a `Debouncer(5)`: its `calculate(input)` returns
`true` only after `input` has been `true` for 5 calls in a row. One `false`
resets the count.

Call it once per tick, at the top of `stateTransitions()`, whatever the
state is:

```kotlin
val stalled = stallDebounce.calculate(motor.getStatorCurrent() > 10.0)
```

A debouncer counts calls, so it must see every tick. WPILib's `Debouncer`
does the same job in seconds.

## Ejecting only when it is safe

Scoring means the piece leaves at `L2` or `L4`. The intake must not eject
while the superstructure is still moving, or the piece falls between the
reef branches. The intake reads two values from its peer, like sensors:

| Read                        | Meaning                                  |
|-----------------------------|------------------------------------------|
| `SuperStructure.state`      | the pose the superstructure is in        |
| `SuperStructure.atPosition` | the elevator and arm have both arrived   |

A scoring pose is `Pose.L2` or `Pose.L4`.

## Transitions

Inside each state the rows are checked top to bottom. The first match wins.

| Current    | Condition                                                   | Next       |
|------------|-------------------------------------------------------------|------------|
| `IDLE`     | `OI.intake`                                                 | `INTAKING` |
| `INTAKING` | `OI.home`                                                   | `IDLE`     |
| `INTAKING` | `stalled`                                                   | `HOLDING`  |
| `HOLDING`  | `OI.score` and a scoring pose and `SuperStructure.atPosition` | `EJECTING` |
| `EJECTING` | `!OI.score`                                                 | `IDLE`     |

`HOLDING` has no way back to `IDLE` except through a score. A piece that is
held stays held.

## Your task

Open `src/Intake.kt`. The enum, the motor, the debouncer, `periodic()`, and
`reset()` are written. Implement:

1. `stateTransitions()`: the debounce line, then the table above.
2. `stateActions()`: send the current state's `control` to the motor.

`src/SuperStructure.kt` is the solved task 3 superstructure. Read it, do not
change it.
