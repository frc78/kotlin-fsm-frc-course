# The Straightenator: Jam Detection

Coral comes off the slap-down intake fast, crooked, and occasionally two at a
time. Before the gripper can take a handoff, something has to center and square
it. LIGHTNING's answer is the **Straightenator** — a centering indexer with two
independently driven sides, each on its own Kraken X44 at 3:1.

The independence is deliberate: an earlier prototype with mechanically coupled
sides jammed whenever two coral came in parallel, so the production version
splits the sides and handles jams in software instead. From the
[technical binder](https://2056.ca/wp-content/uploads/2025/05/OPR25-2056-Technical-Binder.pdf)
(p. 9), the Straightenator *"monitors the motor current draw to detect a CORAL
jam, when detected the motors will briefly drive in opposite directions to
unjam and align the CORAL."*

Read that sentence again — it's a complete FSM spec. A sensor-triggered
transition ("monitors the motor current draw"), a timed state ("briefly"), and
an automatic recovery ("unjam and align"). No driver input anywhere. (2056
never published numeric setpoints; the voltages, thresholds, and times below
are course values.)

## Current draw as a jam sensor

A jammed motor doesn't stop drawing power — it stalls, and its stator current
*climbs*. That makes current a free jam sensor: no extra hardware, just a read:

```kotlin
motor.getStatorCurrent(): Double   // amps
```

Feeding a coral normally draws well under 40 A; pin one against the rollers
and the current shoots past it. 2056 leaned on this trick twice — jam detection
here, and algae possession detection in the gripper (next task).

> **Real Phoenix6 detail:** the real read is a `StatusSignal` —
> `motor.statorCurrent.valueAsDouble`. The stub flattens it to a plain
> `Double`, just like `getPosition()`.

## The unjam window

The counter-rotation runs for a fixed 0.25 s, built exactly like the eject
state in *Timers and Timed States*: `onEnter()` restarts `unjamTimer` when the
FSM enters `UNJAMMING`, and the exit transition polls `hasElapsed(0.25)`.

**Restart, not start.** A second jam must get its own full 0.25 s window. If
the timer carried the time accumulated during the previous unjam, the second
window would end early — or instantly.

## Your task

Open `src/Straightenator.kt`. You're building the whole subsystem again, in
the same order as the intake: wiring, constants, configuration, then the FSM.
Pre-wired for you: the `init { configureMotors() }` call, the periodic
ordering, `previousState` bookkeeping, and `reset()`.

### 1. Hardware — the wiring table

The electrical team has already run the CAN bus:

| Device       | Hardware               | CAN ID |
|--------------|------------------------|--------|
| `leftMotor`  | Kraken X44 (`TalonFX`) | 25     |
| `rightMotor` | Kraken X44 (`TalonFX`) | 26     |

Replace the `TODO()` initializers with real constructions. `unjamTimer` needs
no wiring — it's a plain `Timer()`.

### 2. Constants

| Constant           | Value  | Meaning                               |
|--------------------|--------|---------------------------------------|
| `FEED_VOLTS`       | `4.0`  | Feed voltage on both sides            |
| `JAM_CURRENT_AMPS` | `40.0` | Stator current that counts as a jam   |
| `UNJAM_SECONDS`    | `0.25` | Length of the counter-rotation window |

Same discipline as the intake: every number in your FSM gets a name at the
top of the object. When the Straightenator false-triggers at competition
because the venue carpet drags differently than your shop floor, you retune
`JAM_CURRENT_AMPS` in one place — you don't grep the `when` branches for a
bare `40.0`.

### 3. `configureMotors()`

One `TalonFXConfiguration` per motor, built with `.apply { }` and handed to
`motor.configurator.apply(...)` — the Lesson 5 shape. The pre-written `init`
block runs it once at startup.

| Motor        | NeutralMode | Inverted                              | Supply limit  | Stator limit  |
|--------------|-------------|---------------------------------------|---------------|---------------|
| `leftMotor`  | `Coast`     | `CounterClockwise_Positive` (default) | 20 A, enabled | 60 A, enabled |
| `rightMotor` | `Coast`     | `Clockwise_Positive`                  | 20 A, enabled | 60 A, enabled |

Why these choices:

- **Coast** — these rollers handle a game piece, not a position. In brake
  mode, a coral halfway through the indexer gets clamped the instant the FSM
  commands 0 V; coasting lets it keep sliding into place.
- **Inversion on the right side only** — the two sides face each other across
  the coral's path: mirrored mounting, the classic inversion case from
  *Motor Output: Neutral Mode and Inversion*. Fix it once in configuration
  and `VoltageOut(4.0)` means "feed" on both sides — exactly what the
  `FEEDING` row below assumes.
- **20 A supply** — a small motor on a light indexing job; a low supply cap
  protects the breaker and saves brownout headroom for the drivetrain.
- **60 A stator** — this mechanism *expects* to stall; that's the whole
  detection scheme. The stator limit is what keeps a stalled winding from
  cooking during the quarter-second the FSM takes to react.

> **The detection threshold sits below the limit. Always.** The limiter
> clamps stator current at 60 A; the FSM calls anything over 40 A a jam. If
> the limiter clamped at or below 40 A, the current could never climb past
> the detection threshold and the jam transition would never fire — the
> limiter would erase the very signal the FSM watches for. Whenever current
> doubles as a sensor, check the threshold against the configured limit
> first.

### 4. The FSM

Implement `stateTransitions()`, `onEnter()`, and `stateActions()`.

**`stateActions()`:**

| State       | `leftMotor`        | `rightMotor`      |
|-------------|--------------------|-------------------|
| `IDLE`      | `VoltageOut(0.0)`  | `VoltageOut(0.0)` |
| `FEEDING`   | `VoltageOut(4.0)`  | `VoltageOut(4.0)` |
| `UNJAMMING` | `VoltageOut(-4.0)` | `VoltageOut(4.0)` |

(`UNJAMMING` is the binder's "opposite directions": left reverses while right
keeps pushing, so a stuck coral twists free and squares up.)

**`stateTransitions()`** (priority order — first match wins):

| Current     | Condition                                | Next        |
|-------------|------------------------------------------|-------------|
| `IDLE`      | `commandedFeed`                          | `FEEDING`   |
| `FEEDING`   | either motor's `getStatorCurrent() > 40.0` | `UNJAMMING` |
| `FEEDING`   | `!commandedFeed`                         | `IDLE`      |
| `UNJAMMING` | `unjamTimer.hasElapsed(0.25)`            | `FEEDING`   |

The jam row sits above the release row, so a current spike wins even on the
tick the driver lets go. And `IDLE` has no current row at all — motors that
aren't running can't jam.

**`onEnter()`:** when the state changed this tick (`state != previousState`)
and the new state is `UNJAMMING`, restart the unjam timer.

**The two-hop exit:** `UNJAMMING` always returns to `FEEDING` — it never looks
at the feed button. If the driver released mid-unjam, the `FEEDING → IDLE` row
fires on the very next tick. That's two ticks instead of one, and it's normal,
good FSM style: each state only needs to know its own exits, so resist the urge
to add a special `UNJAMMING → IDLE` row. Likewise, if the coral is *still*
stuck when the window ends, `FEEDING` sees the high current again and starts
another unjam — the loop repeats until the coral frees up.
