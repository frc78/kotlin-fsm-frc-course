# The Robot Loop

Three state machines are finished: `SuperStructure` (tasks 2 and 3),
`Intake` (task 4), and `Climber` (task 5). All three are in this task's
`src/` folder as given code. Nothing calls them yet.

On the real robot, `Robot.teleopPeriodic()` runs 50 times a second and hands
off to one object, `StateMachineManager`. That object calls each state
machine's `periodic()` once, in a fixed order. It holds no state of its own.
It is a schedule, not a fourth state machine.

## The order rule

Each machine reads the others' `state` like a sensor. A reading is only as
fresh as the last time its owner ran. From the 2056 page in lesson 8:
**update the producer before the consumer**, or the consumer reacts one tick
late.

| Machine          | Reads                                                | So it runs        |
|------------------|------------------------------------------------------|-------------------|
| `SuperStructure` | `OI`, `Intake.state`                                 | first             |
| `Intake`         | `OI`, `SuperStructure.state`, `SuperStructure.atPosition` | after `SuperStructure` |
| `Climber`        | `SuperStructure.state`, `SuperStructure.atPosition`  | after `SuperStructure` |

`SuperStructure` and `Intake` read each other, so one of them must go second.
The superstructure goes first. If the superstructure sees a new `HOLDING`
one tick late, a pose change starts one tick late, and nothing is at risk. If
the intake saw `atPosition` one tick early, it could eject while the arm is
still moving.

## Your task

Open `src/StateMachineManager.kt` and implement two functions.

| Function          | Does                                                                         |
|-------------------|------------------------------------------------------------------------------|
| `teleopPeriodic()`| Calls `periodic()` on each of the three machines, in the order from the table above. |
| `reset()`         | Calls `reset()` on `OI`, `SuperStructure`, `Intake`, and `Climber`, so a test starts from a known robot. |

The hidden tests drive a whole match through `OI` and `teleopPeriodic()`
only: intake a piece, hold it, raise to `L4`, score, return home, prepare to
climb, climb. One test checks that the climber extends on the same tick the
superstructure first reports `FULLY_CLIMBED` and `atPosition`. A wrong order
makes that one tick late.
