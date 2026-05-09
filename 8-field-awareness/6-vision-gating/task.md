# Vision Measurement Integration with Gating

The other half of pose estimation: vision. AprilTag detections from a
Limelight or PhotonVision return a pose estimate — "based on the tags I see,
the robot is at `(x, y)` facing `θ`." Feeding these into the estimator
corrects odometry drift over time.

But vision readings are messy:

- **Stale.** Camera processing introduces latency. A reading might be from
  150 ms ago.
- **Imprecise.** A glancing tag at 4 meters has a much wider error ellipse
  than a head-on tag at 1 meter. Vision pipelines report `stdDev`s — high
  values mean low confidence.
- **Implausible.** A bad detection (wrong tag ID, glare, partial occlusion)
  can produce a measurement that's nowhere near where the robot actually is.

Blindly trusting every measurement teleports your pose estimate around. The
fix: **gate** measurements before applying them.

## Three gates

This task implements three rejection criteria. If a measurement fails
*any* of them, drop it on the floor and return `false`:

| Gate         | Reject when…                                                       |
|--------------|--------------------------------------------------------------------|
| Stale        | the measurement is more than **0.5 seconds** older than now        |
| Imprecise    | the measurement's `translationStdDev` exceeds **1.0**              |
| Implausible  | the measurement's pose is more than **1.5 meters** from the current estimate |

Inputs you have available:

- `currentTimestampSeconds: Double` — the parameter passed in.
- `measurement.timestampSeconds: Double` — when the measurement was taken.
- `measurement.translationStdDev: Double` — the vision pipeline's
  reported uncertainty.
- `measurement.pose: Pose2d` — the candidate pose.
- `estimator.currentPose: Pose2d` — the estimator's belief right now.

If all three gates pass, hand the candidate pose to the estimator's
`addVisionMeasurement(pose: Pose2d)` method and return `true`.

## Your task

Implement `integrateVisionMeasurement(estimator, measurement, currentTimestampSeconds)`.
Returns `true` if applied, `false` if rejected.

(In real WPILib, `addVisionMeasurement` takes a timestamp and stddev tuple
which the Kalman filter uses to weight the update. The stub here uses a
fixed weight; gating is the only knob you have.)
