package course.l8t6

import frc.stubs.estimator.PoseEstimator
import frc.stubs.vision.VisionMeasurement

fun integrateVisionMeasurement(
    estimator: PoseEstimator,
    measurement: VisionMeasurement,
    currentTimestampSeconds: Double,
): Boolean {
    // TODO: gate on three criteria. Return false WITHOUT updating if any fails.
    //
    //   1. Stale: currentTimestampSeconds - measurement.timestampSeconds > 0.5
    //   2. Imprecise: measurement.translationStdDev > 1.0
    //   3. Implausible: estimator.currentPose.translation
    //          .getDistance(measurement.pose.translation) > 1.5
    //
    // If all pass, call estimator.addVisionMeasurement(measurement.pose) and return true.
    TODO()
}
