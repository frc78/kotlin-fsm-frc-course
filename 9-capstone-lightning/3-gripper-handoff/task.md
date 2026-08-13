# The Gripper: Automated Handoff

Coral takes a fixed path through LIGHTNING: the slap-down intake drops it into
the Straightenator, the Straightenator centers it into the **Coral Cradle**,
and the gripper — riding the two-stage elevator — pulls it out of the cradle.
The cradle itself is completely passive: no motors, just geometry and one beam
break. Per the
[technical binder](https://2056.ca/wp-content/uploads/2025/05/OPR25-2056-Technical-Binder.pdf)
(p. 11), that beam break "triggers the automated handoff subroutine."

Read that again: **the handoff has no driver input at all.** The moment a
coral settles into the cradle, the gripper FSM leaves `EMPTY` on its own,
spins its wheels, and pulls the coral in. Beam breaks automate every handoff
on this robot — the driver just drives.

## Two ways to know you have it

The gripper handles both game pieces, and it detects possession two different
ways:

- **Coral** is rigid and always arrives in the same spot, so a second beam
  break inside the gripper confirms possession the moment
  `gripperBeamBreak.get()` reads `true`.
- **Algae** is a soft 16-inch ball — nothing for a beam to cleanly break.
  Instead the gripper detects it by feel: the wheels pull until the seated
  ball stalls them, and a stalled motor draws maximum current — which means
  the current reading *is* the sensor. When stator current climbs past 50 A
  during algae intake, the wheels have stalled against a ball: you have it.

You used `getStatorCurrent()` in the Straightenator to detect a jam — a
fault. Here the identical reading means *acquisition* — a success. Same
input, opposite meaning; which one it is depends entirely on the state.

One more Q&A detail: while holding coral, the wheels keep spinning inward at
a whisper-quiet 0.5 V, so a coral knocked loose in a collision gets pulled
back into place instead of dropped.

## The current limit *is* the mechanism

Stalling a motor on purpose — and staying stalled for the rest of the match —
sounds like a recipe for smoke. Asked about exactly that on
[Chief Delphi](https://www.chiefdelphi.com/t/team-2056-op-robotics-2025-technical-binder-release/502550),
2056's answer (Q&A #100) comes down to one line:

> *"conservative gearing to allow a fairly low stator current limit of just
> 55 amps... our stator current is no more than it needs to be to have a
> secure hold on the algae."*

Sit with that, because it inverts how you've used current limits so far. In
Lesson 5 — and in the Straightenator's 60 A stator limit — the limit was
protection: a ceiling the mechanism should never actually hit. The gripper is
*designed to run at the ceiling*. The wheels stall against the ball, the
limiter clamps stator current at 55 A, and that clamped current — stator
current is the torque knob, remember — is the grip strength. The gearing was
chosen so that 55 A is exactly enough squeeze for a secure hold: low enough
to sustain all match without cooking the windings, and cheap on the battery
too (supply current stays under 10 A). This limit isn't protecting the
mechanism; **it is the mechanism.**

The FSM's 50 A detection threshold only works *because* of that limit — and
it must sit below it. This is the Straightenator rule again: the limiter
clamps the reading at 55 A, so a detection threshold at or above 55 A could
never trip. **Detection threshold below the limit, always.** 50 A is close
enough to the ceiling that only a genuine stall reaches it, with 5 A of
headroom so the clamped reading reliably crosses it.

Neutral mode is **`Brake`**: if the robot disables while holding a game
piece — end of auto, a mid-match brownout — coast would let the wheels
back-drive and drop it. Brake pins them.

## Wiring and configuration

The wiring table from the electrical team:

| Device             | Hardware                    | ID     |
|--------------------|-----------------------------|--------|
| `motor`            | Kraken X44 (`TalonFX`)      | CAN 30 |
| `cradleBeamBreak`  | beam break (`DigitalInput`) | DIO 2  |
| `gripperBeamBreak` | beam break (`DigitalInput`) | DIO 3  |

The configuration, per the section above:

| Motor   | NeutralMode | Current limits             | Slot0 |
|---------|-------------|----------------------------|-------|
| `motor` | `Brake`     | Stator 55.0 A, **enabled** | —     |

No Slot0 gains: every state commands a plain `VoltageOut`, open-loop. And
remember the Lesson 5 gotcha — the limit value means nothing unless the
matching `Enable` flag is set in the same configuration.

## Constants

> As throughout this lesson, 2056 never published numeric setpoints — the
> voltages and the 50 A threshold below are course values. The 55 A stator
> limit is the exception: that number is 2056's own, straight from the Q&A.

| Constant           | Value  | Used by              |
|--------------------|--------|----------------------|
| `EMPTY_VOLTS`      | `0.0`  | `EMPTY`              |
| `HANDOFF_VOLTS`    | `6.0`  | `HANDOFF`            |
| `HOLD_CORAL_VOLTS` | `0.5`  | `HOLDING_CORAL`      |
| `ALGAE_VOLTS`      | `10.0` | `ALGAE_INTAKE`       |
| `HOLD_ALGAE_VOLTS` | `2.0`  | `HOLDING_ALGAE`      |
| `RELEASE_VOLTS`    | `-6.0` | `RELEASING`          |
| `ALGAE_STALL_AMPS` | `50.0` | the stall transition |

The names are suggestions; the discipline isn't: every number in your FSM
should have a name, declared once at the top of the object. (The 55 A limit
is welcome to be a constant too.)

## Your task

Open `src/Gripper.kt`. Only the skeleton is pre-written this time — the state
enum, the driver inputs, the `hasCoral()`/`hasAlgae()` helpers (the
superstructure reads those in later tasks), `periodic()`, an `init` block,
and `reset()`. Build the rest in season order:

1. **Hardware** — declare the motor and both beam breaks per the wiring
   table.
2. **Constants** — the table above, at the top of the object.
3. **`configureMotors()`** — build one `TalonFXConfiguration`, mutate it in a
   single `.apply { ... }` block (the Lesson 5 shape), and hand it to the
   motor's configurator. The pre-written `init` calls it once.
4. **The FSM** — implement `stateActions()` and `stateTransitions()`.

**`stateActions()`** — every state commands `motor` with a `VoltageOut`:

| State           | Motor output       | Why                                   |
|-----------------|--------------------|---------------------------------------|
| `EMPTY`         | `VoltageOut(0.0)`  |                                       |
| `HANDOFF`       | `VoltageOut(6.0)`  | pull the coral in from the cradle     |
| `HOLDING_CORAL` | `VoltageOut(0.5)`  | idle-spin inward to re-seat if bumped |
| `ALGAE_INTAKE`  | `VoltageOut(10.0)` | full pull until the ball stalls it    |
| `HOLDING_ALGAE` | `VoltageOut(2.0)`  | keep pressing against the ball        |
| `RELEASING`     | `VoltageOut(-6.0)` | spit                                  |

**`stateTransitions()`** (priority order — first matching row wins):

| Current         | Condition                                      | Next            |
|-----------------|------------------------------------------------|-----------------|
| `EMPTY`         | `cradleBeamBreak.get()`                        | `HANDOFF`       |
| `EMPTY`         | `commandedAlgaeIntake`                         | `ALGAE_INTAKE`  |
| `HANDOFF`       | `gripperBeamBreak.get()`                       | `HOLDING_CORAL` |
| `ALGAE_INTAKE`  | `motor.getStatorCurrent() > 50.0`              | `HOLDING_ALGAE` |
| `ALGAE_INTAKE`  | `!commandedAlgaeIntake`                        | `EMPTY`         |
| `HOLDING_CORAL` | `commandedRelease`                             | `RELEASING`     |
| `HOLDING_ALGAE` | `commandedRelease`                             | `RELEASING`     |
| `RELEASING`     | `!commandedRelease && !gripperBeamBreak.get()` | `EMPTY`         |

Worth noticing:

- The two `EMPTY` rows mean a coral arriving in the cradle **outranks** the
  driver's algae button — that coral is already committed and mid-robot, so
  the handoff must finish first.
- `ALGAE_INTAKE` has two exits: a current stall is success
  (`HOLDING_ALGAE`); releasing the button is giving up (`EMPTY`). The stall
  check comes first.
- `RELEASING` returns to `EMPTY` only when **both** the release button is let
  go *and* the gripper beam is clear. If the coral is still blocking the
  beam — or the driver is still holding the button — keep spitting.
