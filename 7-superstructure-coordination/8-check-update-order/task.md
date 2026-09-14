# Check: Update Order

A teammate writes `StateMachineManager.teleopPeriodic()` in this order:

```kotlin
Climber.periodic()
Intake.periodic()
SuperStructure.periodic()
```

The robot is climbing. On tick N, `SuperStructure.periodic()` runs and, for
the first time, `SuperStructure.state == Pose.FULLY_CLIMBED` and
`SuperStructure.atPosition` is `true` after it returns.

On which tick does the climber leave `RETRACTED`?
