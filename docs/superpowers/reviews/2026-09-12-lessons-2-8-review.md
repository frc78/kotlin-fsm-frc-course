# Review of lessons 2 to 8

Date: 2026-09-12. Seven parallel reviewers, one per lesson. Read-only. Nothing changed.

Audience baseline: the new lesson 1 (programs, types, functions, if, objects, loops
and lists, error reading, enums, when, enum properties, sealed classes, FSM concept).
Course goals: Kotlin, finite state machines, some CTRE Phoenix6.

## Cross-cutting findings

### A. Skeleton syntax that no page explains

| Feature | First use | Explained? |
|---|---|---|
| `internal val` | L2 T1, every starter after | No |
| `var state ... private set` | L2 T1, every starter after | Only L4 T2 |
| `: Subsystem`, `override fun` | L2 T1 | Named, not explained |
| `!` (not) | L2 T2 | No |
| `State?`, `null` check | L2 T3, L3 T3 | One sentence in L2 T3 |
| `mutableListOf`, `.add()` | L2 T3 | No |
| `when (val s = state)` | L3 T3 | Hint only |
| extension property | L3 T3 | Named, not defined |
| `.apply { }`, nested `MotorOutput.X = ` | L5 T2 | Abstract example only |
| `when { }` without subject | L6 T6 | Two sentences, no code |
| `operator fun`, `companion object` factories | L8 T1, T4 | No |

Fix: one "Read the skeleton" section in L2 T1 for the four items every starter has.
Short "Kotlin you need" sections where each later feature first appears.

### B. task.md gives the solution as a prose recipe

L3 T2 (all three bodies), L6 T1/T4/T5 (exact chain shown), L7 T2 ("three assignments"),
L8 T1 to T5 (each function described step by step). L2 T4 table contains the exact
condition expression. Fix: input/output tables and setter tables, no recipes.

### C. Choice checks

- Correct option is the longest or the only one that names a mechanism: L2 C6, L4 C4,
  L5 C5, L6 C7.
- Stem or incorrect-feedback echoes the answer: L4 C3 (recap in T1 is the answer),
  L5 C6, L5 C5 feedback, L6 C7, L8 C8 (question names the surviving gate).
- Check repeats an earlier check or a sentence from the previous page: L3 C5 repeats
  L2 C6; L7 C6 and C7 are recall only.
- L5 C5 asks about a stub default (no supply limit) that is wrong on a real 2025+ TalonFX.

### D. Hidden tests that do not enforce the page

- L2 T1: jam clears while commanded. L2 T2: same-tick piece-and-release (what C6 asks).
  L2 T4: target changes mid-move. L2 T5: intake pressed during eject.
- L2 T1 to T4 and L6 T1/T3/T4/T5: no assertion messages.
- L3 T1: no priority order stated, so any order passes.
- L4 T2: "stay if no row matches" not stated on the page.
- L7 T3: stub intake `modeReached()` is always true, so omitting it passes.
  L7 T4: an empty `Settled` action passes. L7 T5: abort test passes because the stub
  elevator teleports.
- L8 T2: add-then-rotate passes. L8 T6: `abs(dx)` alone passes as a distance gate.
- L5: no `reset()` in any starter or test. L6 T1 to T5: same.

### E. The two-method pattern drifts

- L4 T1 introduces a pure `transition(state, input)` function that no later lesson uses.
- L4 T2 converts the subsystem from `object` to `class`; every other task uses `object`.
- L6 T6 is a stateless priority mapper written as an FSM.
- L7 calls `advanceTransition()` after `stateActions()`, the reverse of the taught order,
  and the page still claims "the same two-method shape".
- L8 T1 to T6 contain no FSM. Only the closing theory page does.
- `previousState` / `onEnter` (L2 T3, T5) never appear again in lessons 3 to 8.

### F. Transfer gaps to the real API

- `TalonFX(canId = )` does not compile on a real robot (`deviceId`, optional CAN bus).
- Swerve stubs take degrees where the real API takes `Rotation2d`. The `Rotation2d`
  stub exists and lesson 8 uses it.
- `PoseEstimator` stub (`currentPose`, `updateWithOdometry`, `addVisionMeasurement(pose,
  weight)`) shares no name with CTRE `SwerveDrivetrain` or WPILib. Largest gap in L8.
- Real code reuses one request object per motor. The course allocates one per tick.
- Missing from swerve stub: `withDeadband`, `withRotationalDeadband`, request types,
  `ForwardPerspectiveValue`. Every real teleop drive uses the deadbands.
- Not mentioned: real `Rotation2d` normalizes on construction (L8 T3 says otherwise),
  `DigitalInput` beam-break polarity, real `CANrange` firmware hysteresis, `apply()`
  returns `StatusCode`, real tests need `HAL.initialize` and device close.
- L7 superstructure commands `Intake.Mode.HOLDING`. The intake owns "do I have a piece".

### G. Thin or duplicate tasks

L2 T2 repeats T1 (only CANrange is new). L3 T2 is three one-liners. L6 T1 to T5 are
five near-identical build-one-request tasks. L7 T2 is three assignments. L7 T4 is a
one-direction dead end that T5 replaces. L8 T4 forwards four parameters to a factory.

### H. Filler, jargon, stale references

Stagey openers in L2 T1, L3 T1 ("Time to build"), L3 T2, L4 T1/T2, L5 T1/T2, L6 T1/T3/T5/T6,
L7 T2/T5 ("A pedagogical note"), L8 T1/T3/T5. Undefined jargon: SmartDashboard, "on edge",
predicate, closed-loop, brown out, stator, yaw, slewing, iff, conjunction, error ellipse,
Kalman filter, AdvantageKit. Team 2056 LIGHTNING references remain in L2 T5, L5 T7 (binder
link), L8 T9 (intentional closing page). L8 T1 field convention text contradicts itself
on red alliance (+X). L8 T4 uses "east/north" where L8 T1 uses +X/+Y.

## Per-lesson ranked proposals

S = prose or test edit. M = rework one task. L = new task or restructure. CUT = remove.

### Lesson 2, Building an FSM (solid)

1. S. T2 test: same-tick distance 0.03 with button released, expect HOLDING.
2. S. T1 page: "Read the skeleton" section (`internal`, `private set`, `: Subsystem`,
   `override`, `!`). Fix the list order at lines 9-10. Cut "the team".
3. S. T3 page: explain `State?`, `null`, `deploymentLogs.add(...)`. Replace jargon.
4. M. T2: start from an empty object, or fold CANrange into T1 and delete T2.
5. S. Assertion messages in T1 to T4. Add the three missing tests (D above).
6. S. T4 page: prose condition, plain words for predicate and closed-loop, one sentence
   on why AT_TARGET to STOWED has no motion.
7. S. C6: shorten the correct option. C7: replace with a `start()` vs `restart()` question.
8. S. T5: cut the 2056 paragraph to one sentence. Note that real code reuses one request.

### Lesson 3, Applied subsystems

1. S. T1: priority-ordered table in the L2 format, "first row wins", cut "Time to build".
2. M. T2: one real rule (accept a new target only when `atTarget()`), tests use
   `setPosition()` for mid-travel. Remove the "why so trivial" paragraph.
3. S. T3: "Kotlin you need" section (nullable, smart cast, `when (val s = ...)`,
   extension property). One letter for the bound state.
4. S. T3: state that `stopMotor()` sends `NeutralOut`. Specify a changed target.
5. M. C5: replace the duplicate priority check with a sealed-class data check
   (`Feeding(4500.0)` on release gives what?).
6. S. One sentence on real CANrange `ProximityThreshold` / `ProximityHysteresis`.
7. S. T1: two-row hysteresis table before the check.
8. S. `ElevatorTest.kt:43-53`: delete the redundant `setPosition(0.0)`.

### Lesson 4, Testing (weakest lesson: it never has the student write a test)

1. L. New task: student writes tests. Finished Intake plus a visible test file with two
   complete tests and three empty `@Test` stubs. Checker runs the student's tests
   against a correct and a broken subsystem, or inspects the source.
2. M. Theory page "Anatomy of a test file" before T1: imports, class, `@BeforeTest reset()`,
   `assertEquals(expected, actual, message)`, why data-class equality makes
   `assertEquals(VoltageOut(6.0), motor.lastRequest)` work, tick-simulate-tick-assert.
3. M. T2 reframe: "One class, many instances". Lead with left/right climbers. Drop
   "fake" and "trick". State the initial state and the stay rule.
4. S. C3 leak: cut or rewrite T1 "Why this matters". Change the incorrect feedback.
5. S. C4: equalize option lengths. Name `simulateValue` on the T2 page.
6. S. Prose cuts (list in the L4 report).
7. S. T1: show `state = transition(state, readInputs())` so the pure function is the
   body of `stateTransitions()`, not a third pattern.
8. S. Real-robot note: `HAL.initialize`, device close, real requests are not value-equal.

### Lesson 5, Motor configuration (existing tasks; the expansion has its own spec)

1. S. C6: stem must not echo the answer. Cut the feedback leak.
2. S. C5: ask about neutral mode or inversion so the answer is also true on real hardware.
3. S. T4: worked lines for kP and kV (error x kP = volts, target x kV = volts), units,
   add the `kG` row.
4. S. Deduplicate real-Phoenix6 notes (T4 vs T7) and the 2025 default paragraph (T3 vs
   T7). Move the deadband paragraph to lesson 6.
5. S. Say `TalonFX(deviceId)` / `TalonFX(deviceId, canBus)` is the real constructor,
   `apply()` returns `StatusCode`, `appliedConfig` is a stub inspection point.
6. M. Add `reset()` and `@BeforeTest` to all four tasks. Field/Type/Value tables in T2
   and T3. Show one nested assignment inside the `apply` block.
7. L (expansion). Stub: `Feedback.SensorToMechanismRatio`, `SoftwareLimitSwitch`,
   `MotionMagic`, `GravityType`, `MotionMagicVoltage`. Tasks in order: gear ratio,
   soft limits, MotionMagic plus kG. Numbers "from CAD" or "from ReCalc".
8. M (expansion). T7 becomes the end-of-lesson bridge with every block plus
   `StatusSignal`, mutable requests, `simState`, `StatusCode`. Drop the 2056 link.

### Lesson 6, Swerve requests

1. M. Merge T1 and T2 (T2 already contains T1 as its else branch).
2. M. Coordinate-frame ASCII sketch in the first task. State "+X forward, +Y left,
   counterclockwise positive" once. Reference it from C7.
3. M. New task: joystick mapping and deadband. Add `withDeadband` /
   `withRotationalDeadband` to the stubs. Inputs `leftY`, `leftX`, `rightX` in -1..1,
   `maxSpeed`. Require the WPILib negations and a deadband.
4. S. Remove solution code from T1, T4, T5 pages and the stub comment.
5. M. Merge T3 and T4 into one brake-vs-point comparison task. Delete the singleton test.
6. M. Switch `moduleDirection` and `targetDirection` to `Rotation2d`.
7. S. T6: one stateful rule (enter BRAKED only when all sticks under 0.05). Add the
   brake-over-robot-relative test.
8. S. Prose cleanup, define yaw / PID / slewing, `when` without subject code sample,
   assertion messages.

### Lesson 7, Superstructure coordination (central to Tim's goal)

1. M. Fix intake ownership: stub `Intake` takes a run request (`IDLE`, `RUN`, `EJECT`)
   and derives HOLDING from a simulated sensor. Update T1 table and tests.
2. M. Restore the two-method order: rename `advanceTransition()` to `stateTransitions()`
   and call it first, or say in one sentence why the order differs.
3. M. Make abort real: honest mid-flight `atTarget()` in the stubs, `startTransition`
   checks `atTarget()` not `state`. Delete the "simplification" paragraph.
4. L. New task: phase timeout. A phase that does not settle in N ticks returns to
   `RetractArm(STOWED)` and sets a fault flag. Reuses the L2 timer.
5. S. Tests: one-tick delay in stub Intake; re-command-after-settle test in T4.
6. S. T5: "Advance when" column; "do not command the elevator in RetractArm". Delete
   "A pedagogical note" and "Subsystems all the way down".
7. S. Checks: new-situation questions (`CLIMB_PREP -> SCORE_L4`: which phase starts?).
8. S. Name "goal state" and "current state" in T2, use them in T3 to T5. Remove the
   pre-filled enum rows from the T1 page.

### Lesson 8, Field awareness (six of nine tasks have no FSM)

1. L. FSM spine: replace T4 and T6 with one `Vision` or `Localizer` object with states
   such as `NO_TAGS`, `TRACKING`, `REJECTING`. `stateTransitions()` applies the three
   gates, `stateActions()` calls `addVisionMeasurement`. Same thresholds.
2. M. Rename the `PoseEstimator` stub to the CTRE shape: `state.pose`,
   `addVisionMeasurement(pose, timestampSeconds[, stdDevs])`. Drop `updateWithOdometry`
   from the student view.
3. CUT. T4 `fieldToRobotSpeeds`. Move the `ChassisSpeeds` field list into T3 or lesson 6.
4. S. Fix the field convention in T1 (origin blue corner, +X toward red, +Y blue's left,
   0 degrees along +X, CCW positive). ASCII sketch. Cut alliance flipping to one
   sentence. Remove east/north from T4 and the test comment.
5. S. Remove recipe sentences in T1, T2, T3, T5. Input/output tables instead.
6. S. C8: give pose, measurement, time gap, std dev as a table and ask which gate rejects.
7. S. Tests: T2 heading 90 at (4,3); T6 diagonal near-threshold jump; T1 quadrant IV;
   T3 goal behind robot; delete `reset_pose_clears_history`.
8. S. Explain operator overloading and companion factories in T1. Correct the
   `Rotation2d` wrap note in T3. Gloss or remove Kalman filter and error ellipse.

## Suggested decomposition into sub-projects

1. Sweep (all S items above, all lessons): prose, leaks, checks, tests, Kotlin-syntax
   notes, 2056 references, real-API one-liners. One plan, many small commits.
2. Lesson 4 rework: theory page plus write-a-test task, T2 reframe.
3. Lesson 6 restructure: merges, frame sketch, joystick/deadband task, Rotation2d.
4. Lesson 7 fixes: intake ownership, two-method order, honest abort, timeout task.
5. Lesson 8 rework: FSM spine, PoseEstimator rename, cut T4.
6. Lesson 5 expansion (already on the roadmap, own spec).
