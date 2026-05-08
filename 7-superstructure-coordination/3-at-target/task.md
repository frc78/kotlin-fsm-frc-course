# `atTarget()` Rolls Up

In Lesson 3 your `Elevator` exposed `atTarget(): Boolean`. It was a useful
question: "is the elevator settled where I asked it to go?" Auto routines
checked it before progressing to the next step.

The same question is meaningful at the superstructure layer:

> Is the **whole robot** at its commanded state?

That answer is just the AND of the per-subsystem answers:

```kotlin
fun atTarget(): Boolean =
    elevator.atTarget() && arm.atTarget() && intake.modeReached()
```

Higher-level FSMs naturally compose this way. A robot is "at" `SCORE_L4` only
when *every* subsystem that contributes to that pose has settled.

## Why subsystems take time

The stubs simulate physical motion. Elevator takes 3 ticks to reach a new
target. Arm takes 2. Intake snaps immediately (it's just a roller mode). So
after commanding `SCORE_L4` from `STOWED`, you need at least 3 periodic ticks
before the elevator's `atTarget()` flips, then at most 2 more before arm's
does — so 5 ticks at most before `Superstructure.atTarget()` becomes true.

## Your task

Implement `atTarget()` in `src/Superstructure.kt`. The body is one line — the
AND of the three per-subsystem queries listed above.
