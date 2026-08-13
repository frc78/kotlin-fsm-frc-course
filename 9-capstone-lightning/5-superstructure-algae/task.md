# Same Buttons, Different Meaning: the Algae Branch

Here's 2056 answering "how does the driver pick what a button does?" in
their
[Chief Delphi Q&A](https://www.chiefdelphi.com/t/team-2056-op-robotics-2025-technical-binder-release/502550):
*"On button press, the elevator state machine checks what is currently in
the gripper and changes state accordingly."*

That sentence is this whole task. LIGHTNING's driver has no separate
grab-algae-off-the-reef buttons. The **same L2 and L3 buttons** mean "score
the coral" when the gripper holds coral — and "pull the algae off the reef"
when it's empty. The FSM decides, not the driver.

## What's new

`SuperState` grows six entries, and — as in the previous task — the pose
table is yours to transcribe. The ten coral pairs you filled in last time
ship correct; the six algae entries ship as `(HOME, STOWED)` placeholders.
The real pairs:

| `SuperState`         | Elevator setpoint | Wrist setpoint |
|----------------------|-------------------|----------------|
| `ALGAE_FLOOR_PICKUP` | `ALGAE_FLOOR`     | `ALGAE_PICKUP` |
| `L2_ALGAE_PICKUP`    | `ALGAE_L2`        | `ALGAE_PICKUP` |
| `L3_ALGAE_PICKUP`    | `ALGAE_L3`        | `ALGAE_PICKUP` |
| `ALGAE_HOME`         | `ALGAE_CARRY`     | `CARRY`        |
| `NET_SCORE`          | `NET`             | `NET`          |
| `PROCESSOR_SCORE`    | `PROCESSOR`       | `PROCESSOR`    |

Two new inputs: `commandedAlgaeFloor` (a dedicated floor-pickup button) and
`algaeInGripper` (the Gripper FSM's stall detection from task 3; the tests
set it directly).

`ALGAE_HOME` is the **carry pose**: a 16-inch ball won't stow inside the
frame, so "home with algae" is its own state. And from there the same trick
repeats — **L4 means the net, L1 means the processor**. (On the real robot
the arm even picks a normal or inverted net shot from the gyro reading; we
don't model that.)

## Priority: one transition per tick

`stateTransitions()` is pre-written as a skeleton. The check order:

1. **Per-state rows, coral half** — the `when` you wrote last task. Given.
2. **Per-state rows, algae branch** — `algaeTransitions()`. *Your work.*
3. **If a per-state row fired, stop.** A pre-written guard returns before
   the global rows run, so a low-priority row can't overwrite a transition
   that already happened this tick.
4. **Global rows, coral half** — L1–L4 with coral, then plain home. Given.

## Your task

Open `src/Superstructure.kt`. First fill in the six algae pose pairs from
the table above. Then implement `algaeTransitions()` — a `when (state)`
covering these rows (within a state, first match wins):

| Current              | Condition                         | Next                 |
|----------------------|-----------------------------------|----------------------|
| `HOME`               | `commandedAlgaeFloor`             | `ALGAE_FLOOR_PICKUP` |
| `HOME`               | `commandedL2 && !coralInGripper`  | `L2_ALGAE_PICKUP`    |
| `HOME`               | `commandedL3 && !coralInGripper`  | `L3_ALGAE_PICKUP`    |
| `ALGAE_FLOOR_PICKUP` | `commandedHome && algaeInGripper` | `ALGAE_HOME`         |
| `L2_ALGAE_PICKUP`    | `commandedHome && algaeInGripper` | `ALGAE_HOME`         |
| `L3_ALGAE_PICKUP`    | `commandedHome && algaeInGripper` | `ALGAE_HOME`         |
| `ALGAE_HOME`         | `commandedL4`                     | `NET_SCORE`          |
| `ALGAE_HOME`         | `commandedL1`                     | `PROCESSOR_SCORE`    |
| `NET_SCORE`          | `commandedHome && algaeInGripper` | `ALGAE_HOME`         |
| `PROCESSOR_SCORE`    | `commandedHome && algaeInGripper` | `ALGAE_HOME`         |

Worth noticing:

- **Carrying algae keeps you in the algae branch.** Your
  `commandedHome && algaeInGripper` rows run *before* the plain
  `commandedHome` global row, so home-with-algae lands in `ALGAE_HOME`
  (step 3's guard makes that stick). Home with an *empty* gripper matches
  none of your rows and falls through to the global row — plain `HOME`.
  Same button, two homes.
- **The dispatch can't double-match.** From `HOME`, the coral global row for
  L2 requires `coralInGripper`; your algae row requires `!coralInGripper`.
  Exactly one of them can fire.
- Getting *out* of `NET_SCORE` or `PROCESSOR_SCORE` after the ball leaves is
  the fall-through again: with `algaeInGripper` false, `commandedHome` hits
  the global row and the superstructure returns to plain `HOME`.
