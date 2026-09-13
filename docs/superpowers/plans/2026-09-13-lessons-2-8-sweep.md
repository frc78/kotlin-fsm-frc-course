# Lessons 2 to 8 sweep. Implementation plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development or superpowers:executing-plans. One worker per lesson. Workers edit and verify. They do not commit. The lead commits one lesson at a time.

**Goal:** Apply every small fix from the lessons 2 to 8 review. No new tasks. No renumbering. No stub API changes except one comment.

**Source of findings:** `docs/superpowers/reviews/2026-09-12-lessons-2-8-review.md`. Sections A to H and the per-lesson lists give file:line evidence.

**Branch:** `sweep-lessons-2-8`.

## Global constraints

- `task.md` never contains the copy-pasteable solution. Show behavior as tables and API signatures. Replace step-by-step prose recipes with input/output tables.
- Starter `TODO` comments say `// TODO: see task.md.` only.
- Hidden tests are the source of truth. `task.md` and tests must agree on every value and name.
- Choice tasks: the question text, `message_correct`, and `message_incorrect` must not state or strongly imply the correct option. Options must have similar length. Do not let the correct option be the only one that names a mechanism.
- Prose in ASD-STE100 style: short sentences, active voice, one instruction per sentence. No stagey filler ("Time to build", "Let's", "the trick", "bread-and-butter"). Define jargon at first use or remove it.
- Remove `internal` and `private set` from every starter in lessons 2 to 8. Keep `: Subsystem` and `override`. Lesson 2 task 1 explains those two.
- Every test uses `kotlin.test` on JUnit 5. Every assertion gets a message that names the state or the request expected and why.
- Do not edit `build.gradle.kts`, `course-info.yaml`, `lesson-info.yaml`, `*-remote-info.yaml`, or the `.zip`.
- Do not commit. Do not run `git add`. The lead commits.

## The check cycle for every programming task you touch

1. Edit the starter, the test, and the page.
2. Write a reference solution for the task to the scratchpad directory. Never write it into the repo.
3. Run the task's tests on the starter: `./gradlew test --tests "course.lNtM.*"`. Expect FAIL.
4. Copy the reference solution over the starter. Run the tests. Expect PASS.
5. Copy the starter back from a saved copy. Run the tests. Expect FAIL.
6. If the compile error is in a package outside your lesson, another worker is mid-edit. Wait 30 seconds and retry. Do not touch that file.

## Task L2. Lesson 2, Building an FSM

- [ ] Remove `internal` and `private set` from all five starters.
- [ ] T1 page: add a "Read the skeleton" section for `: Subsystem` and `override fun periodic()`, and `!`. Fix the list order at lines 9 to 10 to transitions then actions. Cut lines 3 to 4 and "the team".
- [ ] T2: student writes the enum, fields, and `periodic()` from an empty `object` with `reset()`. Page lists the fields as a table. Add the same-tick test (distance 0.03 and button released gives HOLDING).
- [ ] T3 page: explain `State?`, `null`, and `deploymentLogs.add(...)`. Replace "SmartDashboard" and "on edge, not on level". Starter TODOs say `// TODO: see task.md.` only.
- [ ] T4 page: prose condition ("within 0.1 rotations of the target"), replace "predicate" and "closed-loop", one sentence on why AT_TARGET to STOWED has no motion.
- [ ] T5 page: cut the 2056 paragraph to one sentence. Add one "real Phoenix6" note: real code reuses one request object per motor.
- [ ] Tests: assertion messages in T1 to T4. Add: T1 jam clears while commanded stays JAMMED; T4 target changes mid-move stays MOVING; T5 intake pressed during eject is ignored.
- [ ] C6: shorten the correct option to match the others and remove the rule restatement. C7: replace with a `start()` vs `restart()` question (what happens if `onEnter` calls `start()` when the timer already runs).

## Task L3. Lesson 3, Applied subsystems

- [ ] Remove `internal` and `private set` from all three starters.
- [ ] T1 page: priority-ordered transition table in the lesson 2 format, "first matching row wins", two-row hysteresis table (state, threshold that applies). Cut "Time to build". One sentence on real CANrange `ProximityThreshold` / `ProximityHysteresis` and `getIsDetected()`.
- [ ] T2: one real rule. Accept a new `commandedTarget` only when `atTarget()` is true. Page shows the rule as a table row. Tests use `setPosition()` to simulate mid-travel and check the target is not accepted. Remove the "why so trivial" paragraph and the leaked one-liners. Delete the redundant `setPosition(0.0)` and shorten the comment in `ElevatorTest.kt:43-53`.
- [ ] T3 page: "Kotlin you need" section for nullable `?`, null-check smart cast, `when (val s = ...)`, extension property. One letter for the bound state everywhere. State that `stopMotor()` sends `NeutralOut` and differs from `VoltageOut(0.0)`. Specify a changed non-null target while SpinningUp or Ready (stay).
- [ ] C5: replace with a sealed-class data question (in `Feeding(4500.0)` the driver releases fire; options `Ready(4500.0)`, `Ready(0.0)`, `Idle`, `SpinningUp(4500.0)`).

## Task L4. Lesson 4, Testing (small items only)

- [ ] Remove `private set` and `internal` from both starters.
- [ ] T1 page: cut "There's a better way", "Just a math function", "no race conditions", "easiest code in the world", "Every robust FSM-driven robot codebase eventually". Rewrite or cut "Why this matters" so it does not restate C3's answer. Add one paragraph: `transition()` is the body of `stateTransitions()`, shown as `state = transition(state, readInputs())`.
- [ ] T2 page: cut "The trick" and "just a fancy name for". State the initial state and "stay if no row matches". Name `limit.simulateValue(true)`.
- [ ] C3: change `message_incorrect` so it does not point to the answer paragraph. C4: shorten the correct option to match the others.

## Task L5. Lesson 5, Motor configuration (existing tasks only)

- [ ] Remove `internal` from all starters. Add `reset()` to T1 to T4 starters and `@BeforeTest` that calls it to the tests.
- [ ] T1 page and T7 page: real constructor is `TalonFX(deviceId)` or `TalonFX(deviceId, canBus)`; `canId =` is a stub name. `apply()` returns `StatusCode`. `appliedConfig` is a stub inspection point only.
- [ ] T2 and T3 pages: Field / Type / Value tables. Show one nested assignment inside the `apply` block with the `MotorOutput.` prefix. Say uppercase names copy the CTRE Java API. Cut stagey openers in T1 and T2.
- [ ] T3 page: remove the 2025 default paragraph (T7 keeps it). Add one row each for supply vs stator (breakers and battery vs motor and torque cap).
- [ ] T4 page: worked lines "error 10 rps x kP 0.25 = 2.5 V" and "target 80 rps x kV 0.12 = 9.6 V". Units: V per rotation, V per rps. Add the `kG` row. Remove the real-Phoenix6 note (T7 keeps it). Use rotations per second only.
- [ ] T7 page: move the deadband paragraph out (lesson 6 worker adds one sentence to lesson 6 task 1). Drop the 2056 binder link. Remove `!!` from `RealPhoenix6.kt` or explain it in one line.
- [ ] C5: ask about `NeutralMode` or `Inverted` so the answer is true on real hardware. Shorten `message_incorrect` so it does not name the mechanism. Replace the weak 80 A distractor. Equalize lengths.
- [ ] C6: stem must not say "using the Slot0 gains". `message_incorrect` must not say "hands the motor a target to chase".

## Task L6. Lesson 6, Swerve requests (small items only)

- [ ] Remove `internal` and `private set` from all starters. Add `reset()` to T1 to T5 starters and `@BeforeTest` to their tests.
- [ ] Remove solution code from T1 (lines 57 to 58), T4 (lines 7 to 10), T5 (lines 38 to 40). Replace with setter tables. Remove the example chain comment at `SwerveRequest.kt:12-15`.
- [ ] T1 page: add one sentence on `withDeadband` and `withRotationalDeadband` (moved from lesson 5 task 7). Say "counterclockwise is positive" once as a convention.
- [ ] T6: enter `BRAKED` only when `requestedVx`, `requestedVy`, and `requestedOmega` are all under 0.05. Page table gets the rule. Add tests: brake blocked while moving; brake over robot-relative. Add a `when { }` without-subject code sample of two lines.
- [ ] Prose: delete "99% of the time", "bread-and-butter", the "I really do not want to slide" bullet, "brings the FSM theme back". Define yaw, PID (point to lesson 5 task 4), slewing, tangent.
- [ ] Tests: assertion messages in T1, T3, T4, T5. Delete `brake_is_a_singleton_object` in T3.
- [ ] C7: rewrite so the correct option does not repeat the question. Replace the brake distractor with a plausible one.

## Task L7. Lesson 7, Superstructure coordination (small items only)

- [ ] Remove `private set` from starters.
- [ ] T1 page: delete the pre-filled enum code block. Keep the table.
- [ ] T2 page: name "goal state" (`commandedRobotState`) and "current state" (`transition`). Use the terms in T3 to T5. Cut "Subsystems all the way down" and "Three assignments, no conditionals".
- [ ] T3 page: replace "iff" and "conjunction". T1 page: replace "naive".
- [ ] T4 test: add re-command-after-settle (change `commandedRobotState` after Settled and check the new commands).
- [ ] T5 page: add an "Advance when" column to the phase table. Add "in `RetractArm`, do not command the elevator". Delete "A pedagogical note" heading; move "read `startTransition` first" into "Your task". Cut "Read this carefully, it's the brain" from the starter.
- [ ] C6: replace the "majority" distractor. C7: ask a new situation (`CLIMB_PREP -> SCORE_L4`: which phase starts first?). Replace the "intake first" distractor.

## Task L8. Lesson 8, Field awareness (small items only)

- [ ] T1 page: field convention stated once (origin at blue corner, +X toward red wall, +Y to blue's left, 0 degrees along +X, counterclockwise positive). Small ASCII field sketch. Cut alliance flipping to one sentence. Explain operator overloading and `companion object` factories in one short paragraph each. Cut "the foundation everything else stands on".
- [ ] T2, T3, T5 pages: remove recipe sentences (T1 58-59, T2 43-49 and 51-55, T3 16-24, T5 30-35). Replace with input/output tables. Cut filler in T3 (9-11, 52-56) and T5 (46-47).
- [ ] T3 page: correct the wrap note. Real WPILib `Rotation2d` normalizes on construction; only this stub does not.
- [ ] T4 page and `SpeedsTest.kt:18`: replace "east"/"north" with +X/+Y. Cut "Why you'd write this yourself".
- [ ] T6 page: gloss or remove "error ellipse" and "Kalman filter". Say the gate is strict (`>`), and mention the `weight` parameter exists.
- [ ] Tests: T1 add a quadrant IV heading case. T2 add heading 90 with robot at (4, 3). T3 add a goal behind the robot. T6 add a diagonal near-threshold jump (current (1,1), measurement (2.0, 2.2)). T5 delete `reset_pose_clears_history`.
- [ ] C8: give estimator pose, measurement pose, time gap, and std dev as a table. Ask which gate rejects. Four gate-named options of equal length.

## Lead: verification and commits

- [ ] After each worker reports, run `./gradlew compileKotlin compileTestKotlin`. Expect success (starters compile with `TODO()`).
- [ ] Spot-check three edited pages per lesson against their tests for value agreement.
- [ ] Commit one lesson at a time: `Sweep lesson N: <one line>`.
- [ ] Run `./gradlew build`. Expect exit 1 from starter `TODO()` failures only. Confirm no compile errors.
- [ ] Update course-info.yaml summary only if a lesson description changed. None should.
