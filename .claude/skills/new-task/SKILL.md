---
name: new-task
description: Add a new task to an existing lesson in this EduTools course — creates the task directory, starter file, hidden test, task.md, and wires it into the Gradle build and lesson-info.yaml.
---

# Add a new task to an existing lesson

Given a lesson (e.g. `3-applied-subsystems`) and a task concept, create every piece a task needs. A task is only live when ALL five steps are done — a missed step fails silently (task compiles but never runs, or doesn't appear in the Course view).

Determine the numbers first: lesson number `N` is the lesson dir's prefix; task number `M` is one past the highest existing task prefix in that lesson (unless inserting — then renumber the later task dirs AND their entries everywhere below). The package is always `course.lNtM`.

## 1. Task directory

Create `N-lesson-name/M-task-slug/` (kebab-case slug) containing:

```
M-task-slug/
├── task-info.yaml
├── task.md
├── src/SomeName.kt        # visible starter file
└── test/SomeNameTest.kt   # hidden checker
```

`task-info.yaml` (exact shape — do NOT create `task-remote-info.yaml`; the marketplace upload generates it):

```yaml
type: edu
files:
  - name: src/SomeName.kt
    visible: true
  - name: test/SomeNameTest.kt
    visible: false
```

## 2. Starter file (`src/`)

- `package course.lNtM`, imports from `frc.stubs.*` as needed.
- Subsystems are `object`s implementing `Subsystem`, with the two-method pattern: `periodic()` calls `stateTransitions()` then `stateActions()`.
- Student work goes in method bodies replaced with `TODO()` and a comment that is exactly `// TODO: see task.md.` — the comment must NOT hint at the implementation.
- Provide a `fun reset()` restoring all mutable state (state enum, commanded flags, `motor.stopMotor()`, simulated sensor values) — objects are singletons and tests depend on it.
- Everything the student shouldn't touch (enum, hardware vals, `state` with `private set`, `periodic()`, `reset()`) is written out fully; keep hardware `val`s `internal` so tests can reach them.

Model file: `2-building-an-fsm/2-transitions-with-events/src/IntakeIntro.kt`.

## 3. Hidden test (`test/`)

- Same package, `kotlin.test` (`@Test`, `@BeforeTest`, `assertEquals`) on JUnit 5.
- `@BeforeTest` calls `reset()`.
- Drive the FSM via public inputs (`commandedX = true`, `sensor.simulateDistance(...)`) and `periodic()` calls; assert on `state` and hardware outputs (`motor.lastRequest`).
- One `@Test` per behavior in the task table, snake_case names that read as sentences (`close_canrange_transitions_to_holding`). Cover the negative cases too (condition NOT met → no transition).
- Remember each `periodic()` runs transitions before actions, so a transition's action shows up in the same call.

Model file: `2-building-an-fsm/2-transitions-with-events/test/IntakeIntroTest.kt`.

## 4. Write `task.md`

- Start with a short concept explanation (new API, new idea), then a `## Your task` section.
- Specify required behavior with **tables** (State → Motor output; Current → Condition → Next) and API signatures — never copy-pasteable solution code. Code fences are fine only for API reference (e.g. `canRange.getDistance(): Double`).
- Every number in the tables (voltages, thresholds, state names) must exactly match what the hidden test asserts.

## 5. Wire it in (both places)

1. Append the task dir name to the lesson's `lesson-info.yaml` `content:` list, in order.
2. Add both source roots to `build.gradle.kts`: `"N-lesson-name/M-task-slug/src"` in the `main` block (under the lesson's comment) and `"N-lesson-name/M-task-slug/test"` in the `test` block. Keep the lists in lesson/task order.

## Verify

```bash
./gradlew test --tests "course.lNtM.*"
```

The starter's `TODO()` makes tests fail with `NotImplementedError` — that's the correct baseline. To prove the tests are satisfiable, temporarily fill in the reference solution, confirm all tests pass, then restore the `TODO()` bodies before committing. Finally run `./gradlew build` to ensure nothing else broke.
