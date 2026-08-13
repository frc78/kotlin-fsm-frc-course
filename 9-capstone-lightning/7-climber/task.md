# The One-Way Climb

LIGHTNING's climber is a Kraken X44 spinning an 18:1 winch that reels in a
Dyneema rope — and a ratchet that makes the spool physically unable to
back-drive. The spool only ever turns one direction: first paying the
mechanism out to deploy, then reeling the robot up the cage
([Q&A #56](https://www.chiefdelphi.com/t/team-2056-op-robotics-2025-technical-binder-release/502550),
[binder](https://2056.ca/wp-content/uploads/2025/05/OPR25-2056-Technical-Binder.pdf)).
The climb takes under a second, and after the buzzer the ratchet holds the
whole robot up **with the motor unpowered** (Q&A #103). 2056 reset the
mechanism between matches by cutting a zip tie.

That one-way ratchet is the teaching point: **a state machine can encode
mechanical irreversibility.** Draw this FSM as a graph and every edge points
forward — `STOWED → DEPLOYING → DEPLOYED → CLIMBING → CLIMBED` — with no edge
back. Once you deploy, there is no re-stow. If the code can't express a
transition, the code can't accidentally command one.

We add one thing the binder's diagram doesn't show: a **timeout fault**. If
the hook misses the cage, the winch reels against nothing — or jams. Rather
than burn the motor at 12 V forever, the FSM gives the climb 2.0 seconds and
then cuts power and latches `FAULTED`. This is the timer-plus-`onEnter()`
pattern from "Timers and Timed States": entering `CLIMBING` restarts
`climbTimer`.

(As throughout this lesson: 2056 never published numeric setpoints — the
rotations, voltages, and timeout here are course values.)

You're building this one end-to-end, same season anatomy as the rest of the
lesson: wiring table → constants → hardware → `configureMotors()` → the FSM.

## The wiring table

From the electrical team:

| Device       | Hardware                       | ID       |
|--------------|--------------------------------|----------|
| `winchMotor` | Kraken X44, 18:1 winch + ratchet | CAN 40 |

That's the whole subsystem — one motor, no sensors to wire. Deploy and climb
completion are read straight off the winch's built-in encoder
(`winchMotor.getPosition()`), and the timeout comes from a WPILib `Timer`.
The `Timer` is software, not wiring: you construct it yourself, no ID needed.

## Configuration

| Motor        | Neutral mode | Current limits       | Slot0 |
|--------------|--------------|----------------------|-------|
| `winchMotor` | `Brake`      | Supply 40 A, enabled | —     |

**Why brake?** It's belt-and-suspenders. The ratchet is the real holding
device — once it engages, "the motor is under no load when holding us up"
(Q&A #103), which is why `CLIMBED` can sit at 0 V all endgame. Brake mode
backs it up: a ratchet can skip a tooth's worth of travel before it catches,
and between the FSM's unpowered states (`DEPLOYED`, `FAULTED`) brake keeps
the spool exactly where the FSM left it. Since the motor never holds load,
brake costs nothing.

**Why a 40 A supply limit?** The climb is the hardest single pull of the
match, at the exact moment the battery is at its saggiest — and if the hook
misses, the winch stalls at a full 12 V. The supply limit keeps that stall
from tripping a breaker or browning out the rio. Note what it *doesn't* do:
a current limiter helps a stalled winch survive, but it can't decide to give
up. That decision is the FSM's — it's the whole reason `FAULTED` exists.

**Why no Slot0?** Every request in this FSM is an open-loop `VoltageOut`.
No closed-loop control, no gains to tune.

Build the configuration the way Lesson 5 taught: one
`TalonFXConfiguration().apply { ... }` block covering *all* the fields, then
one `winchMotor.configurator.apply(...)` call. The configurator keeps only
the most recently applied configuration — two separate configs means the
second silently wipes the first.

## Constants

| Constant                | Value  | Meaning                                    |
|-------------------------|--------|--------------------------------------------|
| `DEPLOY_VOLTS`          | `4.0`  | Gentle pay-out while the mechanism deploys |
| `CLIMB_VOLTS`           | `12.0` | Full send — the climb takes under a second |
| `DEPLOY_ROTATIONS`      | `15.0` | Winch position when the hook is fully out  |
| `CLIMB_ROTATIONS`       | `40.0` | Winch position when the robot is up        |
| `CLIMB_TIMEOUT_SECONDS` | `2.0`  | Give up and latch `FAULTED`                |

The hidden tests read the motor, not your source file — they can't check
what you *named* anything. The discipline is on you: every number in your
FSM should have a name, so the `when` branches read like the tables below.

## Your task

Open `src/Climber.kt` and build the subsystem in season order:

1. **Hardware** — initialize `winchMotor` and `climbTimer` (the typed `val`s
   are waiting on `TODO()`). The wiring table has the CAN ID.
2. **Constants** — declare the five constants at the marked spot.
3. **`configureMotors()`** — apply the configuration table. `init { }`
   already calls it.
4. **The FSM** — `periodic()` is pre-wired to run `stateTransitions()`, then
   `onEnter()`, then `stateActions()`, then update `previousState`. Implement
   all three methods.

**`onEnter()`:** when the state just changed *and* the new state is
`CLIMBING`, call `climbTimer.restart()`.

**`stateActions()`** — every request is a `VoltageOut` on `winchMotor`, and
every voltage is `>= 0`: the ratchet means we never drive backward.

| State       | winchMotor         |
|-------------|--------------------|
| `STOWED`    | `VoltageOut(0.0)`  |
| `DEPLOYING` | `VoltageOut(4.0)`  |
| `DEPLOYED`  | `VoltageOut(0.0)`  |
| `CLIMBING`  | `VoltageOut(12.0)` |
| `CLIMBED`   | `VoltageOut(0.0)`  |
| `FAULTED`   | `VoltageOut(0.0)`  |

`CLIMBED` at 0 V is the payoff: the ratchet holds the robot — the motor just
hangs there, unloaded.

**`stateTransitions()`** (priority order — first matching row wins):

| Current     | Condition                          | Next        |
|-------------|------------------------------------|-------------|
| `STOWED`    | `commandedDeploy`                  | `DEPLOYING` |
| `DEPLOYING` | `winchMotor.getPosition() >= 15.0` | `DEPLOYED`  |
| `DEPLOYED`  | `commandedClimb`                   | `CLIMBING`  |
| `CLIMBING`  | `winchMotor.getPosition() >= 40.0` | `CLIMBED`   |
| `CLIMBING`  | `climbTimer.hasElapsed(2.0)`       | `FAULTED`   |

Three things to notice:

- **No row leaves `CLIMBED` or `FAULTED`**, and no row returns to `STOWED`.
  Terminal states stay terminal, no matter what the driver presses.
- The two `CLIMBING` rows are ordered: if the winch reaches 40 rotations on
  the very tick the timer expires, **reaching position wins** — check success
  before the timeout.
- `commandedClimb` does nothing in `STOWED` — there is no row for it. The
  climber has to deploy first.

> **Stub note:** `VoltageOut` doesn't move the stub motor's simulated
> position. The hidden tests turn the winch with
> `winchMotor.simulatePosition(rotations)` between `periodic()` calls, the
> way a real spool turns under load.
