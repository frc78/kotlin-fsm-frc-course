# The Slap-Down Intake

Welcome to the capstone. Over the next eight tasks you'll program
**LIGHTNING** — Team 2056 OP Robotics' 2025 robot, which went 68-7-0 across
the REEFSCAPE season — subsystem by subsystem, the way they actually did it.
Their [technical binder](https://2056.ca/wp-content/uploads/2025/05/OPR25-2056-Technical-Binder.pdf)
says it plainly (p. 25): *"Finite state machines control all subsystems."*
Every mechanism runs its own state machine every loop; transitions fire on
buttons, beam breaks, motor current, timers, and other subsystems' states.
The team's [Chief Delphi Q&A thread](https://www.chiefdelphi.com/t/team-2056-op-robotics-2025-technical-binder-release/502550)
is this lesson's other source.

Here's a coral's trip through the robot: a **slap-down ground intake** pulls
it off the carpet, the "Straightenator" centers it, it settles into the
passive Coral Cradle, and a gripper riding a two-stage elevator carries it up
to the reef. You're building the first stop.

> One note before we start: 2056 never published numeric setpoints. The
> states and transitions in this lesson follow the binder; the specific
> voltages, current limits, and rotation counts are course values.

## How a season subsystem gets built

One more thing changes in this lesson. Every task so far handed you the
hardware — constructed, configured, ready to command — and asked only for
the FSM. Season code doesn't arrive like that: you start from a blank file.
And every subsystem, on every robot, gets built in the same five steps, in
order:

1. **Wiring table.** The electrical team hands you CAN IDs and DIO
   channels. You don't choose these — you transcribe them.
2. **Constants.** Named values at the top of the subsystem object:
   setpoints, voltages, thresholds. Every number in your FSM should have a
   name — a bare `12.0` inside a `when` branch tells nobody what it means
   or where to retune it.
3. **Hardware.** One declaration per wiring-table row:
   `internal val motor = TalonFX(canId = …)`, and the same idea for
   `DigitalInput(channel = …)`.
4. **`configureMotors()`.** Build **one** `TalonFXConfiguration` per motor
   in an `.apply { … }` block — everything Lesson 5 taught — and hand each
   to its motor's `configurator.apply(...)`. A pre-written `init { }` calls
   this once at startup.
5. **The FSM.** `stateTransitions()` then `stateActions()`, the two-method
   pattern you've used all course.

Each step leans only on the ones above it, which is why the order never
changes. This task walks the five steps explicitly; the rest of the lesson
expects them as habit — and so will your own robot next January.

## Step 1: the wiring table

The intake is a 20-inch-wide slap-down mechanism: rollers driven by a Kraken
X60 at 4:1, and a pivot driven by a Kraken X44 at 51:1 that swings the whole
assembly between *stowed* (upright, inside the frame) and *down* (rollers on
the carpet). A beam break sits in the coral's path.

| Name          | Device                      | ID     |
|---------------|-----------------------------|--------|
| `rollerMotor` | Kraken X60 (`TalonFX`)      | CAN 20 |
| `pivotMotor`  | Kraken X44 (`TalonFX`)      | CAN 21 |
| `beamBreak`   | beam break (`DigitalInput`) | DIO 0  |

- The pivot holds a position, so it gets `PositionVoltage(rotations)` —
  closed-loop, like the elevator in Lesson 3.
- The rollers just spin, so they get `VoltageOut(volts)`; negative reverses.
- `beamBreak.get()` returns `true` while a coral blocks the beam.

Driver inputs: `commandedDeploy` (right bumper, held) and `commandedPurge`
(a rear paddle, held) — set by joystick code on the real robot, by the tests
here. Both vars are already in the file.

## Step 2: the constants

| Constant              | Value  | Meaning                                 |
|-----------------------|--------|-----------------------------------------|
| `PIVOT_STOWED`        | `0.0`  | pivot rotations, intake upright         |
| `PIVOT_DOWN`          | `12.0` | pivot rotations, rollers on the carpet  |
| `ROLLER_INTAKE_VOLTS` | `9.0`  | roller voltage while intaking           |
| `ROLLER_PURGE_VOLTS`  | `-6.0` | roller voltage while purging (reversed) |

Declare them as `private const val`s in the marked region at the top of the
object. The tests can only see behavior, never your names — the discipline
is the point. When the pivot angle needs retuning at competition, it's one
line, found in seconds. Feel free to name the configuration numbers from the
next table too.

## Steps 3 and 4: hardware and configuration

Create the three devices from the wiring table, then implement
`configureMotors()` so each motor's applied configuration matches this
table:

| Motor         | NeutralMode | Current limits       | Slot0      |
|---------------|-------------|----------------------|------------|
| `rollerMotor` | `Coast`     | Supply 30 A, enabled | —          |
| `pivotMotor`  | `Brake`     | Supply 20 A, enabled | `kP = 8.0` |

Every cell in that table is a decision:

- **Pivot: `Brake`, with gains.** The pivot runs closed-loop
  (`PositionVoltage`), and closed-loop is dead without gains — with `kP`
  left at its default `0.0`, the controller computes zero volts and the
  pivot never moves. Brake mode is the mechanical half of the same job:
  disable the robot with the intake up and a coasting pivot flops it onto
  the carpet; brake holds it where it stopped.
- **Rollers: `Coast`.** The opposite call. Once a coral is grabbed, the
  rollers are pressing on it; coast lets the coral slip under them instead
  of stalling the motor against it.
- **Supply limits on both.** A stalled or jammed motor happily pulls enough
  battery current to trip its breaker mid-match. Supply limits cap the draw
  at what each mechanism actually needs — Lesson 5's rule that current
  limits are not optional.

And remember the two Lesson 5 gotchas: a limit value without its `Enable`
flag does nothing, and the configurator keeps only the *most recently
applied* configuration — so all of a motor's settings go in one
`TalonFXConfiguration`, not one per config block.

## Step 5: the FSM — and the auto-retract

From the binder, p. 7: *"Intake automatically retracts for protection when a
beam break detects that a CORAL is acquired."*

Read that sentence as an FSM designer: it's a transition **no driver
presses**. The moment the beam breaks, the intake stows itself — even if the
driver is still holding deploy. A sensor read is an input like any other, and
here it outranks the driver's button.

## Your task

Open `src/Intake.kt` and work the five steps: declare the constants, create
the three devices, implement `configureMotors()`, then implement
`stateTransitions()` and `stateActions()`.

**`stateActions()`:**

| State      | `pivotMotor`                    | `rollerMotor`                     |
|------------|---------------------------------|-----------------------------------|
| `STOWED`   | `PositionVoltage(PIVOT_STOWED)` | `VoltageOut(0.0)`                 |
| `INTAKING` | `PositionVoltage(PIVOT_DOWN)`   | `VoltageOut(ROLLER_INTAKE_VOLTS)` |
| `PURGING`  | `PositionVoltage(PIVOT_DOWN)`   | `VoltageOut(ROLLER_PURGE_VOLTS)`  |

(`PURGING` keeps the pivot down and reverses the rollers to spit a stuck
coral back onto the floor.)

**`stateTransitions()`** — priority order, first matching row wins:

| Current    | Condition          | Next       |
|------------|--------------------|------------|
| `STOWED`   | `commandedPurge`   | `PURGING`  |
| `STOWED`   | `commandedDeploy`  | `INTAKING` |
| `INTAKING` | `commandedPurge`   | `PURGING`  |
| `INTAKING` | `beamBreak.get()`  | `STOWED`   |
| `INTAKING` | `!commandedDeploy` | `STOWED`   |
| `PURGING`  | `!commandedPurge`  | `STOWED`   |

Two priority calls worth noticing:

- **Purge beats the beam break.** A jammed coral can sit on the beam; when
  the driver hits purge, spitting it out must win over stowing around it.
- **The beam break sits above the release row.** That's the auto-retract:
  the sensor stows the intake while `commandedDeploy` is still held.

The state enum, driver inputs, `periodic()`, `init { configureMotors() }`,
and `reset()` are already in the file.
