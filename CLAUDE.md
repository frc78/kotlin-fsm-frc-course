# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this repo is

A JetBrains Academy (EduTools) course teaching FRC students to write finite state machines for robot subsystems in Kotlin. It is **not** a robot project: real WPILib/Phoenix6 dependencies are deliberately absent. Instead, `util/src/main/kotlin/frc/stubs/` contains lightweight stubs (`TalonFX`, `CANrange`, `SwerveRequest`, `Pose2d`, …) that mirror the real API surface so tasks compile fast without the FRC toolchain.

8 lessons, 63 tasks — 41 programming (`type: edu`), 18 comprehension checks (`type: choice`), 4 theory pages (`type: theory`). Choice tasks carry their question in task.md and their options/answers/feedback in task-info.yaml (`is_multiple_choice`, `options` with `text`/`is_correct`, `message_correct`/`message_incorrect`, `local_check: true`, no files). Theory tasks are task.md prose plus one visible runnable src file, which must be wired into build.gradle.kts like any source dir. Directory layout is the course structure: `course-info.yaml` lists lesson dirs → each lesson's `lesson-info.yaml` lists task dirs → each task dir holds `task.md`, `task-info.yaml`, `src/`, and `test/`. Lesson 1 starts from zero programming knowledge: tasks 1 to 8 teach programs, types, functions, decisions, objects, lists, and error reading before task 9 introduces enums. Later lessons may assume all of it.

Note: if the course is open in IntelliJ with the course-creator plugin while you edit, the IDE live-syncs `course-info.yaml`/`lesson-info.yaml` (it may auto-add/remove entries and bump `yaml_version`) — re-read those files before editing them.

## Commands

```bash
./gradlew build                                   # compile everything + run all tests
./gradlew test                                    # all tests
./gradlew test --tests "course.l3t1.*"            # one task's tests (lesson 3, task 1)
./gradlew test --tests "course.l3t1.IntakeTest"   # one test class
```

Package naming is the routing key: lesson N, task M → `package course.lNtM`. Both the task's `src/` and `test/` files use the same package.

The Gradle wrapper is **9.5.0** with Kotlin plugin **2.4.10** — chosen so Gradle runs on any JDK 17–25 (9.1.0 is the Java-25 minimum; 9.5.0 is the newest release the Kotlin plugin is fully tested with). Compilation stays on a JDK 17 toolchain (`jvmToolchain(17)`), auto-provisioned by the foojay resolver in settings.gradle.kts — this matches `jvm_language_level: JDK_17` in course-info.yaml, so don't bump the toolchain without updating both. Note the JetBrains Academy course template still ships Gradle 8.3; this repo deliberately deviates — if the EduTools Check button ever breaks after a plugin update, the wrapper version is the first suspect.

## Architecture

**Single Gradle project, hand-wired source roots.** Every task's `src/` and `test/` directory is explicitly listed in the `sourceSets` block of `build.gradle.kts`, alongside the shared `util/src/main/kotlin` stubs. A new task that isn't added there silently won't compile or test. This also means all tasks share one classpath — hence the unique `course.lNtM` package per task.

**Task anatomy.** `task-info.yaml` declares files and visibility: `src/*.kt` is `visible: true` (the student's editable starter file, with `TODO()` placeholders), `test/*Test.kt` is `visible: false` (the hidden checker run by EduTools' Check button). `task.md` is the description shown in the IDE.

**Subsystems are Kotlin `object`s** (singletons), so state leaks between tests. Every task's starter file provides a `reset()` that tests call in `@BeforeTest`. Stubs expose `simulate*()` setters for sensor inputs (e.g. `canRange.simulateDistance(0.03)`) and inspection points for outputs (e.g. `motor.lastRequest`). Tests use `kotlin.test` on JUnit 5.

**The FSM pattern taught throughout:** `periodic()` calls `stateTransitions()` (read inputs, reassign `state`) then `stateActions()` (a `when(state)` that commands hardware). Keep new content in this two-method style — the course intentionally does not use WPILib's command-based paradigm.

**EduTools test listener.** The `tasks.test` block in `build.gradle.kts` prints `#educational_plugin FAILED + …` lines on test failure; EduTools parses these to show assertion messages in the Check UI. Don't remove or reformat it.

**Generated/marketplace files — don't hand-edit:** `*-remote-info.yaml` (marketplace IDs written on upload), `.coursecreator/`, and the course `.zip` archives. `.courseignore` controls what's excluded from the generated course archive.

## Content rules

- `task.md` must never contain the copy-pasteable solution. Describe behavior with tables (state/condition/next-state, state/motor-output) and API signatures, not implementation code. Starter-file TODO comments say `// TODO: see task.md.` — they must not reveal the answer either.
- Hidden tests are the source of truth for what a task requires; `task.md` and the tests must agree exactly (thresholds, voltages, state names).
