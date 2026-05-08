package course.l8t6

import frc.stubs.estimator.PoseEstimator
import frc.stubs.geometry.Pose2d
import frc.stubs.geometry.Rotation2d
import frc.stubs.vision.VisionMeasurement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VisionTest {

    private fun freshEstimator(): PoseEstimator =
        PoseEstimator(Pose2d(1.0, 1.0, Rotation2d()))

    @Test fun applies_good_measurement_close_to_current_pose() {
        val e = freshEstimator()
        val applied = integrateVisionMeasurement(
            estimator = e,
            measurement = VisionMeasurement(
                pose = Pose2d(1.1, 1.0, Rotation2d()),
                timestampSeconds = 9.95,
                translationStdDev = 0.2,
            ),
            currentTimestampSeconds = 10.0,
        )
        assertTrue(applied)
        // Pose should have moved toward the measurement.
        assertTrue(e.currentPose.x > 1.0)
        assertTrue(e.currentPose.x < 1.1)
    }

    @Test fun rejects_stale_measurement() {
        val e = freshEstimator()
        val applied = integrateVisionMeasurement(
            estimator = e,
            measurement = VisionMeasurement(
                pose = Pose2d(1.1, 1.0, Rotation2d()),
                timestampSeconds = 9.0,           // 1.0 s old; threshold is 0.5
                translationStdDev = 0.2,
            ),
            currentTimestampSeconds = 10.0,
        )
        assertFalse(applied)
        assertEquals(1.0, e.currentPose.x, 1e-9)
    }

    @Test fun rejects_high_stddev() {
        val e = freshEstimator()
        val applied = integrateVisionMeasurement(
            estimator = e,
            measurement = VisionMeasurement(
                pose = Pose2d(1.1, 1.0, Rotation2d()),
                timestampSeconds = 10.0,
                translationStdDev = 1.5,           // > 1.0 threshold
            ),
            currentTimestampSeconds = 10.0,
        )
        assertFalse(applied)
        assertEquals(1.0, e.currentPose.x, 1e-9)
    }

    @Test fun rejects_implausible_jump() {
        val e = freshEstimator()
        val applied = integrateVisionMeasurement(
            estimator = e,
            measurement = VisionMeasurement(
                pose = Pose2d(5.0, 5.0, Rotation2d()),  // ~5.66 m away from (1,1)
                timestampSeconds = 10.0,
                translationStdDev = 0.2,
            ),
            currentTimestampSeconds = 10.0,
        )
        assertFalse(applied)
        assertEquals(1.0, e.currentPose.x, 1e-9)
    }

    @Test fun applies_when_all_gates_pass() {
        val e = freshEstimator()
        repeat(5) {
            integrateVisionMeasurement(
                estimator = e,
                measurement = VisionMeasurement(
                    pose = Pose2d(2.0, 1.0, Rotation2d()),
                    timestampSeconds = 10.0,
                    translationStdDev = 0.2,
                ),
                currentTimestampSeconds = 10.0,
            )
        }
        // After 5 weighted blends, pose should be drifting toward (2, 1).
        assertTrue(e.currentPose.x > 1.2)
    }
}
