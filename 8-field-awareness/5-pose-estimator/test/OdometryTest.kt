package course.l8t5

import frc.stubs.estimator.PoseEstimator
import kotlin.test.Test
import kotlin.test.assertEquals

class OdometryTest {
    private val tol = 1e-9

    @Test fun starts_at_origin() {
        val e = PoseEstimator()
        assertEquals(0.0, e.currentPose.x, tol)
        assertEquals(0.0, e.currentPose.y, tol)
    }

    @Test fun drive_forward_one_meter() {
        val e = PoseEstimator()
        applyOdometry(e, forwardMeters = 1.0, turnDegrees = 0.0)
        assertEquals(1.0, e.currentPose.x, tol)
        assertEquals(0.0, e.currentPose.y, tol)
        assertEquals(0.0, e.currentPose.rotation.degrees, tol)
    }

    @Test fun rotate_in_place() {
        val e = PoseEstimator()
        applyOdometry(e, forwardMeters = 0.0, turnDegrees = 90.0)
        assertEquals(0.0, e.currentPose.x, tol)
        assertEquals(0.0, e.currentPose.y, tol)
        assertEquals(90.0, e.currentPose.rotation.degrees, 1e-6)
    }

    @Test fun drive_forward_then_turn_then_forward() {
        val e = PoseEstimator()
        applyOdometry(e, 1.0, 0.0)        // -> (1, 0) at 0°
        applyOdometry(e, 0.0, 90.0)       // -> (1, 0) at 90°
        applyOdometry(e, 1.0, 0.0)        // -> (1, 1) at 90°
        assertEquals(1.0, e.currentPose.x, 1e-6)
        assertEquals(1.0, e.currentPose.y, 1e-6)
        assertEquals(90.0, e.currentPose.rotation.degrees, 1e-6)
    }
}
