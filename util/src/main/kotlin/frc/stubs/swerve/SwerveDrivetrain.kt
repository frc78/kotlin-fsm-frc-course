package frc.stubs.swerve

import frc.stubs.geometry.Pose2d
import frc.stubs.geometry.Rotation2d
import frc.stubs.geometry.Translation2d

// Stand-in for CTRE's SwerveDrivetrain. Two jobs, as on a real robot:
//  1. Accept a SwerveRequest through setControl(). Tests read lastRequest.
//  2. Own the pose estimate. Real code reads getState().Pose; here it is
//     state.pose. Odometry runs inside the real drivetrain on every loop and
//     is not modeled here, so tests place the robot with resetPose().
//
// addVisionMeasurement(pose, timestampSeconds) blends the estimate toward the
// camera pose with a fixed weight. The real method uses the timestamp for
// latency compensation and has a three-argument overload that takes a
// Matrix<N3, N1> of standard deviations. This stub has neither.
class SwerveDrivetrain(initialPose: Pose2d = Pose2d()) {
    class SwerveDriveState(var pose: Pose2d)

    val state = SwerveDriveState(initialPose)

    var lastRequest: SwerveRequest = Idle
        private set

    var visionMeasurementsAdded: Int = 0
        private set

    fun setControl(request: SwerveRequest) {
        lastRequest = request
    }

    fun resetPose(pose: Pose2d) {
        state.pose = pose
    }

    fun addVisionMeasurement(pose: Pose2d, timestampSeconds: Double) {
        val weight = 0.1
        val current = state.pose
        val translation = Translation2d(
            current.x * (1 - weight) + pose.x * weight,
            current.y * (1 - weight) + pose.y * weight,
        )
        // ponytail: averages raw radians, wrong across the ±180° seam; tests stay away from it.
        val radians = current.rotation.radians * (1 - weight) + pose.rotation.radians * weight
        state.pose = Pose2d(translation, Rotation2d.fromRadians(radians))
        visionMeasurementsAdded++
    }
}
