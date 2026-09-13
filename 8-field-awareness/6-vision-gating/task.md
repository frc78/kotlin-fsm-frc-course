# Vision Measurement Integration with Gating

The other half of pose estimation is vision. A Limelight or PhotonVision
camera sees AprilTags and returns a pose estimate: "the robot is at `(x, y)`
facing this angle." Feeding these into the estimator corrects odometry drift.

Vision readings have three problems:

- **Stale.** Camera processing takes time. A reading can be 150 ms old.
- **Imprecise.** A tag seen at an angle from 4 meters gives a much less
  certain position than a tag seen head-on from 1 meter. Vision pipelines
  report this as a standard deviation (`stdDev`). A high value means low
  confidence.
- **Implausible.** A bad detection (wrong tag ID, glare, a partly hidden tag)
  can give a position nowhere near the robot.

If you trust every measurement, the pose estimate jumps around the field.
The fix is to **gate** each measurement before you apply it.

## Three gates

Reject the measurement and return `false` if any row matches. The comparisons
are strict: exactly 0.5 s, exactly 1.0, or exactly 1.5 m passes.

| Gate         | Reject when                                                               |
|--------------|---------------------------------------------------------------------------|
| Stale        | the measurement is more than **0.5 seconds** older than now               |
| Imprecise    | `translationStdDev` is greater than **1.0**                               |
| Implausible  | the measured position is more than **1.5 meters** from the current estimate |

The implausible gate uses straight-line distance, the same as `getDistance`
in task 1.

Inputs:

| Value                            | Meaning                                  |
|----------------------------------|------------------------------------------|
| `currentTimestampSeconds`        | the time now, passed in                  |
| `measurement.timestampSeconds`   | when the camera took the measurement     |
| `measurement.translationStdDev`  | the pipeline's reported uncertainty      |
| `measurement.pose`               | the candidate pose                       |
| `estimator.currentPose`          | the estimator's belief right now         |

If all three gates pass, call `estimator.addVisionMeasurement(pose)` and
return `true`. The stub method has a second parameter, `weight`, with a
default value. Leave it at the default.

## Your task

Implement `integrateVisionMeasurement(estimator, measurement, currentTimestampSeconds)`
in `src/Vision.kt`. It returns `true` if it applied the measurement and
`false` if it rejected it.

Real WPILib `addVisionMeasurement` takes the timestamp and the standard
deviations too. The estimator uses them to decide how much to trust each
reading. This stub blends with a fixed weight, so gating is the only control
you have.
