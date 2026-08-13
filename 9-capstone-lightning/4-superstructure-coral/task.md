# One Big State Machine: the Coral Superstructure

So far every mechanism got its own FSM. LIGHTNING's elevator and gripper
wrist don't work that way: every coral pose is a *pair* — an elevator height
AND a wrist angle — and neither means anything alone. 2056 mentor Tyler
Holtzman put it plainly in the
[Chief Delphi Q&A](https://www.chiefdelphi.com/t/team-2056-op-robotics-2025-technical-binder-release/502550):
*"If two subsystems truly depend on each other... you really just have one
big subsystem and it should be a single state machine that controls both
mechanisms."*

So that's what you build: **one FSM commanding two mechanisms**. This is the
coral half of the superstructure diagram on p. 26 of the
[technical binder](https://2056.ca/wp-content/uploads/2025/05/OPR25-2056-Technical-Binder.pdf);
the algae half is the next task.

## The mechanisms

Two new stubs: `frc.stubs.lightning.Elevator` (the two-stage elevator) and
`frc.stubs.lightning.Wrist` (the gripper arm). They behave like the Lesson 7
mechanisms:

```kotlin
elevator.commandedTarget = Elevator.Setpoint.L4  // arms a move toward L4
elevator.state                                   // the last SETTLED setpoint
elevator.atTarget()                              // settled at commandedTarget?
```

Setting `commandedTarget` starts a move that completes after a fixed number
of `tick()` calls — **3 for the elevator, 2 for the wrist** (the wrist is the
faster mechanism). `periodic()` already ticks both, and
`Superstructure.atTarget()` (pre-written) is true only once *both* have
settled. (As throughout this lesson, the setpoint numbers in the stubs are
course values.)

## One state, two setpoints: the pose table

`SuperState`'s shape is in the file — like Lesson 7's `RobotState`, each
entry carries a setpoint for every mechanism it owns. But every entry ships
as the same placeholder pair, `(HOME, STOWED)`. Filling in the real pairs is
part of your task:

| `SuperState`   | Elevator setpoint | Wrist setpoint |
|----------------|-------------------|----------------|
| `HOME`         | `HOME`            | `STOWED`       |
| `CORAL_PICKUP` | `CORAL_PICKUP`    | `CORAL_PICKUP` |
| `L1_SCORE`     | `L1`              | `SCORE`        |
| `L2_SCORE`     | `L2`              | `SCORE`        |
| `L3_SCORE`     | `L3`              | `SCORE`        |
| `L4_SCORE`     | `L4`              | `SCORE`        |
| `L1_SPIT`      | `L1`              | `SPIT`         |
| `L2_DUNK`      | `L2_DUNK`         | `DUNK`         |
| `L3_DUNK`      | `L3_DUNK`         | `DUNK`         |
| `L4_DUNK`      | `L4_DUNK`         | `DUNK`         |

This table *is* the subsystem's constants — the pose-pair version of the
named values you've declared at the top of the last three files. On a season
robot these pairs start life as measurements from CAD and get retuned on the
practice field right up until ship day; keeping every pose in one enum means
one place to look and one place to retune, instead of setpoints scattered
through the FSM.

The `_DUNK` rows are 2056's favorite trick. Pressing Score at L2–L4 does
**not** open the gripper — it moves the *mechanism down* (`L4_DUNK`'s
elevator setpoint is 20.5 rotations, below `L4`'s 22.0), jamming the coral
onto the branch. The binder calls it "Score Lower." L1 is the trough, so
there Score just spits (`L1_SPIT`). And notice what's missing: nothing
releases the coral automatically. 2056 deliberately kept the release on a
driver button so a missed dunk can be retried.

## Per-state rows and global rows

From the binder's FSM diagrams: each state's transitions are checked inside
that state's case, and **global transitions** — legal from *any* state — are
checked after. In our pattern that's a `when (state)` for the per-state
rows, then trailing `if`s after it for the global rows.

## Your task

Open `src/Superstructure.kt`. Three pieces:

**The pose pairs** — replace each `SuperState` entry's placeholder
`(HOME, STOWED)` with the real pair from the table above. (The `HOME` row is
already right — that's what makes it a convincing placeholder.)

**`stateActions()`** — forward the current state's two setpoints into
`elevator.commandedTarget` and `wrist.commandedTarget`. Two assignments, no
`when` — the enum already knows the targets (same move as Lesson 7).

**`stateTransitions()`** — first the per-state rows:

| Current    | Condition              | Next           |
|------------|------------------------|----------------|
| `HOME`     | `commandedCoralPickup` | `CORAL_PICKUP` |
| `L1_SCORE` | `commandedScore`       | `L1_SPIT`      |
| `L2_SCORE` | `commandedScore`       | `L2_DUNK`      |
| `L3_SCORE` | `commandedScore`       | `L3_DUNK`      |
| `L4_SCORE` | `commandedScore`       | `L4_DUNK`      |

Then the global rows, priority order (an `if`/`else if` chain — first match
wins):

| Condition                       | Next       |
|---------------------------------|------------|
| `commandedL1 && coralInGripper` | `L1_SCORE` |
| `commandedL2 && coralInGripper` | `L2_SCORE` |
| `commandedL3 && coralInGripper` | `L3_SCORE` |
| `commandedL4 && coralInGripper` | `L4_SCORE` |
| `commandedHome`                 | `HOME`     |

Worth noticing:

- **The `coralInGripper` guard.** A level button with an empty gripper does
  nothing — the FSM checks the gripper before raising the elevator. (On the
  real robot this flag reads the Gripper FSM from task 3; task 8 wires that
  up. Here the tests set it directly.)
- The level rows are global so the driver can move `L4_SCORE` → `L2_SCORE`
  directly. The binder's diagram tucks these away as "NOT DRAWN: coral score
  to other score positions."
- `commandedHome` is the **lowest**-priority row: if a level button and home
  are pressed together, the level wins.
- A global row may re-select the state you're already in — reassigning
  `state` to the same value changes nothing. But global rows have a sharper
  edge: they can also **overwrite a per-state row that just fired**. Press
  Score while still holding L4 and the global L4 row snaps `L4_DUNK` right
  back to `L4_SCORE` on the same tick. The tests here release the level
  button before pressing Score, so your version works; the next task's
  starter closes the hole for real with a one-line guard between the
  per-state and global rows.
