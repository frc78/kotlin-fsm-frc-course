# Lesson 7 fixes. Implementation plan

> **For agentic workers:** REQUIRED SUB-SKILL: superpowers:subagent-driven-development or superpowers:executing-plans. The lead does tasks A, B, F, G. Workers do C, D, E in parallel after B is done. Workers do not commit.

**Spec:** `docs/superpowers/specs/2026-09-13-lesson-7-fixes-design.md`. Every value, name, and rule comes from there.
**Branch:** `lesson-7-fixes`.

## Global constraints

Same as `docs/superpowers/plans/2026-09-13-lessons-2-8-sweep.md`: no solution in
task.md, `// TODO: see task.md.` only, tests are the truth, ASD-STE100 prose,
assertion messages on every assert, no `internal` or `private set` in starters.

## Check cycle

1. Edit starter, test, page.
2. Reference solution in the scratchpad only.
3. Starter: `./gradlew test --tests "course.l7tM.*"` FAILS with `NotImplementedError`.
4. Solution copied in: PASSES.
5. Starter restored: FAILS.

## Task A. Stubs (lead)

- [ ] `Intake.kt`: `Request`, `Mode`, `request`, `mode`, `simulatePieceDetected`, `tick`, `requestReached`, 4-tick spin-up.
- [ ] `Elevator.kt`, `Arm.kt`: reversal rule in the `commandedTarget` setter.

## Task B. Tasks 1 to 3 and wiring (lead)

- [ ] T1 starter, test, page: `Intake.Request` table and ownership paragraph.
- [ ] T2 starter (`: Subsystem`, `override`), test (`intake.request`), page.
- [ ] T3 starter, test (+ intake-slowest test), page (`requestReached`, 4 ticks).
- [ ] `git mv` checks to `7-check-attarget` and `8-check-sequencing`; edit their text.
- [ ] Create `6-phase-timeout/` skeleton dir with `task-info.yaml`; wire `src` and `test` in `build.gradle.kts`; update `lesson-info.yaml`.
- [ ] Compile: `./gradlew compileKotlin compileTestKotlin` succeeds with T4/T5 still on the old stub API? No: T4/T5 reference `intake.commandedMode` and `modeReached()` in tests. Lead updates those two identifiers in T4/T5 tests and starters so the tree compiles before workers start.

## Task C. Task 4 (worker)

- [ ] Starter: `: Subsystem`, `periodic()` order, `stateTransitions()` with the given goal-change lines and a TODO for the advance rules.
- [ ] Page: two-method order, one-tick lag sentence, phase table unchanged.
- [ ] Tests: recompute tick counts; keep all eight; messages on every assert.
- [ ] Check cycle.

## Task D. Task 5 (worker)

- [ ] Starter: `: Subsystem`, `periodic()` order, `stateTransitions()`, honest `startTransition` and the three `settledAt` helpers as given code.
- [ ] Page: delete the simplification paragraph, add the in-flight paragraph, rename `advanceTransition` mentions.
- [ ] Tests: keep all, add `abort_mid_flight_takes_time_to_return`, recompute counts, messages everywhere.
- [ ] Check cycle.

## Task E. Task 6 (worker)

- [ ] Starter = task 5 reference solution + `phaseTimer`, `faulted`, `phaseTimeoutSeconds`, `enterPhase` TODO, `checkTimeout` TODO, `clearFault` TODO. `import frc.stubs.Timer`.
- [ ] Page: why a timeout exists (a stuck mechanism hangs the transition), the rules table, the API of `Timer` used (`restart`, `hasElapsed`), what tests do with `simulateAdvance`.
- [ ] Tests: the seven cases in the spec.
- [ ] `task-info.yaml` with `src/Superstructure.kt` visible and `test/PhaseTimeoutTest.kt` hidden.
- [ ] Check cycle.

## Task F. Counts and docs (lead)

- [ ] `CLAUDE.md`, `README.md`: 63 tasks, 41 programming.

## Task G. Verify and commit (lead)

- [ ] `./gradlew build`: compiles, all failures are placeholders.
- [ ] Commit in order: stubs + T1-T3 + wiring, T4, T5, T6, docs.
