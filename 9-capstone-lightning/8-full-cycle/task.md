# LIGHTNING Plays a Match

Seven FSMs, each tested alone. A real match needs them running *together*:
the intake hands off to the cradle, the cradle wakes the gripper, the
gripper's state unlocks the superstructure, and the drivebase lines the whole
thing up on a reef pole.

How did 2056 make FSMs talk to each other? No message bus, no event
framework. From mentor Tyler Holtzman in the team's
[Chief Delphi Q&A](https://www.chiefdelphi.com/t/team-2056-op-robotics-2025-technical-binder-release/502550):

> "If one subsystem is entirely dependent on another, we just get an instance
> of the other subsystem so we can set or get variables/states from it."

One FSM simply reads another FSM's `state`. The only rule is **update order**:
a consumer must be updated *after* its producer, or it acts on last loop's
stale value.

## What's in the file

`src/Lightning.kt` ships with compact, pre-written versions of four machines
you already built. Read them; don't edit them.

| Object               | From task | States (and coordination signals)                                       |
|----------------------|-----------|-------------------------------------------------------------------------|
| `MiniIntake`         | 1         | `STOWED`, `INTAKING` — auto-retracts on its beam break                   |
| `MiniGripper`        | 3         | `EMPTY`, `HANDOFF`, `HOLDING_CORAL` — exposes `hasCoral()`               |
| `MiniSuperstructure` | 4         | `HOME`, `L4_SCORE`, `L4_DUNK` — `coralInGripper` guard, `atTarget()`     |
| `MiniDriveBase`      | 6         | `FIELD_DRIVE`, `AUTO_ALIGN` — `atPole()` is true within 0.05 m of `targetPole` |

In task 4, tests set `coralInGripper` by hand. Here it is **your**
`periodic()`'s job to feed it from the gripper every loop.

## Driver feedback: the LED strip

The [technical binder](https://2056.ca/wp-content/uploads/2025/05/OPR25-2056-Technical-Binder.pdf)
(p.25) lists LIGHTNING's LED language: solid white = holding a coral, solid
green = holding an algae, red = auto-align running, blue (plus controller
rumble) = ready to score — position error and mechanism error both within
threshold. The driver never looks away from the robot; the robot tells them
everything.

Our mini robot has no algae path, so `GREEN` stays reserved — the enum has
it, but nothing returns it here.

## Your task

Open `src/Lightning.kt` and implement the three functions on
`object Lightning`.

**`periodic()`** — one full robot loop. Update the four subsystems and wire
the gripper's state across, in exactly this order:

1. `MiniIntake` updates.
2. `MiniGripper` updates.
3. `MiniSuperstructure.coralInGripper` gets the value of `MiniGripper.hasCoral()`.
4. `MiniSuperstructure` updates.
5. `MiniDriveBase` updates.

Step 3 sits between 2 and 4 on purpose. The gripper (producer) has already
seen its beam breaks this loop, so the superstructure (consumer) reacts on
the **same tick** the coral arrives — swap the order and the whole robot goes
one loop stale.

**`readyToScore(): Boolean`** — true only when *all three* hold:

| Check                                        | Meaning                          |
|----------------------------------------------|----------------------------------|
| `MiniSuperstructure.state` is `L4_SCORE`     | a scoring position is selected   |
| `MiniSuperstructure.atTarget()`              | elevator and wrist have arrived  |
| `MiniDriveBase.atPole()`                     | the drive is on the pole         |

**`ledColor(): LedColor`** — first matching row wins:

| Priority | Condition                              | Color   |
|----------|----------------------------------------|---------|
| 1        | `readyToScore()`                       | `BLUE`  |
| 2        | `MiniDriveBase.state` is `AUTO_ALIGN`  | `RED`   |
| 3        | `MiniGripper.hasCoral()`               | `WHITE` |
| 4        | otherwise                              | `OFF`   |

The hidden check ends with `full_coral_cycle`: one test that plays a full
cycle — floor pickup, automated handoff, L4 auto-align, dunk — through your
`periodic()`. If your wiring and priorities are right, LIGHTNING scores.
