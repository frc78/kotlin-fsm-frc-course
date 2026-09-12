# Lesson 1 Kotlin-from-zero Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Remove the lesson 9 capstone and rebuild lesson 1 as a 15-task lesson that starts from zero programming knowledge.

**Architecture:** Every course task is one directory with `task.md`, `task-info.yaml`, a visible `src/` starter, and a hidden `test/` checker. All task directories are hand-wired into one Gradle project in `build.gradle.kts`. Lesson N, task M uses package `course.lNtM`. Old lesson 1 tasks move to new numbers with `git mv`; new tasks are added between them.

**Tech Stack:** Kotlin 2.4.10, Gradle 9.5.0, `kotlin.test` on JUnit 5, JetBrains Academy (EduTools) course format.

**Spec:** `docs/superpowers/specs/2026-09-12-lesson-1-kotlin-from-zero-design.md`

## Global Constraints

- `task.md` never contains the copy-pasteable solution. It shows behavior as tables and API signatures only.
- Starter `TODO` comments say `// TODO: see task.md.` and never reveal the answer.
- Hidden tests are the source of truth. `task.md` and the tests must agree exactly on values.
- Tests check values and behavior only. They do not check names of private members.
- Choice tasks: `type: choice`, `is_multiple_choice: false`, `options` with `text`/`is_correct`, `message_correct`, `message_incorrect`, `custom_name`, `local_check: true`, no `files` key. The question text and `message_correct` must not restate the correct option.
- Programming tasks: `type: edu`, `src/*.kt` `visible: true`, `test/*Test.kt` `visible: false`.
- Theory tasks: `type: theory`, one runnable `src/*.kt` `visible: true`, wired into `sourceSets.main`.
- Every `src` dir and every `test` dir must be listed in `build.gradle.kts`, or it silently does not compile.
- All prose in ASD-STE100 style: short sentences, one instruction per sentence, no filler.
- If IntelliJ has the course open with the course-creator plugin, re-read `course-info.yaml` and `lesson-info.yaml` before each edit.
- Do not hand-edit `*-remote-info.yaml`. Moved tasks keep theirs. New tasks have none.
- Verify each task with `./gradlew test --tests "course.l1tM.*"` and the whole change set with `./gradlew build`. In zsh, check the exit code with `echo $?` on its own line right after the command.

## The check cycle for a programming task

The hidden test must fail on the starter and pass on a correct answer. Each programming task uses this cycle:

1. Write the test, the starter `src` file, `task-info.yaml`, and `task.md`.
2. Wire `src` and `test` into `build.gradle.kts`.
3. Run the task's tests. Expect FAIL with `NotImplementedError` or a wrong value.
4. Commit the starter, the test, and the docs.
5. Replace the starter body with the reference solution shown in the plan. Do not commit it.
6. Run the task's tests. Expect PASS.
7. Restore the starter with `git checkout -- <src file>`.
8. Run the task's tests again. Expect FAIL. The commit is complete.

---

### Task 1: Remove the lesson 9 capstone

**Files:**
- Delete: `9-capstone-lightning/` (already staged as deleted in the index)
- Delete: `util/src/main/kotlin/frc/stubs/lightning/Elevator.kt`, `util/src/main/kotlin/frc/stubs/lightning/Wrist.kt`
- Modify: `build.gradle.kts` (lines 72-80 and 120-127)
- Modify: `course-info.yaml` (summary lines 20-22, `additional_files` lines 52-53; the `content` entry is already removed in the working tree)
- Modify: `README.md` lines 21-25
- Modify: `CLAUDE.md` line 9
- Modify: `2-building-an-fsm/5-timers-and-timeouts/task.md` lines 7-12
- Modify: `5-motor-configuration/7-real-phoenix6-notes/task.md` lines 47-54
- Modify: `7-superstructure-coordination/4-sequencing-guards/task.md` line 46
- Modify: `7-superstructure-coordination/5-bidirectional-sequencing/task.md` line 7
- Modify: `8-field-awareness/9-fsm-architecture-at-2056/task.md` lines 5-7, 24-25, 60

- [ ] **Step 1: Confirm the working tree state**

Run: `git status --short | head -5`
Expected: lines that start with `D  9-capstone-lightning/`, plus ` M course-info.yaml` and ` M 5-motor-configuration/6-check-pid-slot/task-info.yaml`. Also confirm that no `9-capstone-lightning` directory exists on disk: `ls 9-capstone-lightning` prints "No such file or directory".

- [ ] **Step 2: Delete the unused lightning stubs**

Run:
```bash
git rm -q util/src/main/kotlin/frc/stubs/lightning/Elevator.kt util/src/main/kotlin/frc/stubs/lightning/Wrist.kt
```
Then confirm nothing else imports them: `grep -rn "stubs.lightning" --include='*.kt' . | grep -v '^./build/'` prints nothing.

- [ ] **Step 3: Remove the lesson 9 source-set lines from build.gradle.kts**

Delete these nine lines from the `main` block (the comment and the eight paths):
```kotlin
            // Lesson 9
            "9-capstone-lightning/1-ground-intake/src",
            "9-capstone-lightning/2-straightenator/src",
            "9-capstone-lightning/3-gripper-handoff/src",
            "9-capstone-lightning/4-superstructure-coral/src",
            "9-capstone-lightning/5-superstructure-algae/src",
            "9-capstone-lightning/6-drive-modes/src",
            "9-capstone-lightning/7-climber/src",
            "9-capstone-lightning/8-full-cycle/src",
```
Delete these eight lines from the `test` block:
```kotlin
            "9-capstone-lightning/1-ground-intake/test",
            "9-capstone-lightning/2-straightenator/test",
            "9-capstone-lightning/3-gripper-handoff/test",
            "9-capstone-lightning/4-superstructure-coral/test",
            "9-capstone-lightning/5-superstructure-algae/test",
            "9-capstone-lightning/6-drive-modes/test",
            "9-capstone-lightning/7-climber/test",
            "9-capstone-lightning/8-full-cycle/test",
```
Confirm: `grep -c "9-capstone" build.gradle.kts` prints `0`.

- [ ] **Step 4: Edit course-info.yaml**

Re-read the file first. Remove these three summary lines:
```yaml
    9. Capstone - Program LIGHTNING, team 2056's 2025 robot, subsystem by
       subsystem: intake, indexer, gripper, superstructure, drivebase, and
       climber, straight from their published technical binder.
```
Change the lesson 1 summary line to:
```yaml
    1. Kotlin from zero — values, functions, decisions, objects, lists,
       enums, when expressions, and sealed classes.
```
Remove these two `additional_files` entries:
```yaml
  - name: util/src/main/kotlin/frc/stubs/lightning/Elevator.kt
  - name: util/src/main/kotlin/frc/stubs/lightning/Wrist.kt
```
Confirm `content:` ends with `- 8-field-awareness`.

- [ ] **Step 5: Edit README.md**

Replace the lesson 1 line with:
````markdown
1. **Kotlin from zero** — values and types, functions, decisions, objects, loops and lists, `enum`, `when`, and sealed classes.
````
Delete the five-line item 9 (`9. **Capstone: LIGHTNING** — ...` through the Chief Delphi link line). Leave the counts line for now. Task 13 fixes it.

- [ ] **Step 6: Edit CLAUDE.md**

In the paragraph on line 9, delete everything from `Lesson 9 is a capstone` to the end of the paragraph (`...for any new capstone task.`). Leave the counts at the start of the paragraph for now. Task 13 fixes them.

- [ ] **Step 7: Rewrite the forward references in lesson task pages**

`2-building-an-fsm/5-timers-and-timeouts/task.md`, replace lines 7-12 with:
````markdown
Real robots use timed states all the time. Team 2056's 2025 robot LIGHTNING
runs a state machine on every mechanism. Its
[technical binder](https://2056.ca/wp-content/uploads/2025/05/OPR25-2056-Technical-Binder.pdf)
lists timers next to buttons and sensors as transition triggers. One example
is the short unjam pulse of the "Straightenator" indexer.
````

`5-motor-configuration/7-real-phoenix6-notes/task.md`, replace lines 47-54 (from `the error. Team 2056's` through `first thing to reach for on a real elevator.`) with:
````markdown
the error. Team 2056's
[technical binder](https://2056.ca/wp-content/uploads/2025/05/OPR25-2056-Technical-Binder.pdf)
shows Motion Magic position control on the mechanisms of their 2025 robot.
The configuration has the same shape you already know: a `MotionMagic`
config block (cruise velocity, acceleration), then a `MotionMagicVoltage`
request. It is the first thing to reach for on a real elevator.
````

`7-superstructure-coordination/4-sequencing-guards/task.md` line 46: change `That's the capstone in the next task.` to `That is the final task of this lesson.`

`7-superstructure-coordination/5-bidirectional-sequencing/task.md` line 7: change `This capstone implements both directions plus mid-transition abort.` to `This final task implements both directions plus mid-transition abort.`

`8-field-awareness/9-fsm-architecture-at-2056/task.md`:
- Lines 5-7: replace `Before Lesson 9 assembles a full robot — a recreation of FRC team 2056's 2025 robot LIGHTNING — look at how that team organizes all of these pieces.` with `Look at how FRC team 2056 organized all of these pieces on their 2025 robot LIGHTNING.`
- Lines 24-25: replace `The capstone's superstructure uses exactly this split.` with `LIGHTNING's superstructure uses exactly this split.`
- Line 60: delete the sentence `Lesson 9 builds LIGHTNING exactly this way.`

- [ ] **Step 8: Confirm no dangling references remain**

Run:
```bash
grep -rniE "lesson 9|capstone|9-capstone" --include='*.md' --include='*.kt' --include='*.yaml' --include='*.kts' . | grep -v '^./build/' | grep -v '^./docs/'
```
Expected: no output.

- [ ] **Step 9: Build**

Run: `./gradlew build -q`
Expected: exit code 0, no compile errors.

- [ ] **Step 10: Commit**

```bash
git add -A
git commit -m "Remove lesson 9 capstone and its forward references

Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
```
This commit also includes the whitespace-only change already pending in `5-motor-configuration/6-check-pid-slot/task-info.yaml`.

---

### Task 2: Move the old lesson 1 tasks to their new numbers

**Files:**
- Move: `1-kotlin-for-fsms/2-enums-for-states` → `1-kotlin-for-fsms/9-enums-for-states`
- Move: `1-kotlin-for-fsms/3-enums-with-properties` → `1-kotlin-for-fsms/11-enums-with-properties`
- Move: `1-kotlin-for-fsms/4-sealed-classes-alternative` → `1-kotlin-for-fsms/12-sealed-classes-alternative`
- Move: `1-kotlin-for-fsms/5-check-exhaustive-when` → `1-kotlin-for-fsms/13-check-exhaustive-when`
- Move: `1-kotlin-for-fsms/6-check-sealed-vs-enum` → `1-kotlin-for-fsms/14-check-sealed-vs-enum`
- Move: `1-kotlin-for-fsms/7-what-is-an-fsm` → `1-kotlin-for-fsms/15-what-is-an-fsm`
- Delete: `1-kotlin-for-fsms/1-hello-kotlin/` (task 6 in this plan replaces it)
- Modify: package lines in the moved `src` and `test` files
- Modify: `build.gradle.kts` lesson 1 lines, `1-kotlin-for-fsms/lesson-info.yaml`

**Interfaces:**
- Produces: packages `course.l1t9`, `course.l1t11`, `course.l1t12`, `course.l1t15`. Later tasks add `course.l1t2`, `l1t3`, `l1t4`, `l1t6`, `l1t7`, `l1t10` and the theory file in `course.l1t1`.

- [ ] **Step 1: Move the directories**

```bash
cd 1-kotlin-for-fsms
git mv 2-enums-for-states 9-enums-for-states
git mv 3-enums-with-properties 11-enums-with-properties
git mv 4-sealed-classes-alternative 12-sealed-classes-alternative
git mv 5-check-exhaustive-when 13-check-exhaustive-when
git mv 6-check-sealed-vs-enum 14-check-sealed-vs-enum
git mv 7-what-is-an-fsm 15-what-is-an-fsm
git rm -rq 1-hello-kotlin
cd ..
```

- [ ] **Step 2: Rename the packages**

```bash
sed -i '' 's/^package course\.l1t2$/package course.l1t9/' 1-kotlin-for-fsms/9-enums-for-states/src/IntakeState.kt 1-kotlin-for-fsms/9-enums-for-states/test/IntakeStateTest.kt
sed -i '' 's/^package course\.l1t3$/package course.l1t11/' 1-kotlin-for-fsms/11-enums-with-properties/src/ElevatorState.kt 1-kotlin-for-fsms/11-enums-with-properties/test/ElevatorStateTest.kt
sed -i '' 's/^package course\.l1t4$/package course.l1t12/' 1-kotlin-for-fsms/12-sealed-classes-alternative/src/ShooterFsmState.kt 1-kotlin-for-fsms/12-sealed-classes-alternative/test/ShooterFsmStateTest.kt
sed -i '' 's/^package course\.l1t7$/package course.l1t15/' 1-kotlin-for-fsms/15-what-is-an-fsm/src/FsmPlayground.kt
grep -rn "^package" 1-kotlin-for-fsms --include='*.kt'
```
Expected: seven lines, each with the package that matches its directory number.

- [ ] **Step 3: Update build.gradle.kts**

Replace the five lesson 1 lines in the `main` block with:
```kotlin
            // Lesson 1
            "1-kotlin-for-fsms/9-enums-for-states/src",
            "1-kotlin-for-fsms/11-enums-with-properties/src",
            "1-kotlin-for-fsms/12-sealed-classes-alternative/src",
            "1-kotlin-for-fsms/15-what-is-an-fsm/src",
```
Replace the four lesson 1 lines in the `test` block with:
```kotlin
            "1-kotlin-for-fsms/9-enums-for-states/test",
            "1-kotlin-for-fsms/11-enums-with-properties/test",
            "1-kotlin-for-fsms/12-sealed-classes-alternative/test",
```
Later tasks insert the new directories into these two lists in numeric order.

- [ ] **Step 4: Update lesson-info.yaml**

Re-read `1-kotlin-for-fsms/lesson-info.yaml` first. Write:
```yaml
content:
  - 9-enums-for-states
  - 11-enums-with-properties
  - 12-sealed-classes-alternative
  - 13-check-exhaustive-when
  - 14-check-sealed-vs-enum
  - 15-what-is-an-fsm
```
Later tasks insert their directories in numeric order.

- [ ] **Step 5: Run the moved tests**

Run: `./gradlew test --tests "course.l1t9.*" --tests "course.l1t11.*" --tests "course.l1t12.*" -q`
Expected: exit code 1, failures with `NotImplementedError` or wrong values, because the starters are unsolved. Compile errors are NOT expected. If Gradle reports a compile error, a package or path is wrong.

- [ ] **Step 6: Commit**

```bash
git add -A
git commit -m "Move lesson 1 tasks to new numbers; drop hello-kotlin

Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
```

---

### Task 3: Lesson 1 task 1, theory: Your first program

**Files:**
- Create: `1-kotlin-for-fsms/1-your-first-program/task.md`
- Create: `1-kotlin-for-fsms/1-your-first-program/task-info.yaml`
- Create: `1-kotlin-for-fsms/1-your-first-program/src/FirstProgram.kt`
- Modify: `build.gradle.kts` (add the `src` dir), `1-kotlin-for-fsms/lesson-info.yaml`

- [ ] **Step 1: Write the runnable file**

`1-kotlin-for-fsms/1-your-first-program/src/FirstProgram.kt`:
```kotlin
package course.l1t1

// Your first program. Run main() and read the output.
// Then change the text inside the quotes and run it again.

fun main() {
    val batteryVolts = 12.6
    println("Battery: $batteryVolts V")

    // A robot program runs its loop about 50 times a second.
    // repeat(3) runs the block three times, with tick = 0, 1, 2.
    repeat(3) { tick ->
        println("tick $tick")
    }
}
```

- [ ] **Step 2: Write task-info.yaml**

```yaml
type: theory
custom_name: Your First Program
files:
  - name: src/FirstProgram.kt
    visible: true
```

- [ ] **Step 3: Write task.md**

````markdown
# Your First Program

Welcome. This course teaches you to program FRC robot subsystems in Kotlin.
It starts from zero. You do not need to know any programming.

## What a program is

A program is a list of instructions. The computer does them in order, from
top to bottom. Before the program runs, a tool called the **compiler** reads
the whole list and checks it. If a line does not make sense, the compiler
stops and reports a **compiler error**. Nothing runs until every error is
fixed.

When the check passes, the program **runs**. In Kotlin, running starts at
a function named `main`.

## The robot loop

A robot program does not run once and stop. It runs one block of code, then
runs it again, about **50 times a second**, for the whole match. Each run of
that block is one **tick**. Almost all robot code you will write is a
function that the loop calls on every tick. Keep this picture in mind. It
explains why the code in later lessons looks the way it does.

## Run it

1. Open `src/FirstProgram.kt`.
2. Click the green arrow next to `fun main()`.
3. Read the output panel at the bottom of the window.

You see one line with the battery voltage, then three `tick` lines.

- `println(...)` prints one line of text.
- `val batteryVolts = 12.6` stores a value under a name. The `$batteryVolts`
  inside the quotes puts that value into the text.
- `repeat(3) { ... }` runs the block three times. The name `tick` counts
  0, 1, 2.

## Change it

Change the text inside the quotes. Change `3` to `5`. Run it again after
each change. If you make a typo, the compiler underlines it in red and
tells you what is wrong. Read the message, fix the line, and run again.
This fix-and-run loop is how all programming works.

This task has no check. Click **Next** when you are ready.
````

- [ ] **Step 4: Wire the file and the task**

In `build.gradle.kts`, insert `"1-kotlin-for-fsms/1-your-first-program/src",` directly under `// Lesson 1`. In `1-kotlin-for-fsms/lesson-info.yaml`, insert `- 1-your-first-program` as the first entry under `content:`.

- [ ] **Step 5: Run it**

Run: `./gradlew compileKotlin -q`
Expected: exit code 0. The project has no `application` plugin, so the compile check is the verification. To see the output, open the file in IntelliJ and click the green arrow next to `fun main()`.

- [ ] **Step 6: Commit**

```bash
git add 1-kotlin-for-fsms/1-your-first-program build.gradle.kts 1-kotlin-for-fsms/lesson-info.yaml
git commit -m "Add lesson 1 task 1: your first program

Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
```

---

### Task 4: Lesson 1 task 2: Values and types

**Files:**
- Create: `1-kotlin-for-fsms/2-values-and-types/task.md`, `task-info.yaml`, `src/RobotFacts.kt`, `test/RobotFactsTest.kt`
- Modify: `build.gradle.kts`, `1-kotlin-for-fsms/lesson-info.yaml`

**Interfaces:**
- Produces: `object RobotFacts` with `batteryVolts: Double`, `matchSeconds: Int`, `hasGamePiece: Boolean`, `robotName: String`, `describe(): String`.

- [ ] **Step 1: Write the test**

`1-kotlin-for-fsms/2-values-and-types/test/RobotFactsTest.kt`:
```kotlin
package course.l1t2

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RobotFactsTest {
    @Test fun battery_volts_is_12_point_6() {
        assertEquals(12.6, RobotFacts.batteryVolts)
    }

    @Test fun match_seconds_is_150() {
        assertEquals(150, RobotFacts.matchSeconds)
    }

    @Test fun has_game_piece_is_false() {
        assertFalse(RobotFacts.hasGamePiece, "hasGamePiece must be false")
    }

    @Test fun robot_name_is_not_empty() {
        assertTrue(RobotFacts.robotName.isNotEmpty(), "robotName must not be empty")
    }

    @Test fun describe_uses_all_three_values() {
        assertEquals(
            "${RobotFacts.robotName}: 12.6 V, 150 s",
            RobotFacts.describe()
        )
    }
}
```

- [ ] **Step 2: Write the starter**

`1-kotlin-for-fsms/2-values-and-types/src/RobotFacts.kt`:
```kotlin
package course.l1t2

object RobotFacts {
    // TODO: see task.md.
    val batteryVolts: Double = 0.0

    // TODO: see task.md.
    val matchSeconds: Int = 0

    // TODO: see task.md.
    val hasGamePiece: Boolean = true

    // TODO: see task.md.
    val robotName: String = ""

    // TODO: see task.md.
    fun describe(): String = ""
}
```

- [ ] **Step 3: Write task-info.yaml**

```yaml
type: edu
files:
  - name: src/RobotFacts.kt
    visible: true
  - name: test/RobotFactsTest.kt
    visible: false
```

- [ ] **Step 4: Write task.md**

````markdown
# Values and Types

A program stores values under names. Every value has a **type**. The type
tells the compiler what kind of value it is and what you can do with it.

## Four types you will use every day

| Type      | What it holds                  | Examples             |
|-----------|--------------------------------|----------------------|
| `Int`     | a whole number                 | `0`, `150`, `-3`     |
| `Double`  | a number with a decimal point  | `12.6`, `0.0`, `-4.5` |
| `Boolean` | true or false                  | `true`, `false`      |
| `String`  | text, inside double quotes     | `"Kraken"`, `""`     |

`12` and `12.0` are different. `12` is an `Int`. `12.0` is a `Double`. Motor
voltages, positions, and speeds are always `Double`. Counts are `Int`.

## Naming a value

```kotlin
val batteryVolts: Double = 12.6
var matchSeconds: Int = 150
```

- `val` names a value that never changes after it is set.
- `var` names a value that can change. `matchSeconds = matchSeconds - 1`
  makes it one smaller.
- The part after the colon is the type. Then `=` gives the value.

Use `val` unless you know the value must change.

## Putting values into text

A `$` inside a string pulls a value in:

```kotlin
val name = "Kraken"
println("Motor: $name")      // prints  Motor: Kraken
```

This is a **string template**. When a `Double` goes into a template, its
decimal point stays: `12.6` prints as `12.6`.

## Your task

Open `src/RobotFacts.kt`. Set the four properties to these values:

| Property        | Value                        |
|-----------------|------------------------------|
| `batteryVolts`  | `12.6`                       |
| `matchSeconds`  | `150`                        |
| `hasGamePiece`  | `false`                      |
| `robotName`     | any text that is not empty   |

Then make `describe()` return this text, with the three values filled in
from the properties:

```text
<robotName>: <batteryVolts> V, <matchSeconds> s
```

For example, if `robotName` is `"Kraken"`, `describe()` returns
`"Kraken: 12.6 V, 150 s"`.

## Hints

- A function that returns one value can be written on one line:
  `fun describe(): String = "..."`.
- Inside an `object`, a template can use the object's own properties by
  name: `"$batteryVolts"`.
````

- [ ] **Step 5: Wire and run, expect FAIL**

Add `"1-kotlin-for-fsms/2-values-and-types/src",` after the task 1 line in the `main` block, and `"1-kotlin-for-fsms/2-values-and-types/test",` as the first lesson 1 line in the `test` block. Add `- 2-values-and-types` after `- 1-your-first-program` in `lesson-info.yaml`.

Run: `./gradlew test --tests "course.l1t2.*" -q`
Expected: exit code 1. All five tests fail with wrong values.

- [ ] **Step 6: Commit the starter**

```bash
git add 1-kotlin-for-fsms/2-values-and-types build.gradle.kts 1-kotlin-for-fsms/lesson-info.yaml
git commit -m "Add lesson 1 task 2: values and types

Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
```

- [ ] **Step 7: Verify with the reference solution, then restore**

Replace the body of `RobotFacts.kt` with:
```kotlin
package course.l1t2

object RobotFacts {
    val batteryVolts: Double = 12.6
    val matchSeconds: Int = 150
    val hasGamePiece: Boolean = false
    val robotName: String = "Kraken"
    fun describe(): String = "$robotName: $batteryVolts V, $matchSeconds s"
}
```
Run: `./gradlew test --tests "course.l1t2.*" -q`
Expected: exit code 0.

Run: `git checkout -- 1-kotlin-for-fsms/2-values-and-types/src/RobotFacts.kt`
Then `git status --short` shows no change in that file.

---

### Task 5: Lesson 1 task 3: Functions

**Files:**
- Create: `1-kotlin-for-fsms/3-functions/task.md`, `task-info.yaml`, `src/Conversions.kt`, `test/ConversionsTest.kt`
- Modify: `build.gradle.kts`, `1-kotlin-for-fsms/lesson-info.yaml`

**Interfaces:**
- Produces: top-level `fractionToVolts(fraction: Double): Double`, `mechanismRotations(motorRotations: Double, gearRatio: Double): Double`, `rotationsToDegrees(rotations: Double): Double`.

- [ ] **Step 1: Write the test**

`1-kotlin-for-fsms/3-functions/test/ConversionsTest.kt`:
```kotlin
package course.l1t3

import kotlin.test.Test
import kotlin.test.assertEquals

class ConversionsTest {
    private val eps = 1e-9

    @Test fun full_fraction_is_twelve_volts() {
        assertEquals(12.0, fractionToVolts(1.0), eps)
    }

    @Test fun half_fraction_is_six_volts() {
        assertEquals(6.0, fractionToVolts(0.5), eps)
    }

    @Test fun negative_fraction_is_negative_volts() {
        assertEquals(-3.0, fractionToVolts(-0.25), eps)
    }

    @Test fun four_to_one_ratio_divides_by_four() {
        assertEquals(2.5, mechanismRotations(10.0, 4.0), eps)
    }

    @Test fun one_to_one_ratio_changes_nothing() {
        assertEquals(7.25, mechanismRotations(7.25, 1.0), eps)
    }

    @Test fun fifty_one_to_one_ratio() {
        assertEquals(1.0, mechanismRotations(51.0, 51.0), eps)
    }

    @Test fun one_rotation_is_360_degrees() {
        assertEquals(360.0, rotationsToDegrees(1.0), eps)
    }

    @Test fun quarter_rotation_is_90_degrees() {
        assertEquals(90.0, rotationsToDegrees(0.25), eps)
    }

    @Test fun negative_rotations_give_negative_degrees() {
        assertEquals(-180.0, rotationsToDegrees(-0.5), eps)
    }
}
```

- [ ] **Step 2: Write the starter**

`1-kotlin-for-fsms/3-functions/src/Conversions.kt`:
```kotlin
package course.l1t3

// TODO: see task.md.
fun fractionToVolts(fraction: Double): Double = TODO()

// TODO: see task.md.
fun mechanismRotations(motorRotations: Double, gearRatio: Double): Double = TODO()

// TODO: see task.md.
fun rotationsToDegrees(rotations: Double): Double = TODO()
```

- [ ] **Step 3: Write task-info.yaml**

```yaml
type: edu
files:
  - name: src/Conversions.kt
    visible: true
  - name: test/ConversionsTest.kt
    visible: false
```

- [ ] **Step 4: Write task.md**

````markdown
# Functions

A **function** is a named block of code that takes values in and gives one
value back. You write it once and call it as many times as you need.

## The parts of a function

```kotlin
fun metersToFeet(meters: Double): Double = meters * 3.28084
```

| Part              | Meaning                                              |
|-------------------|------------------------------------------------------|
| `fun`             | starts a function                                    |
| `metersToFeet`    | the name                                             |
| `meters: Double`  | a **parameter**: a named input with its type         |
| `: Double`        | the **return type**: the type of the value it gives back |
| `= meters * 3.28084` | the **body**: the value it returns                |

A function with more than one parameter separates them with commas:
`fun add(a: Double, b: Double): Double = a + b`.

## Calling a function

```kotlin
val feet = metersToFeet(2.0)     // feet is 6.56168
```

You give a value for each parameter, in order. The call becomes the value
the function returns. A function can call another function inside its body.

## Arithmetic

`+`, `-`, `*` (multiply), `/` (divide). On two `Double` values, `/` gives
the exact decimal result.

## Gear ratios, in one paragraph

Motors turn fast. Mechanisms turn slowly. A gearbox between them has a
**ratio**. A 4:1 ratio means the motor turns four times for one turn of
the mechanism. So mechanism rotations = motor rotations divided by the
ratio. You will use this idea in every lesson about motors.

## Your task

Open `src/Conversions.kt`. Complete three functions:

| Function                                                                | Returns                              |
|-------------------------------------------------------------------------|--------------------------------------|
| `fractionToVolts(fraction: Double): Double`                             | `fraction` times 12.0                |
| `mechanismRotations(motorRotations: Double, gearRatio: Double): Double` | `motorRotations` divided by `gearRatio` |
| `rotationsToDegrees(rotations: Double): Double`                         | `rotations` times 360.0              |

Examples: `fractionToVolts(0.5)` is `6.0`. `mechanismRotations(10.0, 4.0)`
is `2.5`. `rotationsToDegrees(0.25)` is `90.0`.

## Hint

Each function is one line. Replace `TODO()` with the arithmetic.
````

- [ ] **Step 5: Wire and run, expect FAIL**

Add `"1-kotlin-for-fsms/3-functions/src",` and `"1-kotlin-for-fsms/3-functions/test",` in numeric order in `build.gradle.kts`. Add `- 3-functions` after `- 2-values-and-types` in `lesson-info.yaml`.

Run: `./gradlew test --tests "course.l1t3.*" -q`
Expected: exit code 1. All nine tests fail with `NotImplementedError`.

- [ ] **Step 6: Commit the starter**

```bash
git add 1-kotlin-for-fsms/3-functions build.gradle.kts 1-kotlin-for-fsms/lesson-info.yaml
git commit -m "Add lesson 1 task 3: functions

Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
```

- [ ] **Step 7: Verify with the reference solution, then restore**

Reference body:
```kotlin
fun fractionToVolts(fraction: Double): Double = fraction * 12.0
fun mechanismRotations(motorRotations: Double, gearRatio: Double): Double = motorRotations / gearRatio
fun rotationsToDegrees(rotations: Double): Double = rotations * 360.0
```
Run: `./gradlew test --tests "course.l1t3.*" -q` → exit code 0.
Run: `git checkout -- 1-kotlin-for-fsms/3-functions/src/Conversions.kt`.

---

### Task 6: Lesson 1 task 4: Decisions

**Files:**
- Create: `1-kotlin-for-fsms/4-decisions/task.md`, `task-info.yaml`, `src/Decisions.kt`, `test/DecisionsTest.kt`
- Modify: `build.gradle.kts`, `1-kotlin-for-fsms/lesson-info.yaml`

**Interfaces:**
- Produces: top-level `isAtTarget(position: Double, target: Double, tolerance: Double): Boolean`, `shouldRunIntake(commanded: Boolean, hasGamePiece: Boolean): Boolean`, `clampVolts(volts: Double): Double`.

- [ ] **Step 1: Write the test**

`1-kotlin-for-fsms/4-decisions/test/DecisionsTest.kt`:
```kotlin
package course.l1t4

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DecisionsTest {
    @Test fun at_target_when_exactly_on_target() {
        assertTrue(isAtTarget(5.0, 5.0, 0.1))
    }

    @Test fun at_target_when_inside_tolerance() {
        assertTrue(isAtTarget(5.05, 5.0, 0.1))
        assertTrue(isAtTarget(4.95, 5.0, 0.1))
    }

    @Test fun at_target_when_exactly_at_tolerance_edge() {
        assertTrue(isAtTarget(5.1, 5.0, 0.1), "error equal to tolerance counts as at target")
    }

    @Test fun not_at_target_when_outside_tolerance() {
        assertFalse(isAtTarget(5.2, 5.0, 0.1))
        assertFalse(isAtTarget(4.8, 5.0, 0.1))
    }

    @Test fun intake_runs_when_commanded_and_empty() {
        assertTrue(shouldRunIntake(commanded = true, hasGamePiece = false))
    }

    @Test fun intake_stops_when_holding_a_piece() {
        assertFalse(shouldRunIntake(commanded = true, hasGamePiece = true))
    }

    @Test fun intake_stops_when_not_commanded() {
        assertFalse(shouldRunIntake(commanded = false, hasGamePiece = false))
        assertFalse(shouldRunIntake(commanded = false, hasGamePiece = true))
    }

    @Test fun clamp_leaves_in_range_values_alone() {
        assertEquals(6.0, clampVolts(6.0))
        assertEquals(-6.0, clampVolts(-6.0))
        assertEquals(0.0, clampVolts(0.0))
    }

    @Test fun clamp_limits_high_values_to_12() {
        assertEquals(12.0, clampVolts(15.0))
        assertEquals(12.0, clampVolts(12.0))
    }

    @Test fun clamp_limits_low_values_to_minus_12() {
        assertEquals(-12.0, clampVolts(-20.0))
        assertEquals(-12.0, clampVolts(-12.0))
    }
}
```

- [ ] **Step 2: Write the starter**

`1-kotlin-for-fsms/4-decisions/src/Decisions.kt`:
```kotlin
package course.l1t4

import kotlin.math.abs

// TODO: see task.md.
fun isAtTarget(position: Double, target: Double, tolerance: Double): Boolean = TODO()

// TODO: see task.md.
fun shouldRunIntake(commanded: Boolean, hasGamePiece: Boolean): Boolean = TODO()

// TODO: see task.md.
fun clampVolts(volts: Double): Double = TODO()
```

- [ ] **Step 3: Write task-info.yaml**

```yaml
type: edu
files:
  - name: src/Decisions.kt
    visible: true
  - name: test/DecisionsTest.kt
    visible: false
```

- [ ] **Step 4: Write task.md**

````markdown
# Decisions

Robot code makes decisions on every tick. Is the elevator at its target?
Should the intake run? Is this voltage safe? Each decision is a `Boolean`
question with a `true` or `false` answer.

## Comparisons

A comparison takes two values and gives a `Boolean`:

| Operator | Meaning                  |
|----------|--------------------------|
| `<`      | less than                |
| `<=`     | less than or equal       |
| `>`      | greater than             |
| `>=`     | greater than or equal    |
| `==`     | equal                    |
| `!=`     | not equal                |

`volts > 12.0` is `true` when `volts` is 15.0 and `false` when it is 6.0.

## Combining Booleans

| Operator | Meaning                                | Example                          |
|----------|----------------------------------------|----------------------------------|
| `&&`     | and: both must be true                 | `commanded && hasPiece`          |
| `\|\|`   | or: at least one must be true          | `tooHot \|\| tooFast`             |
| `!`      | not: flips true and false              | `!hasPiece`                      |

## if / else as a value

```kotlin
val mode = if (volts > 11.5) "OK" else "LOW"
```

`if` tests a `Boolean`. When it is `true`, the value is the first branch.
When it is `false`, the value is the `else` branch. A function can return
an `if` directly:

```kotlin
fun signOf(x: Double): Double = if (x < 0.0) -1.0 else 1.0
```

You can chain them: `if (a) x else if (b) y else z`.

## Absolute value

`abs(x)` from `kotlin.math` gives the distance of `x` from zero.
`abs(-0.3)` is `0.3`. The starter file already imports it.

## Your task

Open `src/Decisions.kt`. Complete three functions:

| Function                                                                   | Returns `true` when, or value                                    |
|----------------------------------------------------------------------------|-------------------------------------------------------------------|
| `isAtTarget(position: Double, target: Double, tolerance: Double): Boolean` | the distance between `position` and `target` is less than or equal to `tolerance` |
| `shouldRunIntake(commanded: Boolean, hasGamePiece: Boolean): Boolean`      | the intake is commanded and there is no game piece                |
| `clampVolts(volts: Double): Double`                                        | `volts`, but never above `12.0` and never below `-12.0`           |

Examples: `isAtTarget(5.1, 5.0, 0.1)` is `true`. `isAtTarget(5.2, 5.0, 0.1)`
is `false`. `clampVolts(15.0)` is `12.0`. `clampVolts(-20.0)` is `-12.0`.

## Hints

- `isAtTarget` is one comparison on an `abs(...)`.
- `shouldRunIntake` is two Booleans joined with one operator.
- `clampVolts` is an `if` / `else if` / `else` chain with three branches.
````

- [ ] **Step 5: Wire and run, expect FAIL**

Add `"1-kotlin-for-fsms/4-decisions/src",` and `"1-kotlin-for-fsms/4-decisions/test",` in numeric order in `build.gradle.kts`. Add `- 4-decisions` after `- 3-functions` in `lesson-info.yaml`.

Run: `./gradlew test --tests "course.l1t4.*" -q`
Expected: exit code 1. All tests fail with `NotImplementedError`.

- [ ] **Step 6: Commit the starter**

```bash
git add 1-kotlin-for-fsms/4-decisions build.gradle.kts 1-kotlin-for-fsms/lesson-info.yaml
git commit -m "Add lesson 1 task 4: decisions

Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
```

- [ ] **Step 7: Verify with the reference solution, then restore**

Reference body:
```kotlin
fun isAtTarget(position: Double, target: Double, tolerance: Double): Boolean =
    abs(position - target) <= tolerance
fun shouldRunIntake(commanded: Boolean, hasGamePiece: Boolean): Boolean =
    commanded && !hasGamePiece
fun clampVolts(volts: Double): Double =
    if (volts > 12.0) 12.0 else if (volts < -12.0) -12.0 else volts
```
Run: `./gradlew test --tests "course.l1t4.*" -q` → exit code 0.
Run: `git checkout -- 1-kotlin-for-fsms/4-decisions/src/Decisions.kt`.

---

### Task 7: Lesson 1 task 5, check: Predict the output

**Files:**
- Create: `1-kotlin-for-fsms/5-check-predict-output/task.md`, `task-info.yaml`
- Modify: `1-kotlin-for-fsms/lesson-info.yaml`

- [ ] **Step 1: Write task.md**

````markdown
# Check: Predict the Output

Read this code. Do not run it.

```kotlin
val volts = 11.2
val hasGamePiece = true

val message = if (volts < 11.5 && !hasGamePiece) {
    "low battery, empty"
} else if (volts < 11.5) {
    "low battery, holding"
} else if (hasGamePiece) {
    "holding"
} else {
    "ready"
}
println(message)
```

What does the program print?
````

- [ ] **Step 2: Write task-info.yaml**

```yaml
type: choice
is_multiple_choice: false
options:
  - text: "low battery, empty"
    is_correct: false
  - text: "low battery, holding"
    is_correct: true
  - text: "holding"
    is_correct: false
  - text: "ready"
    is_correct: false
message_correct: "The first test needs both parts to be true. One part is false,\
  \ so the chain moves to the second test, which only looks at the voltage."
message_incorrect: "Work through the chain one test at a time. For each test, write\
  \ down true or false. The first true test picks the branch, and the rest are skipped.\
  \ Remember that ! flips a Boolean."
custom_name: "Check: predict the output"
local_check: true
```

- [ ] **Step 3: Wire the task**

Add `- 5-check-predict-output` after `- 4-decisions` in `1-kotlin-for-fsms/lesson-info.yaml`. Choice tasks need no `build.gradle.kts` entry.

- [ ] **Step 4: Check the YAML parses**

Run:
```bash
python3 -c "import yaml,sys; d=yaml.safe_load(open('1-kotlin-for-fsms/5-check-predict-output/task-info.yaml')); print(sum(o['is_correct'] for o in d['options']))"
```
Expected: `1`. If `yaml` is not installed, run `ruby -ryaml -e "d=YAML.load_file('1-kotlin-for-fsms/5-check-predict-output/task-info.yaml'); puts d['options'].count{|o| o['is_correct']}"` instead.

- [ ] **Step 5: Commit**

```bash
git add 1-kotlin-for-fsms/5-check-predict-output 1-kotlin-for-fsms/lesson-info.yaml
git commit -m "Add lesson 1 task 5: predict-the-output check

Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
```

---

### Task 8: Lesson 1 task 6: Objects

**Files:**
- Create: `1-kotlin-for-fsms/6-objects/task.md`, `task-info.yaml`, `src/Battery.kt`, `test/BatteryTest.kt`
- Modify: `build.gradle.kts`, `1-kotlin-for-fsms/lesson-info.yaml`

**Interfaces:**
- Produces: `object Battery` with `var volts: Double`, `isLow(): Boolean`, `percent(): Int`, `reset()`.

- [ ] **Step 1: Write the test**

`1-kotlin-for-fsms/6-objects/test/BatteryTest.kt`:
```kotlin
package course.l1t6

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BatteryTest {
    @BeforeTest fun setUp() {
        Battery.reset()
    }

    @Test fun full_battery_is_not_low() {
        assertFalse(Battery.isLow(), "12.6 V is not low")
    }

    @Test fun below_11_point_5_is_low() {
        Battery.volts = 11.4
        assertTrue(Battery.isLow(), "11.4 V is low")
    }

    @Test fun exactly_11_point_5_is_not_low() {
        Battery.volts = 11.5
        assertFalse(Battery.isLow(), "11.5 V is the limit and is not low")
    }

    @Test fun full_battery_is_100_percent() {
        assertEquals(100, Battery.percent())
    }

    @Test fun ten_volts_is_0_percent() {
        Battery.volts = 10.0
        assertEquals(0, Battery.percent())
    }

    @Test fun midpoint_is_50_percent() {
        Battery.volts = 11.3
        assertEquals(50, Battery.percent())
    }

    @Test fun percent_rounds_to_nearest_whole_number() {
        Battery.volts = 11.0
        assertEquals(38, Battery.percent(), "1.0 / 2.6 * 100 = 38.46, rounds to 38")
    }

    @Test fun percent_never_goes_above_100() {
        Battery.volts = 13.2
        assertEquals(100, Battery.percent())
    }

    @Test fun percent_never_goes_below_0() {
        Battery.volts = 9.0
        assertEquals(0, Battery.percent())
    }

    @Test fun reset_restores_full_voltage() {
        Battery.volts = 9.0
        Battery.reset()
        assertEquals(12.6, Battery.volts)
    }
}
```

- [ ] **Step 2: Write the starter**

`1-kotlin-for-fsms/6-objects/src/Battery.kt`:
```kotlin
package course.l1t6

import kotlin.math.roundToInt

object Battery {
    var volts: Double = 12.6

    // TODO: see task.md.
    fun isLow(): Boolean = TODO()

    // TODO: see task.md.
    fun percent(): Int = TODO()

    fun reset() {
        volts = 12.6
    }
}
```

- [ ] **Step 3: Write task-info.yaml**

```yaml
type: edu
files:
  - name: src/Battery.kt
    visible: true
  - name: test/BatteryTest.kt
    visible: false
```

- [ ] **Step 4: Write task.md**

````markdown
# Objects

So far, values and functions have stood alone. Real code groups them. An
**object** is one named thing that owns its own values and its own
functions.

```kotlin
object Compressor {
    var pressurePsi: Double = 0.0
    fun isFull(): Boolean = pressurePsi >= 115.0
}
```

- `object Compressor` creates exactly one `Compressor`. There is no second
  one. A robot has one compressor, one intake, one elevator. Each gets one
  object. Every subsystem in this course is an `object`.
- Values inside an object are called **properties**. Functions inside are
  called **methods**. They can use each other by name.
- Code outside the object reaches inside with a dot:
  `Compressor.pressurePsi = 120.0` then `Compressor.isFull()`.

## The reset() habit

An object keeps its values between uses. A test that changes `volts`
would leak that change into the next test. So every object in this course
has a `reset()` method that puts the values back to their start. The
tests call it before each check. You will see `reset()` in every subsystem
from Lesson 2 on.

## Your task

Open `src/Battery.kt`. The object has one property, `volts`, and a
`reset()` that is already written. Complete two methods:

| Method      | Returns                                                                              |
|-------------|--------------------------------------------------------------------------------------|
| `isLow()`   | `true` when `volts` is below `11.5`                                                  |
| `percent()` | charge as a whole number from 0 to 100. `10.0` V is 0. `12.6` V is 100. Values in between are on a straight line. Round to the nearest whole number. Never return less than 0 or more than 100. |

Examples: at `11.3` V, `percent()` is `50`. At `11.0` V it is `38`. At
`13.2` V it is `100`. At `9.0` V it is `0`.

## Hints

- `isLow()` is one comparison on `volts`.
- For `percent()`: first find the fraction of the way from 10.0 to 12.6,
  as a `Double`. Multiply by 100. Then `.roundToInt()` turns a `Double`
  into the nearest `Int`. The starter imports it. Then
  `.coerceIn(0, 100)` limits an `Int` to a range.
````

- [ ] **Step 5: Wire and run, expect FAIL**

Add `"1-kotlin-for-fsms/6-objects/src",` and `"1-kotlin-for-fsms/6-objects/test",` in numeric order in `build.gradle.kts`. Add `- 6-objects` after `- 5-check-predict-output` in `lesson-info.yaml`.

Run: `./gradlew test --tests "course.l1t6.*" -q`
Expected: exit code 1. Nine tests fail with `NotImplementedError`. `reset_restores_full_voltage` passes.

- [ ] **Step 6: Commit the starter**

```bash
git add 1-kotlin-for-fsms/6-objects build.gradle.kts 1-kotlin-for-fsms/lesson-info.yaml
git commit -m "Add lesson 1 task 6: objects

Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
```

- [ ] **Step 7: Verify with the reference solution, then restore**

Reference bodies:
```kotlin
    fun isLow(): Boolean = volts < 11.5

    fun percent(): Int = ((volts - 10.0) / 2.6 * 100.0).roundToInt().coerceIn(0, 100)
```
Run: `./gradlew test --tests "course.l1t6.*" -q` → exit code 0.
Run: `git checkout -- 1-kotlin-for-fsms/6-objects/src/Battery.kt`.

---

### Task 9: Lesson 1 task 7: Loops and lists

**Files:**
- Create: `1-kotlin-for-fsms/7-loops-and-lists/task.md`, `task-info.yaml`, `src/Readings.kt`, `test/ReadingsTest.kt`
- Modify: `build.gradle.kts`, `1-kotlin-for-fsms/lesson-info.yaml`

**Interfaces:**
- Produces: top-level `averageVolts(readings: List<Double>): Double`, `anyModuleFaulted(faults: List<Boolean>): Boolean`.

- [ ] **Step 1: Write the test**

`1-kotlin-for-fsms/7-loops-and-lists/test/ReadingsTest.kt`:
```kotlin
package course.l1t7

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReadingsTest {
    private val eps = 1e-9

    @Test fun average_of_empty_list_is_zero() {
        assertEquals(0.0, averageVolts(emptyList()), eps)
    }

    @Test fun average_of_one_reading_is_that_reading() {
        assertEquals(12.4, averageVolts(listOf(12.4)), eps)
    }

    @Test fun average_of_several_readings() {
        assertEquals(12.0, averageVolts(listOf(12.6, 11.8, 11.6)), eps)
    }

    @Test fun average_of_four_readings() {
        assertEquals(11.75, averageVolts(listOf(12.0, 11.5, 12.0, 11.5)), eps)
    }

    @Test fun no_modules_means_no_fault() {
        assertFalse(anyModuleFaulted(emptyList()))
    }

    @Test fun all_false_means_no_fault() {
        assertFalse(anyModuleFaulted(listOf(false, false, false, false)))
    }

    @Test fun one_true_means_a_fault() {
        assertTrue(anyModuleFaulted(listOf(false, false, true, false)))
    }

    @Test fun last_module_faulted_is_still_a_fault() {
        assertTrue(anyModuleFaulted(listOf(false, false, false, true)))
    }
}
```

- [ ] **Step 2: Write the starter**

`1-kotlin-for-fsms/7-loops-and-lists/src/Readings.kt`:
```kotlin
package course.l1t7

// TODO: see task.md.
fun averageVolts(readings: List<Double>): Double = TODO()

// TODO: see task.md.
fun anyModuleFaulted(faults: List<Boolean>): Boolean = TODO()
```

- [ ] **Step 3: Write task-info.yaml**

```yaml
type: edu
files:
  - name: src/Readings.kt
    visible: true
  - name: test/ReadingsTest.kt
    visible: false
```

- [ ] **Step 4: Write task.md**

````markdown
# Loops and Lists

Sometimes one name holds many values. A swerve drive has four modules. A
sensor filter keeps the last few readings. Kotlin puts many values of one
type into a **List**.

## Lists

```kotlin
val moduleTemps: List<Double> = listOf(31.5, 30.0, 33.2, 29.8)
```

| Expression          | Meaning                               |
|---------------------|---------------------------------------|
| `listOf(a, b, c)`   | makes a list with those values        |
| `emptyList()`       | a list with nothing in it             |
| `list.size`         | how many values it holds, as an `Int` |
| `list[0]`           | the first value. Counting starts at 0 |
| `list.isEmpty()`    | `true` when `size` is 0               |

`List<Double>` reads as "a list of Doubles". The type inside the angle
brackets is the type of every element.

## Loops

A **loop** runs one block once for each element:

```kotlin
var total = 0.0
for (temp in moduleTemps) {
    total = total + temp
}
```

Each time through, `temp` is the next element. After the loop, `total`
holds the sum. The `var` outside the loop is how the loop remembers a
result.

`repeat(n) { ... }` runs a block `n` times when there is no list. You saw
it in task 1.

## Where loops live in robot code

Loops are rare in state machine code. The robot loop itself, the one that
runs 50 times a second, is the loop that matters. It is written for you by
WPILib. You will see `for` when code touches all swerve modules or all
motors of a mechanism at once.

## Your task

Open `src/Readings.kt`. Complete two functions:

| Function                                            | Returns                                                       |
|-----------------------------------------------------|---------------------------------------------------------------|
| `averageVolts(readings: List<Double>): Double`      | the sum of the readings divided by their count. `0.0` when the list is empty. |
| `anyModuleFaulted(faults: List<Boolean>): Boolean`  | `true` when at least one element is `true`. `false` for an empty list. |

Examples: `averageVolts(listOf(12.6, 11.8, 11.6))` is `12.0`.
`anyModuleFaulted(listOf(false, true, false, false))` is `true`.

## Hints

- Check for an empty list first with an `if`. Dividing by zero gives a
  strange value, not an error.
- For `anyModuleFaulted`, start a `var found = false` before the loop and
  set it to `true` inside the loop when you see a `true`.
- Kotlin also has `readings.average()` and `faults.any { it }`. They do
  the same job in one call. Write the loop version first so you know what
  they do.
````

- [ ] **Step 5: Wire and run, expect FAIL**

Add `"1-kotlin-for-fsms/7-loops-and-lists/src",` and `"1-kotlin-for-fsms/7-loops-and-lists/test",` in numeric order in `build.gradle.kts`. Add `- 7-loops-and-lists` after `- 6-objects` in `lesson-info.yaml`.

Run: `./gradlew test --tests "course.l1t7.*" -q`
Expected: exit code 1. All eight tests fail with `NotImplementedError`.

- [ ] **Step 6: Commit the starter**

```bash
git add 1-kotlin-for-fsms/7-loops-and-lists build.gradle.kts 1-kotlin-for-fsms/lesson-info.yaml
git commit -m "Add lesson 1 task 7: loops and lists

Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
```

- [ ] **Step 7: Verify with the reference solution, then restore**

Reference bodies:
```kotlin
fun averageVolts(readings: List<Double>): Double {
    if (readings.isEmpty()) return 0.0
    var total = 0.0
    for (r in readings) {
        total = total + r
    }
    return total / readings.size
}

fun anyModuleFaulted(faults: List<Boolean>): Boolean {
    var found = false
    for (f in faults) {
        if (f) found = true
    }
    return found
}
```
Run: `./gradlew test --tests "course.l1t7.*" -q` → exit code 0.
Run: `git checkout -- 1-kotlin-for-fsms/7-loops-and-lists/src/Readings.kt`.

---

### Task 10: Lesson 1 task 8, check: Read the error

**Files:**
- Create: `1-kotlin-for-fsms/8-check-read-the-error/task.md`, `task-info.yaml`
- Modify: `1-kotlin-for-fsms/lesson-info.yaml`

- [ ] **Step 1: Confirm the compiler message text**

Create a scratch file with a type mismatch and compile it, so the wording in the question matches Kotlin 2.4.10. Do this outside the project tree:
```bash
S=/private/tmp/claude-502/-Users-tim-winters-src-kotlin-wpilib-fsm-course/ccf4b196-0330-4952-a360-07a37e52b185/scratchpad
mkdir -p $S/errcheck/src && cd $S/errcheck
cat > settings.gradle.kts <<'X'
plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0" }
X
cat > build.gradle.kts <<'X'
plugins { kotlin("jvm") version "2.4.10" }
repositories { mavenCentral() }
kotlin { jvmToolchain(17) }
sourceSets { main { kotlin.srcDirs("src") } }
X
cat > src/Err.kt <<'X'
val batteryVolts: Double = "12.6"
X
cp -r /Users/tim.winters/src/kotlin-wpilib-fsm-course/gradle . && cp /Users/tim.winters/src/kotlin-wpilib-fsm-course/gradlew .
./gradlew compileKotlin -q 2>&1 | grep -i "mismatch"
cd /Users/tim.winters/src/kotlin-wpilib-fsm-course
```
Expected: one line like `e: file:///.../Err.kt:1:28 Initializer type mismatch: expected 'Double', actual 'String'.` Use the exact wording after the position in the question below. If the wording differs, update the question to match.

- [ ] **Step 2: Write task.md**

````markdown
# Check: Read the Error

Errors are not failures. They are the tools telling you exactly where to
look. This course shows you two kinds.

## A compiler error

The compiler checks your code before it runs. In the IDE, the bad line is
underlined in red. Hover over it, or look in the **Build** panel, to read
the message. A teammate writes this line:

```kotlin
val batteryVolts: Double = "12.6"
```

The compiler reports:

```text
Initializer type mismatch: expected 'Double', actual 'String'.
```

## A failed check

When the compiler is happy, the **Check** button runs the hidden tests.
A failed test prints a line like this in the **Check** panel:

```
#educational_plugin FAILED + expected:<12.6> but was:<0.0>
```

The `expected` part is what the test wanted. The `but was` part is what
your code gave.

## The question

Read both messages above. Which statement explains what is wrong in each
case?
````

- [ ] **Step 3: Write task-info.yaml**

```yaml
type: choice
is_multiple_choice: false
options:
  - text: "Compiler: the value 12.6 is too large for a Double. Check: the test itself\
      \ has the wrong number in it."
    is_correct: false
  - text: "Compiler: the quotes make \"12.6\" a String, and a Double property cannot hold\
      \ a String. Check: a property that should be 12.6 was left at 0.0."
    is_correct: true
  - text: "Compiler: the name batteryVolts is spelled wrong. Check: the code crashed\
      \ before the test could run."
    is_correct: false
  - text: "Compiler: val must be var for a Double. Check: the test expected 0.0 and got\
      \ 12.6."
    is_correct: false
message_correct: "Read the type names in a compiler message. They tell you what the\
  \ compiler wanted and what it found. In a failed check, expected is the test's\
  \ value and but was is yours."
message_incorrect: "Look at the two type names in the compiler message, and at which\
  \ side of the check message says expected. Then re-read task 2 on types and string\
  \ quotes."
custom_name: "Check: read the error"
local_check: true
```

- [ ] **Step 4: Wire the task and check the YAML**

Add `- 8-check-read-the-error` after `- 7-loops-and-lists` in `1-kotlin-for-fsms/lesson-info.yaml`.

Run the same YAML parse check as in task 7 step 4, on this file. Expected: `1`.

- [ ] **Step 5: Commit**

```bash
git add 1-kotlin-for-fsms/8-check-read-the-error 1-kotlin-for-fsms/lesson-info.yaml
git commit -m "Add lesson 1 task 8: read-the-error check

Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
```

---

### Task 11: Lesson 1 task 10: The when expression

**Files:**
- Create: `1-kotlin-for-fsms/10-when-expression/task.md`, `task-info.yaml`, `src/RollerState.kt`, `test/RollerStateTest.kt`
- Modify: `build.gradle.kts`, `1-kotlin-for-fsms/lesson-info.yaml`

**Interfaces:**
- Consumes: `enum class` syntax from task 9 (`9-enums-for-states`).
- Produces: `enum class RollerState { STOPPED, FORWARD, REVERSE }` and top-level `voltsFor(state: RollerState): Double`.

- [ ] **Step 1: Write the test**

`1-kotlin-for-fsms/10-when-expression/test/RollerStateTest.kt`:
```kotlin
package course.l1t10

import kotlin.test.Test
import kotlin.test.assertEquals

class RollerStateTest {
    @Test fun stopped_is_zero_volts() {
        assertEquals(0.0, voltsFor(RollerState.STOPPED))
    }

    @Test fun forward_is_eight_volts() {
        assertEquals(8.0, voltsFor(RollerState.FORWARD))
    }

    @Test fun reverse_is_minus_four_volts() {
        assertEquals(-4.0, voltsFor(RollerState.REVERSE))
    }

    @Test fun every_state_has_a_voltage() {
        for (s in RollerState.entries) {
            voltsFor(s)
        }
    }
}
```

- [ ] **Step 2: Write the starter**

`1-kotlin-for-fsms/10-when-expression/src/RollerState.kt`:
```kotlin
package course.l1t10

enum class RollerState { STOPPED, FORWARD, REVERSE }

fun voltsFor(state: RollerState): Double = when (state) {
    // TODO: see task.md. Write one branch per state, then DELETE the else line.
    else -> TODO("write the three branches and remove this else")
}
```

- [ ] **Step 3: Write task-info.yaml**

```yaml
type: edu
files:
  - name: src/RollerState.kt
    visible: true
  - name: test/RollerStateTest.kt
    visible: false
```

- [ ] **Step 4: Write task.md**

````markdown
# The when Expression

In task 9 you made an enum: a fixed list of named values. Now you pick a
different result for each value. An `if` / `else if` chain can do it, but
Kotlin has a cleaner tool: **`when`**.

## when as a value

```kotlin
enum class Gear { LOW, HIGH }

fun maxSpeed(gear: Gear): Double = when (gear) {
    Gear.LOW -> 2.0
    Gear.HIGH -> 4.5
}
```

`when (gear)` looks at the value. Each line is a **branch**:
`value -> result`. The branch that matches gives the result of the whole
`when`. A function can return a `when` directly, the same way it returns
an `if`.

## Exhaustive: every value must have a branch

When `when` is used as a value, the compiler counts the branches against
the enum. If one value has no branch, the code does not compile. This is
a feature. Add a fourth `Gear` later, and the compiler points at every
`when` that needs a new line. You never ship a state the code forgot.

An `else ->` branch catches everything not listed. It also switches this
check off. In this course, `when` over an enum lists every value and has
no `else`.

## Your task

Open `src/RollerState.kt`. The enum is written. Complete `voltsFor` so
that each state gives its voltage:

| State     | Volts  |
|-----------|--------|
| `STOPPED` | `0.0`  |
| `FORWARD` | `8.0`  |
| `REVERSE` | `-4.0` |

Write one branch per state. Then delete the `else` line. With all three
branches present, the `when` is exhaustive and compiles without it.

## Hint

A branch names the enum value with its type: `RollerState.STOPPED -> ...`.
````

- [ ] **Step 5: Wire and run, expect FAIL**

Add `"1-kotlin-for-fsms/10-when-expression/src",` after the `9-enums-for-states/src` line and `"1-kotlin-for-fsms/10-when-expression/test",` after the `9-enums-for-states/test` line in `build.gradle.kts`. Add `- 10-when-expression` after `- 9-enums-for-states` in `lesson-info.yaml`.

Run: `./gradlew test --tests "course.l1t10.*" -q`
Expected: exit code 1. All four tests fail with `NotImplementedError`.

- [ ] **Step 6: Commit the starter**

```bash
git add 1-kotlin-for-fsms/10-when-expression build.gradle.kts 1-kotlin-for-fsms/lesson-info.yaml
git commit -m "Add lesson 1 task 10: the when expression

Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
```

- [ ] **Step 7: Verify with the reference solution, then restore**

Reference body:
```kotlin
fun voltsFor(state: RollerState): Double = when (state) {
    RollerState.STOPPED -> 0.0
    RollerState.FORWARD -> 8.0
    RollerState.REVERSE -> -4.0
}
```
Run: `./gradlew test --tests "course.l1t10.*" -q` → exit code 0.
Run: `git checkout -- 1-kotlin-for-fsms/10-when-expression/src/RollerState.kt`.

---

### Task 12: Update the moved tasks' text and cross-references

**Files:**
- Modify: `1-kotlin-for-fsms/9-enums-for-states/task.md`
- Modify: `1-kotlin-for-fsms/13-check-exhaustive-when/task-info.yaml` line 17
- Modify: `1-kotlin-for-fsms/14-check-sealed-vs-enum/task-info.yaml` lines 5, 21-23
- Modify: `1-kotlin-for-fsms/15-what-is-an-fsm/task.md` lines 3-6, 30-31

- [ ] **Step 1: Rewrite task 9's opening and its forward reference**

In `1-kotlin-for-fsms/9-enums-for-states/task.md`, replace the first paragraph and code block (lines 1-11, from `# Enums for States` through `Every value is a unique singleton — there's exactly one \`RED\`.`) with:
````markdown
# Enums for States

You know four types: `Int`, `Double`, `Boolean`, `String`. Now you make a
type of your own. An **enum** is a type with a fixed list of named values,
and nothing else. It is the simplest way to say "this thing is in exactly
one of these modes".

```kotlin
enum class TrafficLight {
    RED, YELLOW, GREEN
}
```

`TrafficLight` is now a type, like `Boolean`. A `Boolean` has two possible
values. A `TrafficLight` has three: `TrafficLight.RED`, `TrafficLight.YELLOW`,
and `TrafficLight.GREEN`. There is exactly one `RED`. A finite state
machine has a *finite* set of named states, so an enum is the natural fit.
````
Then in the "Why enums for states" list, replace `You'll use this in the last task of this lesson, and in every subsystem from Lesson 2 on.` with `You will use this in task 10, and in every subsystem from Lesson 2 on.`

- [ ] **Step 2: Fix task 13's feedback reference**

In `1-kotlin-for-fsms/13-check-exhaustive-when/task-info.yaml`, change `section in task 2` to `section in task 9`.

- [ ] **Step 3: Fix task 14's references**

In `1-kotlin-for-fsms/14-check-sealed-vs-enum/task-info.yaml`:
- Line 5: change `exactly like ElevatorState in task 3.` to `exactly like ElevatorState in task 11.`
- Line 21: change `Compare tasks 3 and 4:` to `Compare tasks 11 and 12:`

- [ ] **Step 4: Fix task 15's references**

In `1-kotlin-for-fsms/15-what-is-an-fsm/task.md`:
- Lines 3-5: change `enums name a fixed set of states (task 2), enum properties attach compile-time data to them (task 3), and sealed classes carry runtime data (task 4).` to `enums name a fixed set of states (task 9), enum properties attach compile-time data to them (task 11), and sealed classes carry runtime data (task 12).`
- Line 31: change `(you declared exactly those four in task 2)` to `(you declared exactly those four in task 9)`.

- [ ] **Step 5: Confirm no stale numbers remain**

Run:
```bash
grep -rnE "task [0-9]+" 1-kotlin-for-fsms --include='*.md' --include='*.yaml'
```
Expected: every number named is one of 1, 2, 9, 10, 11, 12 and points at a task that teaches what the sentence says. Also run:
```bash
grep -rniE "lesson 1\b" --include='*.md' . | grep -v '^./1-kotlin' | grep -v '^./docs' | grep -v '^./build'
```
Expected: any hit refers to lesson 1 as a whole, never to a task number inside it.

- [ ] **Step 6: Build and commit**

Run: `./gradlew compileKotlin compileTestKotlin -q` → exit code 0.
```bash
git add 1-kotlin-for-fsms
git commit -m "Update lesson 1 cross-references to new task numbers

Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
```

---

### Task 13: Counts, summaries, and the final build

**Files:**
- Modify: `README.md` lines 10-11 and lesson 1 line
- Modify: `CLAUDE.md` line 9 counts and line 12 of the summary paragraph
- Modify: `course-info.yaml` lesson 1 summary line (done in task 1; confirm)

- [ ] **Step 1: Count the tasks from the lesson files**

Run:
```bash
for d in [1-8]-*/; do grep -c '^  - ' "$d/lesson-info.yaml"; done | paste -sd+ | bc
for t in edu choice theory; do echo -n "$t: "; grep -l "^type: $t" [1-8]-*/*/task-info.yaml | wc -l; done
```
Expected totals: 8 lessons, 62 tasks. `edu: 40`, `choice: 18`, `theory: 4`. If the numbers differ, use the printed numbers and find out why before you continue.

- [ ] **Step 2: Update README.md**

Replace lines 10-11 with the printed numbers, in this shape:
````markdown
8 lessons, 62 tasks (40 programming exercises, 18 comprehension checks,
4 theory pages):
````
Confirm the lesson 1 line reads `1. **Kotlin from zero** — values and types, functions, decisions, objects, loops and lists, \`enum\`, \`when\`, and sealed classes.` (set in task 1).

- [ ] **Step 3: Update CLAUDE.md**

On line 9, change `9 lessons, 64 tasks — 43 programming (\`type: edu\`), 18 comprehension checks (\`type: choice\`), 3 theory pages (\`type: theory\`).` to the printed numbers: `8 lessons, 62 tasks — 40 programming (\`type: edu\`), 18 comprehension checks (\`type: choice\`), 4 theory pages (\`type: theory\`).`

Add one sentence at the end of that paragraph: `Lesson 1 starts from zero programming knowledge: tasks 1 to 8 teach programs, types, functions, decisions, objects, lists, and error reading before task 9 introduces enums. Later lessons may assume all of it.`

- [ ] **Step 4: Confirm build.gradle.kts lists every directory**

Run:
```bash
for d in [1-8]-*/*/src; do grep -q "\"$d\"" build.gradle.kts || echo "MISSING src: $d"; done
for d in [1-8]-*/*/test; do grep -q "\"$d\"" build.gradle.kts || echo "MISSING test: $d"; done
for d in $(grep -oE '"[1-8]-[^"]+"' build.gradle.kts | tr -d '"'); do [ -d "$d" ] || echo "STALE: $d"; done
```
Expected: no output.

- [ ] **Step 5: Confirm lesson-info.yaml matches the directories**

Run:
```bash
diff <(sed -n 's/^  - //p' 1-kotlin-for-fsms/lesson-info.yaml) <(ls -d 1-kotlin-for-fsms/*/ | xargs -n1 basename | sort -n)
```
Expected: no output, and the order in the file is 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15.

- [ ] **Step 6: Full build**

Run: `./gradlew build -q; echo $?`
Expected: compile succeeds for every task. The exit code is 1 because starters fail their own tests. Confirm no `e:` compiler lines appear in the output:
```bash
./gradlew build 2>&1 | grep -E "^e: " ; echo "compile errors above, if any"
```
Expected: only the echo line.

Also confirm the moved and untouched tasks still behave. Pick one test that has a known-good solution shape and confirm the failure is an assertion, not a missing class:
```bash
./gradlew test --tests "course.l1t9.*" 2>&1 | grep "#educational_plugin FAILED" | head -3
```
Expected: lines like `#educational_plugin FAILED + expected:<4> but was:<0>`.

- [ ] **Step 7: Commit**

```bash
git add README.md CLAUDE.md
git commit -m "Update course counts and summaries for the lesson 1 rebuild

Co-Authored-By: Claude Fable 5.1 <noreply@anthropic.com>"
```

---

## Self-review notes

- Spec coverage: capstone removal (task 1), renumbering (task 2), new tasks 1 to 8 and 10 (tasks 3 to 11), moved-task text and cross-references (task 12), counts and final build (task 13). Every spec section has a task.
- The spec table lists the theory file package as `course.l1t1`. Task 3 uses that.
- Values in every task.md match its test: 12.6 / 150 / false (task 4); 12.0, 4:1, 360 (task 5); 11.5 limit, 10.0 to 12.6, 38 at 11.0 (task 8); 0.0 / 8.0 / -4.0 (task 11).
