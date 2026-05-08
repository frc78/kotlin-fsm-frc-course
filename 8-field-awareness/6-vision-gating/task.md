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

This task implements three rejection criteria. If a measurement fails *any*
of them, drop it on the floor:

| Gate           | Reject when…                                                     |
|----------------|------------------------------------------------------------------|
| Stale          | `currentTimestampSeconds - measurement.timestampSeconds > 0.5`   |
| Imprecise      | `measurement.translationStdDev > 1.0`                            |
| Implausible    | distance from current estimate to measurement pose > 1.5 m       |

If all three pass, call
`estimator.addVisionMeasurement(measurement.pose)` and return `true`.
Otherwise return `false`.

## Your task

Implement `integrateVisionMeasurement(estimator, measurement, currentTimestampSeconds)`.
Returns `true` if applied, `false` if rejected.

(In real WPILib, `addVisionMeasurement` takes a timestamp and stddev tuple
which the Kalman filter uses to weight the update. The stub here uses a
fixed weight; gating is the only knob you have.)
