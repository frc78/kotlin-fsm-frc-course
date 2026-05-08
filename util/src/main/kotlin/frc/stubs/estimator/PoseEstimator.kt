package frc.stubs.estimator

import frc.stubs.geometry.Pose2d
import frc.stubs.geometry.Rotation2d
import frc.stubs.geometry.Translation2d

// Lightweight stand-in for WPILib's SwerveDrivePoseEstimator. Real WPILib runs
// a Kalman filter that fuses odometry + vision with a buffer for latency
// compensation. This stub does a simple weighted blend — enough to teach the
// concepts of "current pose," "odometry update," and "vision merge."

class PoseEstimator(initialPose: Pose2d = Pose2d()) {
    var currentPose: Pose2d = initialPose
        private set

    fun resetPose(pose: Pose2d) {
        currentPose = pose
    }

    // translationDelta is in the robot's body frame ("forward" relative to
    // current heading). The estimator rotates it into the field frame before
    // adding.
    fun updateWithOdometry(translationDelta: Translation2d, rotationDelta: Rotation2d) {
        val fieldDelta = translationDelta.rotateBy(currentPose.rotation)
        currentPose = Pose2d(
            currentPose.translation + fieldDelta,
            currentPose.rotation + rotationDelta,
        )
    }

    fun addVisionMeasurement(visionPose: Pose2d, weight: Double = 0.1) {
        val newTranslation = Translation2d(
            currentPose.x * (1 - weight) + visionPose.x * weight,
            currentPose.y * (1 - weight) + visionPose.y * weight,
        )
        val newRotationRadians =
            currentPose.rotation.radians * (1 - weight) +
                visionPose.rotation.radians * weight
        currentPose = Pose2d(newTranslation, Rotation2d.fromRadians(newRotationRadians))
    }
}
