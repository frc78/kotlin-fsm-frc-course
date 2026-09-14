# The Superstructure Is One Peer FSM

The superstructure is an FSM like the lesson 3 subsystems. Its state type is
`Pose`. Its actions send setpoints to the elevator and the arm. It has two
kinds of input:

| Input                     | Source                                | Read as                    |
|---------------------------|---------------------------------------|----------------------------|
| driver buttons            | `OI.home`, `OI.intake`, `OI.scoreL2`, `OI.scoreL4`, `OI.prepareClimb`, `OI.climb` | booleans |
| the intake's state        | `Intake.state`                        | another FSM's `state`, like a sensor |

`OI` is the operator interface stub. Each field is `true` while the driver
holds that button. `Intake` in this task is a stand-in with a `state` field
that tests set. Task 4 replaces it with the real intake FSM. The superstructure
never writes to `Intake`. It reads `Intake.state` to learn whether the robot
holds a piece, the same way it reads a button.

## The mechanisms

`Elevator` and `Arm` are stubs that mirror the real subsystems:

| Call or field                 | Meaning                                            |
|-------------------------------|----------------------------------------------------|
| `Elevator.goTo(rotations)`    | set the elevator target                            |
| `Arm.goTo(degrees)`           | set the arm target                                 |
| `Elevator.atPosition`         | `true` when the carriage is within 0.1 rotations   |
| `Arm.atPosition`              | `true` when the arm is within 2 degrees            |

Both move a fixed step per tick: 3.0 rotations and 45.0 degrees. The
`simulationPeriodic()` calls in `periodic()` advance them. `atPosition` on the
superstructure is given: it is `true` when both mechanisms report `true`.

## Transitions

Inside each state the rows are checked top to bottom. The first match wins.
`holding` means `Intake.state == Intake.State.HOLDING`.

| Current          | Condition                 | Next             |
|------------------|---------------------------|------------------|
| `HOME`           | `OI.intake && !holding`   | `CORAL_STATION`  |
| `HOME`           | `holding && OI.scoreL2`   | `L2`             |
| `HOME`           | `holding && OI.scoreL4`   | `L4`             |
| `HOME`           | `OI.prepareClimb`         | `READY_TO_CLIMB` |
| `CORAL_STATION`  | `OI.home`                 | `HOME`           |
| `L2`             | `OI.home`                 | `HOME`           |
| `L2`             | `holding && OI.scoreL4`   | `L4`             |
| `L4`             | `OI.home`                 | `HOME`           |
| `L4`             | `holding && OI.scoreL2`   | `L2`             |
| `READY_TO_CLIMB` | `OI.climb`                | `FULLY_CLIMBED`  |
| `READY_TO_CLIMB` | `OI.home`                 | `HOME`           |
| `FULLY_CLIMBED`  | none                      | stay             |

If no row matches, the state does not change.

## Actions

| State    | Elevator                          | Arm                        |
|----------|-----------------------------------|----------------------------|
| every pose | `goTo(state.elevatorRotations)` | `goTo(state.armDegrees)`   |

Both setpoints are sent every tick, at the same time. Task 3 adds the rule
that orders them.

## Your task

Open `src/SuperStructure.kt`. Implement `stateTransitions()` from the
transition table and `stateActions()` from the actions table. `periodic()`,
`atPosition`, and `reset()` are given.
