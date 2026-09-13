# Lessons 6 and 8 restructure. Implementation plan

> **For agentic workers:** REQUIRED SUB-SKILL: superpowers:subagent-driven-development or superpowers:executing-plans. The lead does the stub changes, directory moves, and wiring for each lesson first, so the tree compiles. Workers then take one task each. Workers do not commit.

**Specs:** `docs/superpowers/specs/2026-09-13-lesson-6-restructure-design.md` and
`docs/superpowers/specs/2026-09-13-lesson-8-rework-design.md`. Every value, name, and
test comes from there.
**Branch:** `lessons-6-8-restructure`. Lesson 6 lands first, then lesson 8.

## Global constraints

Same as `docs/superpowers/plans/2026-09-13-lessons-2-8-sweep.md`. Check cycle for
every programming task: starter FAILS with `NotImplementedError`, reference solution
(scratchpad only) PASSES, restored starter FAILS.

## Lesson 6

- [x] Lead: `SwerveRequest.kt` deadbands and `Rotation2d` directions; directory moves;
      packages `l6t1` to `l6t5`; `build.gradle.kts`; `lesson-info.yaml`; counts;
      minimal compile patches to the moved tests and lesson 8 task 3 test.
- [ ] Worker A: task 1 `1-field-and-robot-centric` (merge). Page, starter, tests per spec.
- [ ] Worker B: task 2 `2-brake-and-point-wheels` (merge). Page, starter, tests per spec.
- [ ] Worker C: task 3 `3-facing-angle`, task 5 `5-drive-mode-fsm`, checks 6 and 7.
      Pages and tests per spec; the `Rotation2d` wrap is the student's job.
- [ ] Worker D: task 4 `4-joystick-mapping` (new). Page, starter, tests per spec.
- [ ] Lead: compile, commit lesson 6.

## Lesson 8

- [ ] Lead: `SwerveDrivetrain.kt` gains `state.pose`, `resetPose`, `addVisionMeasurement`,
      `visionMeasurementsAdded`; delete `PoseEstimator.kt`; `git rm 4-chassis-speeds`
      and `6-vision-gating`; moves to `4-pose-estimator`, `5-vision-fsm` (new dir),
      `6-check-pose-math`, `7-check-vision-gating`, `8-fsm-architecture-at-2056`;
      packages; `build.gradle.kts`; `lesson-info.yaml`; `course-info.yaml`
      `additional_files` and summary; counts.
- [ ] Worker E: task 3 `3-targeting` page and test edits per spec.
- [ ] Worker F: task 4 `4-pose-estimator` rewrite per spec (`Odometry.kt`, `OdometryTest`).
- [ ] Worker G: task 5 `5-vision-fsm` (new) and check 7 rewrite per spec.
- [ ] Lead: compile, full build, commit lesson 8, merge to main.
