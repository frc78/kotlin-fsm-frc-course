# Sequencing as a Rule

Task 2 commanded the elevator and the arm on the same tick. On the real robot
that breaks things. The arm swings through the space the elevator carriage
moves in. Going up, the arm must swing clear before the carriage rises. Going
down, the carriage must come down while the arm waits at a safe angle, and
the arm swings to its final angle only after the carriage arrives.

## The rule

The sequence is a rule on sensor readings, evaluated every tick inside
`stateActions()`. `state` is the target pose. `SAFE_ARM_DEGREES` is `90.0`.

| Case                                                       | Elevator command                                                                                   | Arm command                                                                                                          |
|------------------------------------------------------------|----------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------|
| going down: `state.elevatorRotations < Elevator.position`  | `state.elevatorRotations` every tick                                                               | `SAFE_ARM_DEGREES` until `abs(Elevator.position - state.elevatorRotations) < 0.5`, then `state.armDegrees`            |
| otherwise (up, or same height)                             | `state.elevatorRotations`, but only when `abs(Arm.angle - state.armDegrees) < 2.0`; no call otherwise, so the last target holds | `state.armDegrees` every tick                                                                                        |

On the way up the arm moves first. The elevator receives no new target until
the arm is within 2 degrees of where the pose wants it. On the way down the
elevator moves first. The arm holds the safe angle until the carriage is
within 0.5 rotations of the pose height.

## Why the rule needs no extra state

Task 5 of the old version of this lesson tracked phases in a second state
machine. This rule does not. Each tick it reads two numbers, `Elevator.position`
and `Arm.angle`, and compares them with the pose. The numbers already say
which phase the robot is in. When the driver changes the pose mid-move, the
next tick reads the new pose against the same numbers and does the right thing
with no reset and no abort logic. This is how FRC team 78's superstructure
works.

## What is already in the file

`stateTransitions()` is task 2's answer. `periodic()` runs transitions, then
actions, then one simulation step for each mechanism. The stubs move a fixed
amount per tick: the elevator up to 3.0 rotations, the arm up to 45 degrees.
`Elevator.target` and `Arm.target` hold the last `goTo(...)` argument, and the
hidden tests read them to check what you commanded. `atPosition` is true only
when both targets equal the pose setpoints and both mechanisms have arrived,
so a mechanism that is still waiting for its turn does not count as settled.

## Your task

Implement `stateActions()` in `src/SuperStructure.kt` per the rule table.
