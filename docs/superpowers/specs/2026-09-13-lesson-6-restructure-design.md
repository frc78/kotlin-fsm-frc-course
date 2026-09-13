# Lesson 6 restructure. Design

Date: 2026-09-13. Source: `docs/superpowers/reviews/2026-09-12-lessons-2-8-review.md`,
lesson 6 proposals 1, 2, 3, 5, 6.

## Goal

Cut the five near-identical build-one-request tasks to three, add the one task
every real teleop drive needs (joystick mapping with deadbands), and make the
swerve stubs take `Rotation2d` where the real API does. Lesson 6 goes from 8 tasks
(6 programming, 2 checks) to 7 tasks (5 programming, 2 checks).

## Stub changes (`util/src/main/kotlin/frc/stubs/swerve/SwerveRequest.kt`)

| Type                      | Change                                                                                                             |
|---------------------------|--------------------------------------------------------------------------------------------------------------------|
| `FieldCentric`            | add `val deadband: Double = 0.0`, `val rotationalDeadband: Double = 0.0`, `fun withDeadband(metersPerSecond: Double)`, `fun withRotationalDeadband(radiansPerSecond: Double)` |
| `RobotCentric`            | same four additions                                                                                                |
| `PointWheelsAt`           | `val moduleDirection: Rotation2d = Rotation2d()`, `fun withModuleDirection(direction: Rotation2d)`                |
| `FieldCentricFacingAngle` | `val targetDirection: Rotation2d = Rotation2d()`, `fun withTargetDirection(direction: Rotation2d)`                |
| header comment            | replace "Directions are in degrees in this course; real Phoenix6 takes Rotation2d." with "Directions are `Rotation2d`, as in real Phoenix6." |

All setters return a copy, like the existing ones. The stub stores the deadbands
and does not apply them. The page says the real drivetrain applies them.
`SwerveDrivetrain` is unchanged. Tests read `lastRequest` fields.

## Task table

| #  | Directory                     | Package        | Source                                             | remote-info                          |
|----|-------------------------------|----------------|----------------------------------------------------|--------------------------------------|
| 1  | `1-field-and-robot-centric`   | `course.l6t1`  | merge of old 1 and 2                               | `git mv` from `1-field-centric`; delete old 2's |
| 2  | `2-brake-and-point-wheels`    | `course.l6t2`  | merge of old 3 and 4                               | `git mv` from `3-swerve-brake`; delete old 4's |
| 3  | `3-facing-angle`              | `course.l6t3`  | old 5 moved                                        | moves with `git mv`                  |
| 4  | `4-joystick-mapping`          | `course.l6t4`  | new                                                | none                                 |
| 5  | `5-drive-mode-fsm`            | `course.l6t5`  | old 6 moved                                        | moves with `git mv`                  |
| 6  | `6-check-request-frames`      | none           | old 7 moved                                        | moves with `git mv`                  |
| 7  | `7-check-facing-angle`        | none           | old 8 moved                                        | moves with `git mv`                  |

Old directories `2-robot-centric` and `4-point-wheels-at` are deleted. Their
marketplace IDs are lost; the next upload creates none for them because the tasks
no longer exist.

## Task 1. Field- and robot-centric

Page: request objects and `setControl` (old 1), the frame sketch, the builder table,
the deadband note as one sentence ("task 4 sets them"), robot-centric meaning and
the four "when to use it" bullets (old 2), the task table. Sketch:

```
        far alliance wall
   +-----------------------+
   |          +X ^         |
   |             |         |
   |   +Y <------+         |   counterclockwise = positive
   |                       |
   +-----------------------+
        your driver station
```

Starter: `object Drive` with `drivetrain`, `reset()` sending `Idle`, and
`fun teleopDrive(vx: Double, vy: Double, omega: Double, robotRelative: Boolean)`.

| `robotRelative` | Request                                       |
|-----------------|-----------------------------------------------|
| `false`         | `FieldCentric` with `vx`, `vy`, `omega`       |
| `true`          | `RobotCentric` with `vx`, `vy`, `omega`       |

Tests (data-class equality, one `setControl` per call):

| Test                                  | Call                                  | Expected                                          |
|---------------------------------------|---------------------------------------|---------------------------------------------------|
| `field_centric_when_not_robot_relative` | `(1.0, 0.5, 0.3, false)`            | `FieldCentric(1.0, 0.5, 0.3)`                     |
| `robot_centric_when_robot_relative`   | `(1.0, 0.5, 0.3, true)`               | `RobotCentric(1.0, 0.5, 0.3)`                     |
| `negative_values_pass_through`        | `(-1.5, -0.7, -1.0, false)`           | `FieldCentric(-1.5, -0.7, -1.0)`                  |
| `zero_velocity_still_sends_a_request` | `(0.0, 0.0, 0.0, true)`               | `RobotCentric()`                                  |
| `toggle_changes_request_type`         | `false` then `true`, vx 1.0           | `FieldCentric(velocityX = 1.0)` then `RobotCentric(velocityX = 1.0)` |

## Task 2. Brake and point wheels

Page: X-lock sketch and the brake-vs-zero-velocity contrast (old 3), slewing
defined (old 4), the `data object` note, `withModuleDirection` takes a
`Rotation2d` built with `Rotation2d.fromDegrees(...)`, and this table:

| Request           | Use it when                                           | Robot moves? |
|-------------------|-------------------------------------------------------|--------------|
| `SwerveDriveBrake`| you must hold position against a push                 | no           |
| `PointWheelsAt`   | you are about to burst in a known direction from rest | no           |

Starter: `fun applyBrake()` and `fun pointWheels(directionDegrees: Double)`.

| Test                          | Call                 | Expected                                        |
|-------------------------------|----------------------|-------------------------------------------------|
| `applies_brake`               | `applyBrake()`       | `SwerveDriveBrake`                              |
| `points_wheels_at_zero`       | `pointWheels(0.0)`   | `PointWheelsAt(Rotation2d.fromDegrees(0.0))`    |
| `points_wheels_at_45`         | `pointWheels(45.0)`  | `PointWheelsAt(Rotation2d.fromDegrees(45.0))`   |
| `points_wheels_at_negative`   | `pointWheels(-90.0)` | `PointWheelsAt(Rotation2d.fromDegrees(-90.0))`  |
| `brake_after_point_replaces_request` | `pointWheels(45.0)` then `applyBrake()` | `SwerveDriveBrake`             |

No singleton test.

## Task 3. Facing angle

Old task 5. Page: `withTargetDirection` takes `Rotation2d`; delete the "stub takes
degrees" paragraph; keep the `HeadingController` note. "The next task" becomes
"task 5". Starter: `fun aimWhileDriving(vx: Double, vy: Double, targetDegrees: Double)`.

| Test                              | Call                  | Expected                                                              |
|-----------------------------------|-----------------------|-----------------------------------------------------------------------|
| `aims_with_zero_velocity`         | `(0.0, 0.0, 90.0)`    | `FieldCentricFacingAngle(0.0, 0.0, Rotation2d.fromDegrees(90.0))`     |
| `aims_with_translation`           | `(1.5, -0.5, 180.0)`  | `FieldCentricFacingAngle(1.5, -0.5, Rotation2d.fromDegrees(180.0))`   |
| `changing_target_changes_request` | `0.0` then `45.0`, vx 1.0 | `targetDirection` `fromDegrees(0.0)` then `fromDegrees(45.0)`     |

## Task 4. Joystick mapping (new)

Page: why raw stick values cannot go to `setControl` (a stick reads -1..1, a request
wants m/s and rad/s), the WPILib joystick convention table, the deadband
definition (the small stick motion the drivetrain ignores so a stick that rests
off center does not creep the robot; the real `withDeadband` is applied inside the
drivetrain), and the task table. No solution code.

| Stick input        | Reads       | Must become                             |
|--------------------|-------------|-----------------------------------------|
| left stick forward | `leftY` < 0 | `velocityX` > 0 (+X, downfield)         |
| left stick left    | `leftX` < 0 | `velocityY` > 0 (+Y, left)              |
| right stick right  | `rightX` > 0| `rotationalRate` < 0 (clockwise)        |

Starter:

```kotlin
object Drive {
    val drivetrain = SwerveDrivetrain()
    val maxSpeedMetersPerSecond = 4.5
    val maxTurnRadiansPerSecond = 6.0
    fun drive(leftX: Double, leftY: Double, rightX: Double)   // TODO
    fun reset()
}
```

Rules: `velocityX = -leftY * 4.5`, `velocityY = -leftX * 4.5`,
`rotationalRate = -rightX * 6.0`, `deadband = 0.45`, `rotationalDeadband = 0.6`.
Request type `FieldCentric`. One `setControl` per call.

Tests read fields of `lastRequest` with `assertEquals(expected, actual, 1e-9)`
after `assertIs<FieldCentric>`:

| Test                                  | Call               | `velocityX` | `velocityY` | `rotationalRate` |
|---------------------------------------|--------------------|-------------|-------------|------------------|
| `full_forward_is_plus_x`              | `(0.0, -1.0, 0.0)` | 4.5         | 0.0         | 0.0              |
| `left_is_plus_y`                      | `(-0.5, 0.0, 0.0)` | 0.0         | 2.25        | 0.0              |
| `right_stick_right_is_clockwise`      | `(0.0, 0.0, 0.5)`  | 0.0         | 0.0         | -3.0             |
| `stick_back_is_minus_x`               | `(0.0, 0.2, 0.0)`  | -0.9        | 0.0         | 0.0              |
| `all_axes_together`                   | `(1.0, -1.0, -1.0)`| 4.5         | -4.5        | 6.0              |
| `deadbands_are_ten_percent_of_max`    | any                | `deadband` 0.45, `rotationalDeadband` 0.6 |  |
| `centered_sticks_send_zero_velocities`| `(0.0, 0.0, 0.0)`  | 0.0         | 0.0         | 0.0, deadbands still set |

## Task 5. Drive-mode FSM

Old task 6, moved. Two edits: `AIMING` builds
`withTargetDirection(Rotation2d.fromDegrees(aimTargetDegrees))`; the page says so.
Tests: the `AIMING` expectations use `Rotation2d.fromDegrees(90.0)` and
`fromDegrees(45.0)`. The sweep's stillness rule and all ten tests stay.

## Checks

- `6-check-request-frames`: `message_incorrect` says "Re-read the frame sketch and
  the request tables in task 1." Question unchanged.
- `7-check-facing-angle`: "your FSM from task 6" becomes "task 5"; "setter table in
  task 5" becomes "task 3".

## Lesson 8 call sites that the `Rotation2d` switch breaks

Compile must stay green, so this work patches them minimally. The lesson 8 spec
owns the prose.

| File                                                  | Change                                                        |
|-------------------------------------------------------|---------------------------------------------------------------|
| `8-field-awareness/3-targeting/src/Targeting.kt`      | if the starter's given code passes degrees, wrap with `Rotation2d.fromDegrees` |
| `8-field-awareness/3-targeting/test/TargetingTest.kt` | lines 21, 31, 43, 53: `r.targetDirection` becomes `r.targetDirection.degrees` |
| `8-field-awareness/3-targeting/task.md`               | lines 26 and 29: "in degrees" becomes "a `Rotation2d`"       |

## Wiring

- `build.gradle.kts`: replace the six lesson 6 `src` lines and six `test` lines with
  the five new directories.
- `6-swerve-requests/lesson-info.yaml`: the seven directories in order.
- `CLAUDE.md` line 9 and `README.md` line 10: 63 tasks to 62, 41 programming to 40.
- `README.md` line 18: add "joystick mapping with deadbands" to the lesson 6 line.
- `course-info.yaml` line 18: "6. Swerve requests - CTRE swerve API, joystick
  mapping, and a drive-mode FSM."

## Verification

Each programming task: starter fails with `NotImplementedError`, reference solution
passes, restored starter fails. `./gradlew build` compiles; every failure is a
starter placeholder. Lesson 8 task 3 tests still pass with its reference solution.
