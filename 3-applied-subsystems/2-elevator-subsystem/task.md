# Applied Elevator

This subsystem shows off enum-with-properties at its best. Every elevator state
*is* its setpoint — no `when` block needed inside `stateActions()` to look up
the target.

## State

```kotlin
enum class State(val targetRotations: Double) {
    STOWED(0.0),
    LOW(4.0),
    MID(9.5),
    HIGH(14.5);
}
```

(Already provided.)

## Driver input

A single `var commandedTarget: State` — the driver tells the elevator where to
go by setting it directly to a state value.

## Transitions

The elevator just follows the driver's target:

```
state = commandedTarget
```

That's it. One line. No `when`. Why so trivial? Because the *interesting* logic
("where do I want to be?") is in the `commandedTarget` field, set elsewhere by
driver-input code. The elevator subsystem's job is to move there.

## Actions

Command the motor to the current state's target — every tick:

```kotlin
motor.setControl(PositionVoltage(state.targetRotations))
```

`PositionVoltage` is a Phoenix6 closed-loop control request that drives the
motor to the given rotational position. Sending it every tick is fine — the
controller just keeps holding.

## At-target check

Most subsystems also expose a status query so other code can ask "are we
there?" Implement `atTarget()` to return `true` when the motor's position is
within 0.1 rotations of the current state's target.

## Your task

Three small things in `src/Elevator.kt`:

1. `stateTransitions()` — copy `commandedTarget` into `state`.
2. `stateActions()`     — send `PositionVoltage(state.targetRotations)`.
3. `atTarget()`         — return whether motor position is close to target.

## Hint

`kotlin.math.abs` is already imported.
