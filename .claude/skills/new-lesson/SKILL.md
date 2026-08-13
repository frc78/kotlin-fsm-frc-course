---
name: new-lesson
description: Add a new lesson (a group of tasks) to this EduTools course — creates the lesson directory, registers it in course-info.yaml, then uses the new-task skill for each task inside it.
---

# Add a new lesson

A lesson is a numbered directory of tasks plus two registrations. Pick the next lesson number `N` (one past the highest existing prefix; if inserting earlier, renumber every later lesson dir, its tasks' packages `course.lNtM`, and all `build.gradle.kts` entries — avoid inserting unless asked).

## 1. Lesson directory

Create `N-lesson-slug/` (kebab-case) containing only:

```yaml
# N-lesson-slug/lesson-info.yaml
content:
  - 1-first-task-slug
  - 2-second-task-slug
```

Do NOT create `lesson-remote-info.yaml` — the marketplace upload generates it.

## 2. Register the lesson

- Append `N-lesson-slug` to the `content:` list in `course-info.yaml` (order = course order).
- Add a matching line to the lesson list in `course-info.yaml`'s `summary:` and to the "What's inside" list in `README.md`.
- In `build.gradle.kts`, add a `// Lesson N` comment group in both the `main` and `test` sourceSet lists (tasks fill it in).

## 3. Create the tasks

For each task, follow the `new-task` skill (`.claude/skills/new-task/SKILL.md`) — it covers the task directory, starter file, hidden test, task.md rules, and Gradle wiring.

If the lesson introduces new hardware or API concepts not yet stubbed, add the stub to `util/src/main/kotlin/frc/stubs/` first, mirroring the real WPILib/Phoenix6 API surface (real names and signatures, plus `simulate*()` input setters and `last*`/inspection outputs for tests), and register the new file under `additional_files:` in `course-info.yaml`.

## Content guidance

- Lessons progress from concept → applied: early tasks isolate one new idea; later tasks combine it with prior lessons' patterns.
- Stay in the course's two-method FSM style (`stateTransitions()` / `stateActions()` called from `periodic()`); no command-based paradigm.
- 2–6 tasks per lesson matches the rest of the course.

## Verify

```bash
./gradlew build          # everything still compiles, all tests run
```

Then open the project in IntelliJ with EduTools to confirm the lesson and tasks appear in the Course view in the right order.
