# Lesson 8 rework. Design

Date: 2026-09-13. Source: `docs/superpowers/reviews/2026-09-12-lessons-2-8-review.md`,
lesson 8 proposals 1 to 3. Depends on the lesson 6 spec for one stub change
(`withTargetDirection(Rotation2d)`); whichever lands first makes that change.

## Goal

1. Give lesson 8 an FSM. Vision gating becomes a two-method `object Vision : Subsystem`.
2. Make the pose API the one students meet on a CTRE swerve robot: `drivetrain.state.pose`,
   `drivetrain.resetPose(...)`, `drivetrain.addVisionMeasurement(pose, timestampSeconds)`.
3. Cut the pass-through task (`fieldToRobotSpeeds`).
4. Targeting builds its request with `Rotation2d`, not degrees.

Lesson 8 goes from 9 tasks to 8. Programming tasks go from 6 to 5.

## Stub changes

### Delete `util/src/main/kotlin/frc/stubs/estimator/PoseEstimator.kt`

Nothing outside lesson 8 uses it. Remove its `additional_files` line from
`course-info.yaml`.

### Extend `util/src/main/kotlin/frc/stubs/swerve/SwerveDrivetrain.kt`

```kotlin
class SwerveDrivetrain(initialPose: Pose2d = Pose2d()) {
    class SwerveDriveState(var pose: Pose2d)       // mirrors CTRE getState().Pose
    val state: SwerveDriveState

    var lastRequest: SwerveRequest                 // unchanged, private set
    fun setControl(request: SwerveRequest)         // unchanged

    fun resetPose(pose: Pose2d)                    // CTRE name
    fun addVisionMeasurement(pose: Pose2d, timestampSeconds: Double)
    var visionMeasurementsAdded: Int               // inspection point, private set
}
```

Rules:

- `addVisionMeasurement` blends translation and rotation toward `pose` with a fixed
  weight of 0.1, the same math as the old stub. It increments `visionMeasurementsAdded`.
  `timestampSeconds` is accepted and ignored. The header comment says the real class
  uses it for latency compensation and also has a three-argument overload that takes
  a `Matrix<N3, N1>` of standard deviations; this stub has neither.
- Odometry is internal to the real drivetrain and not modeled. Tests place the robot
  with `resetPose`.
- `// ponytail: rotation blend averages raw radians and is wrong across the ±180° seam;
  tests stay away from it.`

### `FieldCentricFacingAngle` (lesson 6 spec owns this)

`targetDirection: Rotation2d = Rotation2d()`, `withTargetDirection(targetDirection: Rotation2d)`.
Same for `PointWheelsAt.moduleDirection`. Lesson 8 task 3 depends on the first.

## Task table

| #  | Directory                     | Package       | Type   | Source                                   |
|----|-------------------------------|---------------|--------|------------------------------------------|
| 1  | `1-translation-rotation`      | `course.l8t1` | edu    | unchanged                                |
| 2  | `2-pose2d`                    | `course.l8t2` | edu    | unchanged                                |
| 3  | `3-targeting`                 | `course.l8t3` | edu    | edited: `Rotation2d` target, aside on `ChassisSpeeds` |
| 4  | `4-pose-estimator`            | `course.l8t4` | edu    | moved from 5, rewritten                  |
| 5  | `5-vision-fsm`                | `course.l8t5` | edu    | new, replaces `6-vision-gating` (deleted) |
| 6  | `6-check-pose-math`           | none          | choice | moved from 7, unchanged                  |
| 7  | `7-check-vision-gating`       | none          | choice | moved from 8, rewritten                  |
| 8  | `8-fsm-architecture-at-2056`  | `course.l8t8` | theory | moved from 9, package renamed            |
| —  | `4-chassis-speeds`            | —             | —      | cut (`git rm`); its remote-info is lost  |

Moves use `git mv` so `task-remote-info.yaml` files travel with their directories.
`6-vision-gating` is deleted; `5-vision-fsm` is a new task with no remote-info.

## Task 3, Targeting

Starter: unchanged signature. Page changes:

- Setter table: `withTargetDirection(...)` takes "the heading from the robot to the
  goal, as a `Rotation2d`". Delete the sentence "The stub's `withTargetDirection`
  takes degrees as a `Double`". Delete the `.degrees` mention in the angle-wrap section.
- "In real code" block: `robotPose = drivetrain.state.pose,   // task 4`.
- New aside "Frames inside the drivetrain", two sentences: a `ChassisSpeeds` is the
  robot-frame velocity `(vxMetersPerSecond, vyMetersPerSecond, omegaRadiansPerSecond)`
  that the kinematics turn into module speeds. `FieldCentric` requests convert your
  field-frame sticks into it inside the drivetrain, so you do not write that math.

Tests: `assertEquals(expected, r.targetDirection.degrees, tol)` in all five tests.
Expected values unchanged: 0, 90, 0, 180, 53.130102.

## Task 4, Pose from the drivetrain (moved from 5)

Page outline:

1. Two inputs to a pose estimate: odometry (always on, drifts) and vision (when a
   tag is in view, corrects). One paragraph each. Keep the current wording.
2. On a CTRE swerve robot the drivetrain owns the estimator. Table:

   | You want                        | Call                                             |
   |---------------------------------|--------------------------------------------------|
   | where am I                      | `drivetrain.state.pose`                          |
   | tell it where auto starts       | `drivetrain.resetPose(Pose2d)`                   |
   | feed a camera reading           | `drivetrain.addVisionMeasurement(pose, timestampSeconds)` (task 5) |

   Odometry runs inside on every loop. You never call it.
3. What one odometry step does, so the student knows what the box computes: the
   modules report a body-frame motion `(forward, sideways, heading change)`; the
   estimator rotates it into the field frame by the current heading, then adds it.
4. Your task: `poseAfterOdometry(current: Pose2d, forwardMeters: Double, turnDegrees: Double): Pose2d`
   in `src/Odometry.kt`. Motion is along the robot's +X only. The heading change is
   applied after the translation.

   | Parameter       | Meaning                                              |
   |-----------------|------------------------------------------------------|
   | `current`       | the pose at the start of the tick                    |
   | `forwardMeters` | motion along the robot's +X axis at the start heading |
   | `turnDegrees`   | heading change during the tick                       |

5. Trace table (unchanged values): start `(0,0)` 0°; forward 1 → `(1,0)` 0°;
   turn 90 → `(1,0)` 90°; forward 1 → `(1,1)` 90°.

Tests (`OdometryTest`, tolerance 1e-9 for translation, 1e-6 for degrees):

| Test                                   | Input                                        | Expected                 |
|----------------------------------------|----------------------------------------------|--------------------------|
| `drive_forward_one_meter`              | `(0,0,0°)`, 1.0, 0.0                         | `(1, 0)` 0°              |
| `rotate_in_place`                      | `(0,0,0°)`, 0.0, 90.0                        | `(0, 0)` 90°             |
| `forward_then_turn_then_forward`       | chain of the trace table                     | `(1, 1)` 90°             |
| `forward_at_heading_uses_robot_frame`  | `(2,3,180°)`, 1.0, 0.0                       | `(1, 3)` 180°            |
| `drivetrain_reset_then_read_state`     | `SwerveDrivetrain().resetPose(Pose2d(4,3,90°))` then `state.pose` | `(4, 3)` 90° (stub API smoke test, one assert) |

## Task 5, Vision FSM (new)

Starter `src/Vision.kt`, package `course.l8t5`:

```kotlin
object Vision : Subsystem {
    enum class State { NO_TARGET, TRACKING, REJECTING }

    val drivetrain = SwerveDrivetrain()
    var latestMeasurement: VisionMeasurement? = null   // null when the camera sees no tag
    var nowSeconds: Double = 0.0                       // the robot clock, set by the caller
    var state: State = State.NO_TARGET
    var lastAppliedTimestampSeconds: Double = Double.NEGATIVE_INFINITY

    override fun periodic() { stateTransitions(); stateActions() }
    private fun stateTransitions() { TODO }
    private fun stateActions() { TODO }
    fun reset() { TODO }
}
```

`reset()` sets `latestMeasurement = null`, `nowSeconds = 0.0`, `state = NO_TARGET`,
`lastAppliedTimestampSeconds = NEGATIVE_INFINITY`, and `drivetrain.resetPose(Pose2d())`.
The page lists these five in a table.

Transition table. Rows are checked top to bottom on every tick, from any state.
Comparisons are strict: exactly 0.5 s, 1.0, or 1.5 m passes.

| Priority | Condition                                                             | Next state   |
|----------|-----------------------------------------------------------------------|--------------|
| 1        | `latestMeasurement == null`                                           | `NO_TARGET`  |
| 2        | `m.timestampSeconds == lastAppliedTimestampSeconds` (already applied) | `NO_TARGET`  |
| 3        | stale: `nowSeconds - m.timestampSeconds > 0.5`                        | `REJECTING`  |
| 4        | imprecise: `m.translationStdDev > 1.0`                                | `REJECTING`  |
| 5        | implausible: `m.pose.translation.getDistance(drivetrain.state.pose.translation) > 1.5` | `REJECTING` |
| 6        | otherwise                                                             | `TRACKING`   |

Row 2 is the memory that makes this an FSM and not a filter: a camera frame is
applied once. Real pipelines return only unread results for the same reason.

Actions:

| State        | Action                                                                                  |
|--------------|-----------------------------------------------------------------------------------------|
| `TRACKING`   | `drivetrain.addVisionMeasurement(m.pose, m.timestampSeconds)`; `lastAppliedTimestampSeconds = m.timestampSeconds` |
| `REJECTING`  | nothing                                                                                 |
| `NO_TARGET`  | nothing                                                                                 |

Page outline: the three problems with vision (keep the current three bullets),
"the gates as a state machine" with both tables, the ownership sentence (the FSM
decides, the drivetrain blends), the `reset()` table, and "Your task". Note the
stub `addVisionMeasurement` ignores the timestamp and the real one uses it for
latency compensation.

Tests (`VisionTest`). `@BeforeTest` calls `Vision.reset()` then
`Vision.drivetrain.resetPose(Pose2d(1.0, 1.0, Rotation2d()))` and sets
`Vision.nowSeconds = 10.0`. `m(x, y, ts, std)` is a local helper that builds a
`VisionMeasurement` with `Rotation2d()`. One `periodic()` per row unless stated.
`n` is `drivetrain.visionMeasurementsAdded`.

| Test                                     | Measurement                    | Expected state | `n` | Pose check                    |
|------------------------------------------|--------------------------------|----------------|-----|-------------------------------|
| `starts_with_no_target`                  | none, no tick                  | `NO_TARGET`    | 0   | `(1, 1)`                      |
| `no_measurement_stays_no_target`         | `null`                         | `NO_TARGET`    | 0   | `(1, 1)`                      |
| `good_measurement_is_tracked`            | `m(1.1, 1.0, 9.95, 0.2)`       | `TRACKING`     | 1   | `1.0 < x < 1.1`               |
| `stale_measurement_is_rejected`          | `m(1.1, 1.0, 9.0, 0.2)`        | `REJECTING`    | 0   | `x == 1.0`                    |
| `imprecise_measurement_is_rejected`      | `m(1.1, 1.0, 10.0, 1.5)`       | `REJECTING`    | 0   | `x == 1.0`                    |
| `implausible_jump_is_rejected`           | `m(5.0, 5.0, 10.0, 0.2)`       | `REJECTING`    | 0   | `x == 1.0`                    |
| `diagonal_jump_just_over_threshold`      | `m(2.0, 2.2, 10.0, 0.2)`       | `REJECTING`    | 0   | `x == 1.0` (1.562 m; `|dx|` or `max` gates wrongly accept) |
| `boundary_values_pass`                   | `m(2.5, 1.0, 9.5, 1.0)`        | `TRACKING`     | 1   | `x > 1.0` (0.5 s, 1.0, 1.5 m exactly) |
| `same_frame_is_applied_once`             | `m(1.1, 1.0, 9.95, 0.2)`, two ticks | tick 1 `TRACKING`, tick 2 `NO_TARGET` | 1 | — |
| `new_frame_after_reject_is_tracked`      | `m(5.0, 5.0, 10.0, 0.2)` then `m(1.1, 1.0, 9.98, 0.2)` | `REJECTING` then `TRACKING` | 1 | — |
| `losing_the_tag_returns_to_no_target`    | good `m`, tick; then `null`, tick | `TRACKING` then `NO_TARGET` | 1 | — |
| `reset_clears_state`                     | good `m`, tick, `reset()`      | `NO_TARGET`    | 0 (new drivetrain count is unchanged; assert `latestMeasurement == null` and `state`) | `(0, 0)` |

Every assert carries a message that names the gate or the rule.

## Check 6, Pose math (moved from 7)

Directory move only. Text unchanged.

## Check 7, Vision gating (moved from 8, rewritten)

Question: `Vision` from task 5 is in `TRACKING`. On the next tick it sees this input
(same table as today, header names `drivetrain.state.pose` and `nowSeconds`). "After
`periodic()`, what state is `Vision` in?"

| Option                                                                       | Correct |
|------------------------------------------------------------------------------|---------|
| `TRACKING`. All gates pass, so the drivetrain blends toward it.              | no      |
| `REJECTING`. The stale gate fails, because the reading is too old.           | no      |
| `REJECTING`. The implausible gate fails, because the jump is too far.        | yes     |
| `NO_TARGET`. The frame was already applied last tick, so nothing is new.     | no      |

`message_incorrect`: compute each row of the transition table in order; exactly one
row before "otherwise" matches. `message_correct`: distance about 5.6 m; the previous
`TRACKING` state does not matter because every tick re-evaluates from the top.

## Task 8, Theory (moved from 9)

`git mv`. Change `package course.l8t9` to `package course.l8t8` in
`src/ArchitectureDemo.kt`. Page text unchanged.

## Wiring

- `build.gradle.kts`: main `src` list becomes tasks 1, 2, 3, 4, 5, 8 of lesson 8;
  test list becomes tasks 1 to 5. Remove `4-chassis-speeds` and `6-vision-gating`
  entries and the old `5-pose-estimator`, `9-fsm-architecture-at-2056` paths.
- `8-field-awareness/lesson-info.yaml`: the eight directories in order.
- `course-info.yaml`: remove the `estimator/PoseEstimator.kt` line. Summary line 8
  becomes "Field awareness - Pose math, the drivetrain's pose estimate, and a vision
  gating FSM".
- `CLAUDE.md`: "8 lessons, 62 tasks — 40 programming". `README.md` line 10 the same;
  line 20: "`Translation2d` / `Rotation2d` / `Pose2d` math, the drivetrain pose
  estimate, and a vision-gating FSM".
- Lesson 6 task 6 page: no change required; its `aimTargetDegrees` input stays.

## Verification

Each programming task: starter fails with `NotImplementedError`, reference solution
passes, restored starter fails. Task 5 reference solution must also fail the
`diagonal_jump` test when the distance is computed as `max(|dx|, |dy|)`, checked once
by hand-editing the scratchpad copy. Full `./gradlew build` compiles; every failure is
a starter placeholder. `grep -rn 'PoseEstimator\|currentPose\|updateWithOdometry'`
over the repo returns nothing.
