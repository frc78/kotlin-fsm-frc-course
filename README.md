# Kotlin FSMs for FRC Subsystems

An EduTools course that teaches FRC students how to write finite state
machines for robotic subsystems in Kotlin, using lightweight stubs that
mimic WPILib 2026 and Phoenix6 (TalonFX, CANrange, swerve requests). What
you learn here transfers directly to a real robot project.

## What's inside

8 lessons, 62 tasks (40 programming exercises, 18 comprehension checks,
4 theory pages):

1. **Kotlin from zero** — values and types, functions, decisions, objects, loops and lists, `enum`, `when`, and sealed classes.
2. **Building an FSM** — the two-method `stateActions()` / `stateTransitions()` pattern, entry side effects, guards, and timers.
3. **Applied subsystems** — Intake, Elevator, Shooter (real subsystem shapes).
4. **Testing** — pure-function FSM tests + dependency injection with fake hardware.
5. **Motor configuration** — TalonFX setup: instantiation, neutral mode + inversion, current limits, and Slot0 PID gains.
6. **Swerve drive requests** — Phoenix6 `SwerveRequest` API (`FieldCentric`, `RobotCentric`, `SwerveDriveBrake`, `PointWheelsAt`, `FieldCentricFacingAngle`) plus a drive-mode FSM.
7. **Superstructure coordination** — composing subsystem FSMs into a single robot FSM with sequencing guards.
8. **Field awareness** — `Translation2d` / `Rotation2d` / `Pose2d` math, `ChassisSpeeds`, pose estimator + vision-measurement gating.

## Setup

1. Install **IntelliJ IDEA** (Community is fine).
2. Install **JDK 17 or newer** (IntelliJ can download one for you on first
   import). Any modern JDK works — the build runs Gradle 9.5 and
   auto-downloads its own JDK 17 compile toolchain.
3. Install the **EduTools** plugin: `Settings → Plugins → Marketplace`, search "EduTools," install, restart.
4. Clone this repo:
   ```
   git clone git@github.com:frc78/kotlin-fsm-frc-course.git
   ```
5. In IntelliJ, `File → Open` the `kotlin-fsm-frc-course` folder. Accept the Gradle import prompt.

The Course view appears in the left panel, listing all lessons and tasks.

## Working a task

1. Click a task in the Course view. The middle panel shows the task description; the source file opens with `TODO()` placeholders.
2. Replace each `TODO()` with your implementation.
3. Click the green **Check** button (toolbar). EduTools runs the hidden test file and reports pass / fail.

If a task fails, the test output explains why. You can also run tests
directly via Gradle:

```
./gradlew test --tests "course.l3t1.IntakeTest"
```

(Lesson 3, task 1 = `course.l3t1`.)

## Updating

```
git pull
```

Your local progress lives in each task's `task-info.yaml` — pulling won't
clobber it.

## Troubleshooting

- **Build fails with only a version number as the error (e.g. `What went
  wrong: 25.0.4`):** your Gradle JVM is older than this course's Gradle 9.5
  wrapper supports, or a stale Gradle daemon is running. Set `Settings →
  Build Tools → Gradle → Gradle JVM` to any JDK 17–25 and reload.
- **Course view is empty:** confirm the EduTools plugin is installed and
  that you opened the *root* folder (the one containing `course-info.yaml`),
  not a sub-lesson.
- **JDK errors:** `Settings → Build Tools → Gradle → Gradle JVM` should be
  a JDK 17 or newer.

## What's NOT included

- Real WPILib / Phoenix6 dependencies — the course uses lightweight stubs
  with the same API surface so tasks compile fast and don't need the FRC
  native toolchain.
- The command-based paradigm (`SubsystemBase`, `Command`, scheduler) — the
  course teaches the team's preferred direct two-method FSM style.
- AdvantageKit logging, simulation visualization, SysId routines — out of
  scope for this course.

## License

Internal team course material. Distribution restrictions follow team policy.
