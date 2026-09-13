# Applied Elevator

Every elevator state carries its own setpoint. This is the enum property from
lesson 1 task 11 at work. `stateActions()` does not need a `when` block to look
up the target.

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

`var commandedTarget: State`. The driver code sets this to the place the
elevator must go.

## Transitions

The elevator finishes its current move before it accepts a new target. This
keeps the mechanism from reversing part way along its travel.

| Current | Condition    | Next              |
|---------|--------------|-------------------|
| any     | `atTarget()` | `commandedTarget` |
| any     | otherwise    | stay              |

## Actions

| State | Motor                                            |
|-------|--------------------------------------------------|
| any   | `PositionVoltage` with the state's `targetRotations` |

`PositionVoltage` is a Phoenix6 closed-loop request. It moves the motor to a
rotation count and holds it there. Sending it every tick is correct. The
controller keeps holding.

> **Stub detail:** the stub snaps the simulated position to the requested
> rotations at once. The tests use `motor.setPosition(...)` to place the
> elevator part way along its travel.

## At-target check

Other code needs to ask "is the elevator there yet?". `atTarget(): Boolean`
returns `true` when `motor.getPosition()` is within 0.1 rotations of the
current state's `targetRotations`, on either side of it.

## Your task

Implement `stateTransitions()`, `stateActions()`, and `atTarget()` in
`src/Elevator.kt`. `kotlin.math.abs` is already imported.
