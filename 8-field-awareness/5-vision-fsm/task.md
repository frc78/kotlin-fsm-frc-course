# Vision Gating as a State Machine

A Limelight or PhotonVision camera sees AprilTags and returns a pose
estimate: "the robot is at `(x, y)` facing this angle." Feeding these into
the drivetrain corrects odometry drift.

Vision readings have three problems:

- **Stale.** Camera processing takes time. A reading can be 150 ms old.
- **Imprecise.** A tag seen at an angle from 4 meters gives a much less
  certain position than a tag seen head-on from 1 meter. Vision pipelines
  report this as a standard deviation (`stdDev`). A high value means low
  confidence.
- **Implausible.** A bad detection (wrong tag ID, glare, a partly hidden tag)
  can give a position nowhere near the robot.

If you trust every measurement, the pose estimate jumps around the field.
The fix is to **gate** each measurement before you apply it.

## The gates as a state machine

`Vision` is a subsystem with the two-method pattern. Its inputs:

| Input                             | Meaning                                         |
|-----------------------------------|-------------------------------------------------|
| `latestMeasurement`               | the newest camera result, or `null` with no tag |
| `nowSeconds`                      | the robot clock                                 |
| `drivetrain.state.pose`           | the drivetrain's current pose estimate          |
| `lastAppliedTimestampSeconds`     | the timestamp of the last frame you applied     |

`stateTransitions()` checks the rows below top to bottom on every tick, from
any state. The first matching row wins. `m` is `latestMeasurement`.
Comparisons are strict: exactly 0.5 s, exactly 1.0, or exactly 1.5 m passes.

| Priority | Condition                                                                 | Next state  |
|----------|---------------------------------------------------------------------------|-------------|
| 1        | `m` is `null`                                                             | `NO_TARGET` |
| 2        | `m.timestampSeconds == lastAppliedTimestampSeconds` (already applied)     | `NO_TARGET` |
| 3        | stale: `nowSeconds - m.timestampSeconds` is more than 0.5                 | `REJECTING` |
| 4        | imprecise: `m.translationStdDev` is more than 1.0                         | `REJECTING` |
| 5        | implausible: `m.pose` is more than 1.5 m from `drivetrain.state.pose`     | `REJECTING` |
| 6        | otherwise                                                                 | `TRACKING`  |

Row 2 is the memory that makes this a state machine and not a filter. A
camera frame is applied once. Real pipelines return only unread results for
the same reason.

The implausible gate uses straight-line distance, the same as `getDistance`
in task 1.

`stateActions()`:

| State       | Action                                                                                          |
|-------------|-------------------------------------------------------------------------------------------------|
| `TRACKING`  | `drivetrain.addVisionMeasurement(m.pose, m.timestampSeconds)`, then record `m.timestampSeconds` in `lastAppliedTimestampSeconds` |
| `REJECTING` | nothing                                                                                         |
| `NO_TARGET` | nothing                                                                                         |

The FSM decides. The drivetrain blends. The stub `addVisionMeasurement`
ignores the timestamp. The real one uses it for latency compensation.

## `reset()`

| Field                          | Reset value                  |
|--------------------------------|------------------------------|
| `latestMeasurement`            | `null`                       |
| `nowSeconds`                   | `0.0`                        |
| `state`                        | `NO_TARGET`                  |
| `lastAppliedTimestampSeconds`  | `Double.NEGATIVE_INFINITY`   |
| drivetrain pose                | `drivetrain.resetPose(Pose2d())` |

## Your task

Implement `stateTransitions()`, `stateActions()`, and `reset()` in
`src/Vision.kt`. `latestMeasurement` is nullable. Bind it to a local
`val m = latestMeasurement` first, then check `m == null` in the first row,
so the later rows can read `m.pose` and `m.timestampSeconds`.
