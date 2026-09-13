# Phase Timeout

Task 5's `Superstructure` waits in each phase until a mechanism settles. On a
real robot a mechanism can fail to settle. A game piece jams the arm. A chain
skips and the elevator stalls one inch short. The encoder reports a value the
controller never reaches. In every case `atTarget()` stays `false`, the phase
never advances, and the robot is stuck with the arm out. The driver has no
way to get it back.

The fix is a **watchdog**: a timer that starts when a phase begins. If the
phase is still running when the timer passes a limit, the superstructure
gives up on the goal, moves the robot to the safe pose, and raises a fault
that the dashboard can show.

## What is already in the file

`src/Superstructure.kt` is the complete task 5 solution with the two-method
pattern: `stateTransitions()` then `stateActions()`. Three fields are new:

| Field                  | Type      | Meaning                                       |
|------------------------|-----------|-----------------------------------------------|
| `phaseTimer`           | `Timer`   | Time since the current phase began.           |
| `faulted`              | `Boolean` | A phase timed out. Stays `true` until cleared. |
| `phaseTimeoutSeconds`  | `Double`  | The limit, `2.0` seconds.                      |

Every place that used to assign `transition` now calls `enterPhase(next)`.
`stateTransitions()` calls `checkTimeout()` after the goal-change check and
before the advance rules.

`Timer` is the lesson 2 stub with the WPILib API. You need two calls:

| Call                         | Effect                                            |
|------------------------------|---------------------------------------------------|
| `phaseTimer.restart()`       | Set the clock to zero and start it.               |
| `phaseTimer.hasElapsed(s)`   | `true` when the clock reads `s` seconds or more.  |

## The rules

| Method             | Condition                                                   | Effect                                                                                        |
|--------------------|-------------------------------------------------------------|-----------------------------------------------------------------------------------------------|
| `enterPhase(next)` | `next != transition`                                        | `transition = next`, then restart the timer.                                                  |
| `enterPhase(next)` | `next == transition`                                        | Nothing. The clock keeps running.                                                             |
| `checkTimeout()`   | `transition` is `Settled`                                   | Nothing. A settled robot has no phase to time out.                                            |
| `checkTimeout()`   | any other phase, and the timer has passed the limit         | Set `faulted`. Set the goal to `STOWED`. Enter `startTransition(RobotState.STOWED)`.          |
| `clearFault()`     | always                                                      | Clear `faulted`.                                                                              |

`faulted` is a flag only. It does not block new goals. A driver who sets a
new goal after a fault is followed. `clearFault()` only clears the flag.

## How the tests drive time

The hidden tests call `phaseTimer.simulateAdvance(seconds)` between
`periodic()` calls. The stub clock only moves when a test moves it. A test
that starts a move, advances the clock by more than the limit, and ticks once
must see a fault. A test that advances the clock while the robot is settled
must not.

## Your task

Implement `enterPhase(next)`, `checkTimeout()`, and `clearFault()` in
`src/Superstructure.kt` per the rules table. Do not change the other methods.
