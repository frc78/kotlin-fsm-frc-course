# Lesson 1: Kotlin from zero, for FRC. Design

Date: 2026-09-12

## Goal

Rebuild lesson 1 so that a student with no programming knowledge can start
the course. Remove the lesson 9 capstone at the same time.

This spec is the first of three. Later specs cover the motor configuration
expansion and the robot-analysis comprehension checks.

## Decisions already made

- The lesson 9 capstone is deleted. No replacement capstone is planned.
- Lesson 1 grows in place. It is one lesson with 15 tasks. Lessons 2 to 8
  keep their numbers and packages.
- Lesson 1 covers: programs and the robot loop, values and types,
  functions, decisions, objects, loops and lists, reading errors, enums,
  `when`, enums with properties, sealed classes, and what an FSM is.
- Robot-analysis skills are assessed with choice tasks only. That is a
  later spec.

## Lesson 1 task list

T is a theory page, E a programming task, C a comprehension check.

| #  | Type | Directory                      | Package       | Source                |
|----|------|--------------------------------|---------------|-----------------------|
| 1  | T    | `1-your-first-program`         | `course.l1t1` | new                   |
| 2  | E    | `2-values-and-types`           | `course.l1t2` | new                   |
| 3  | E    | `3-functions`                  | `course.l1t3` | new                   |
| 4  | E    | `4-decisions`                  | `course.l1t4` | new                   |
| 5  | C    | `5-check-predict-output`       | none          | new                   |
| 6  | E    | `6-objects`                    | `course.l1t6` | new, replaces old 1   |
| 7  | E    | `7-loops-and-lists`            | `course.l1t7` | new                   |
| 8  | C    | `8-check-read-the-error`       | none          | new                   |
| 9  | E    | `9-enums-for-states`           | `course.l1t9` | old 2, moved          |
| 10 | E    | `10-when-expression`           | `course.l1t10`| new                   |
| 11 | E    | `11-enums-with-properties`     | `course.l1t11`| old 3, moved          |
| 12 | E    | `12-sealed-classes-alternative`| `course.l1t12`| old 4, moved          |
| 13 | C    | `13-check-exhaustive-when`     | none          | old 5, moved          |
| 14 | C    | `14-check-sealed-vs-enum`      | none          | old 6, moved          |
| 15 | T    | `15-what-is-an-fsm`            | `course.l1t15`| old 7, moved          |

The old `1-hello-kotlin` task is deleted. Task 6 teaches its content.

## New task designs

Every task.md shows behavior as tables and API signatures. It never shows
the solution. Starter `TODO` comments say `// TODO: see task.md.`
Every example uses a robot quantity: battery volts, motor rotations, gear
ratios, beam-break booleans.

### Task 1, theory: Your first program

File: `src/FirstProgram.kt`, visible, runnable.

`main()` prints a battery voltage, then uses `repeat(3)` to print
`tick 0`, `tick 1`, `tick 2`.

The page explains:

- A program is a list of instructions. The compiler checks the whole
  list before anything runs. A compiler error means the check failed.
- Running a program executes `main()` from top to bottom.
- A robot program runs its loop about 50 times a second. Most robot
  code is a function that the loop calls again and again.
- How to run the file, and how to change the text and run it again.

### Task 2: Values and types

File: `src/RobotFacts.kt`. Starter:

```kotlin
object RobotFacts {
    val batteryVolts: Double = 0.0     // TODO
    val matchSeconds: Int = 0          // TODO
    val hasGamePiece: Boolean = true   // TODO
    val robotName: String = ""         // TODO
    fun describe(): String = ""        // TODO
}
```

Required values:

| Property        | Value                      |
|-----------------|----------------------------|
| `batteryVolts`  | `12.6`                     |
| `matchSeconds`  | `150`                      |
| `hasGamePiece`  | `false`                    |
| `robotName`     | any text that is not empty |

`describe()` returns `"<robotName>: <batteryVolts> V, <matchSeconds> s"`.

The page teaches `Int`, `Double`, `Boolean`, `String`, `val` vs `var`,
string templates, and why `12` and `12.0` have different types. It
shows a `var` that changes with `matchSeconds = matchSeconds - 1`.

Tests check each value and the `describe()` text built from the
student's own `robotName`.

### Task 3: Functions

File: `src/Conversions.kt`. Three top-level functions:

| Function                                                              | Rule                             |
|-----------------------------------------------------------------------|----------------------------------|
| `fractionToVolts(fraction: Double): Double`                           | fraction times 12.0              |
| `mechanismRotations(motorRotations: Double, gearRatio: Double): Double` | motor rotations divided by ratio |
| `rotationsToDegrees(rotations: Double): Double`                       | rotations times 360.0            |

The page teaches parameters, return types, calling a function, and
calling one function from another. It explains a gear ratio in one
paragraph: a 4:1 ratio means the motor turns four times for one turn of
the mechanism.

Tests check several inputs per function with a tolerance of 1e-9.

### Task 4: Decisions

File: `src/Decisions.kt`. Three top-level functions:

| Function                                                                    | Rule                                                 |
|-----------------------------------------------------------------------------|------------------------------------------------------|
| `isAtTarget(position: Double, target: Double, tolerance: Double): Boolean`  | absolute error is less than or equal to tolerance    |
| `shouldRunIntake(commanded: Boolean, hasGamePiece: Boolean): Boolean`       | commanded and not holding a game piece               |
| `clampVolts(volts: Double): Double`                                         | limit to the range -12.0 to 12.0                     |

The page teaches `if`/`else` as a value, `<`, `<=`, `>`, `>=`, `==`,
`!=`, `&&`, `||`, `!`, and `kotlin.math.abs`. The starter imports `abs`.

Tests check both sides of every boundary.

### Task 5, check: Predict the output

A short `if`/`else if`/`else` chain over `volts` and `hasGamePiece`,
with a `&&` and a `!`. The student picks the printed text. Four options,
one correct. The feedback text does not restate the correct option.

### Task 6: Objects

File: `src/Battery.kt`. Starter:

```kotlin
object Battery {
    var volts: Double = 12.6
    fun isLow(): Boolean = TODO()
    fun percent(): Int = TODO()
    fun reset() { volts = 12.6 }
}
```

| Function    | Rule                                                              |
|-------------|-------------------------------------------------------------------|
| `isLow()`   | true when `volts` is below 11.5                                   |
| `percent()` | linear from 10.0 V at 0 to 12.6 V at 100, limited to 0 to 100, rounded to `Int` |

The page teaches that an object is one named thing with its own values
and functions, that code reads them with a dot, and that a robot has
one intake so it gets one object. It introduces `reset()` as the way
tests start from a known state.

Tests set `volts`, call the functions, and call `reset()` before each
test.

### Task 7: Loops and lists

File: `src/Readings.kt`. Two top-level functions:

| Function                                        | Rule                                       |
|-------------------------------------------------|--------------------------------------------|
| `averageVolts(readings: List<Double>): Double`  | sum divided by count; 0.0 for an empty list |
| `anyModuleFaulted(faults: List<Boolean>): Boolean` | true if any element is true             |

The page teaches `listOf`, `list[0]`, `list.size`, `for (x in list)`,
and `repeat(n)`. It states that loops are rare in FSM code, and that
the robot loop is the loop that matters.

Tests include an empty list, a one-element list, and a longer list.

### Task 8, check: Read the error

The question shows two messages:

1. A Kotlin compiler message for a type mismatch, for example assigning
   `"12.6"` to a `Double`.
2. A failed-test line in the format the course's test listener prints.

Four options name the cause. One is correct. The page before the
question explains where each message appears in the IDE.

### Task 10: The `when` expression

File: `src/RollerState.kt`. Starter:

```kotlin
enum class RollerState { STOPPED, FORWARD, REVERSE }

fun voltsFor(state: RollerState): Double = when (state) {
    // TODO: see task.md, then delete the else line.
    else -> TODO()
}
```

| State     | Volts  |
|-----------|--------|
| `STOPPED` | `0.0`  |
| `FORWARD` | `8.0`  |
| `REVERSE` | `-4.0` |

The page teaches `when` used as a value, one branch per enum value, and
that the compiler requires every value to be covered when there is no
`else`. Tasks 13 and 15 depend on this fact.

Tests check the three values.

### Moved tasks 9, 11 to 15

Content stays. Changes:

- Task 9 gains one paragraph that names an enum as a new type the
  student defines, in the same way `Int` and `Boolean` are types.
- Task 9 no longer says the student will use exhaustive `when` "in the
  last task of this lesson". It points to task 10.
- Cross-references inside lesson 1 update: old "task 2" is now 9, old
  "task 3" is 11, old "task 4" is 12.
- Packages update to the new task numbers in `src` and `test`.

## Mechanics

- Move old task directories with `git mv` so history follows.
- Update `1-kotlin-for-fsms/lesson-info.yaml` to the 15 directories in
  order.
- Update the `sourceSets` block in `build.gradle.kts`: add every new
  `src` and `test` directory, and rename the moved ones.
- Every programming task has `task-info.yaml` with the `src` file
  `visible: true` and the `test` file `visible: false`.
- Choice tasks have `task-info.yaml` with `type: choice`,
  `is_multiple_choice: false`, `options`, `message_correct`,
  `message_incorrect`, `local_check: true`, and no files.
- The theory task has `type: theory` and its runnable file
  `visible: true`.
- Lessons 2 to 8 refer to lesson 1 by lesson number only. Confirm with
  a grep for "task N" inside lesson 1 and "Lesson 1" outside it.

## Capstone removal

Done in the same change set:

- Commit the staged deletion of `9-capstone-lightning/`.
- Remove the 16 lesson 9 source-set lines from `build.gradle.kts`.
- Delete `util/src/main/kotlin/frc/stubs/lightning/Elevator.kt` and
  `Wrist.kt`. Nothing outside lesson 9 uses them. Remove their two
  entries from `additional_files` in `course-info.yaml`.
- Remove the lesson 9 line from the `course-info.yaml` summary and the
  README lesson list.
- Rewrite forward references to lesson 9 and the capstone in:
  - `2-building-an-fsm/5-timers-and-timeouts/task.md`
  - `5-motor-configuration/7-real-phoenix6-notes/task.md`. Keep the
    MotionMagic section. Drop the capstone pointer.
  - `7-superstructure-coordination/4-sequencing-guards/task.md` and
    `5-bidirectional-sequencing/task.md`. These use "capstone" to mean
    the lesson's own final task. Reword to "the final task of this
    lesson".
  - `8-field-awareness/9-fsm-architecture-at-2056/task.md`. Keep the
    page as a theory page about how team 2056 structures FSMs. Remove
    the sentences that say lesson 9 builds it.
- Update CLAUDE.md: 8 lessons, new task totals, remove the lesson 9
  paragraph. Update the README counts. Count from the lesson-info files
  after the change.

## Testing

- Every new programming task has one hidden `kotlin.test` class in the
  same package. Tests check values and behavior. They do not check
  names of private members.
- Choice tasks follow the no-answer-leak rule: `message_correct` and
  the question text do not restate the correct option.
- Final check: `./gradlew build` compiles every task and runs every
  test.

## Out of scope

- Motor configuration expansion: soft limits, sensor-to-mechanism
  ratio, gravity type, MotionMagic, gains from recalc.
- Robot-analysis choice tasks.
