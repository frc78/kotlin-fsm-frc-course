package course.l8t5

import frc.stubs.geometry.Pose2d
import frc.stubs.geometry.Rotation2d
import frc.stubs.vision.VisionMeasurement
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class VisionTest {
    private val tol = 1e-9
    private var n0 = 0

    @BeforeTest fun setUp() {
        Vision.reset()
        n0 = Vision.drivetrain.visionMeasurementsAdded   // the object keeps one drivetrain; count deltas
        Vision.drivetrain.resetPose(Pose2d(1.0, 1.0, Rotation2d()))
        Vision.nowSeconds = 10.0
    }

    private fun m(x: Double, y: Double, ts: Double, std: Double) =
        VisionMeasurement(Pose2d(x, y, Rotation2d()), ts, translationStdDev = std)

    private val n get() = Vision.drivetrain.visionMeasurementsAdded - n0
    private val x get() = Vision.drivetrain.state.pose.x

    @Test fun starts_with_no_target() {
        assertEquals(Vision.State.NO_TARGET, Vision.state, "after reset() the state is NO_TARGET")
        assertEquals(0, n, "nothing is applied before the first tick")
        assertEquals(1.0, x, tol, "the pose set in setUp is untouched")
    }

    @Test fun no_measurement_stays_no_target() {
        Vision.latestMeasurement = null
        Vision.periodic()
        assertEquals(Vision.State.NO_TARGET, Vision.state, "row 1: a null measurement means NO_TARGET")
        assertEquals(0, n, "NO_TARGET applies nothing")
        assertEquals(1.0, x, tol, "NO_TARGET leaves the pose alone")
    }

    @Test fun good_measurement_is_tracked() {
        Vision.latestMeasurement = m(1.1, 1.0, 9.95, 0.2)
        Vision.periodic()
        assertEquals(Vision.State.TRACKING, Vision.state, "row 6: every gate passes, so TRACKING")
        assertEquals(1, n, "TRACKING calls addVisionMeasurement once")
        assertTrue(x > 1.0 && x < 1.1, "the pose blends toward the measurement, x was $x")
        assertEquals(9.95, Vision.lastAppliedTimestampSeconds, tol, "TRACKING records the applied timestamp")
    }

    @Test fun stale_measurement_is_rejected() {
        Vision.latestMeasurement = m(1.1, 1.0, 9.0, 0.2)
        Vision.periodic()
        assertEquals(Vision.State.REJECTING, Vision.state, "stale gate: 1.0 s old is more than 0.5 s")
        assertEquals(0, n, "REJECTING applies nothing")
        assertEquals(1.0, x, tol, "REJECTING leaves the pose alone")
    }

    @Test fun imprecise_measurement_is_rejected() {
        Vision.latestMeasurement = m(1.1, 1.0, 10.0, 1.5)
        Vision.periodic()
        assertEquals(Vision.State.REJECTING, Vision.state, "imprecise gate: stdDev 1.5 is more than 1.0")
        assertEquals(0, n, "REJECTING applies nothing")
        assertEquals(1.0, x, tol, "REJECTING leaves the pose alone")
    }

    @Test fun implausible_jump_is_rejected() {
        Vision.latestMeasurement = m(5.0, 5.0, 10.0, 0.2)
        Vision.periodic()
        assertEquals(Vision.State.REJECTING, Vision.state, "implausible gate: (5,5) is 5.66 m from (1,1)")
        assertEquals(0, n, "REJECTING applies nothing")
        assertEquals(1.0, x, tol, "REJECTING leaves the pose alone")
    }

    @Test fun diagonal_jump_just_over_threshold() {
        // (2.0, 2.2) is 1.562 m from (1, 1). |dx| = 1.0 and max(|dx|,|dy|) = 1.2 would wrongly pass.
        Vision.latestMeasurement = m(2.0, 2.2, 10.0, 0.2)
        Vision.periodic()
        assertEquals(Vision.State.REJECTING, Vision.state, "implausible gate uses straight-line distance: 1.562 m is more than 1.5 m")
        assertEquals(0, n, "REJECTING applies nothing")
        assertEquals(1.0, x, tol, "REJECTING leaves the pose alone")
    }

    @Test fun boundary_values_pass() {
        // exactly 0.5 s old, stdDev exactly 1.0, exactly 1.5 m away: all strict comparisons pass.
        Vision.latestMeasurement = m(2.5, 1.0, 9.5, 1.0)
        Vision.periodic()
        assertEquals(Vision.State.TRACKING, Vision.state, "gates are strict: exactly 0.5 s, 1.0, and 1.5 m all pass")
        assertEquals(1, n, "TRACKING calls addVisionMeasurement once")
        assertTrue(x > 1.0, "the pose blends toward the measurement, x was $x")
    }

    @Test fun same_frame_is_applied_once() {
        Vision.latestMeasurement = m(1.1, 1.0, 9.95, 0.2)
        Vision.periodic()
        assertEquals(Vision.State.TRACKING, Vision.state, "first tick: a new frame is TRACKING")
        Vision.periodic()
        assertEquals(Vision.State.NO_TARGET, Vision.state, "row 2: the same timestamp was already applied, so NO_TARGET")
        assertEquals(1, n, "a frame is applied once, not on every tick")
    }

    @Test fun new_frame_after_reject_is_tracked() {
        Vision.latestMeasurement = m(5.0, 5.0, 10.0, 0.2)
        Vision.periodic()
        assertEquals(Vision.State.REJECTING, Vision.state, "the bad frame is REJECTING")
        Vision.latestMeasurement = m(1.1, 1.0, 9.98, 0.2)
        Vision.periodic()
        assertEquals(Vision.State.TRACKING, Vision.state, "rows are re-evaluated from the top: a good frame after a reject is TRACKING")
        assertEquals(1, n, "only the good frame is applied")
    }

    @Test fun losing_the_tag_returns_to_no_target() {
        Vision.latestMeasurement = m(1.1, 1.0, 9.95, 0.2)
        Vision.periodic()
        assertEquals(Vision.State.TRACKING, Vision.state, "a good frame is TRACKING")
        Vision.latestMeasurement = null
        Vision.periodic()
        assertEquals(Vision.State.NO_TARGET, Vision.state, "row 1: no tag in view means NO_TARGET")
        assertEquals(1, n, "losing the tag applies nothing more")
    }

    @Test fun reset_clears_state() {
        Vision.latestMeasurement = m(1.1, 1.0, 9.95, 0.2)
        Vision.periodic()
        Vision.reset()
        assertEquals(Vision.State.NO_TARGET, Vision.state, "reset() returns to NO_TARGET")
        assertNull(Vision.latestMeasurement, "reset() clears latestMeasurement")
        assertEquals(0.0, Vision.nowSeconds, tol, "reset() zeros the clock")
        assertEquals(Double.NEGATIVE_INFINITY, Vision.lastAppliedTimestampSeconds, "reset() forgets the last applied frame")
        assertEquals(0.0, x, tol, "reset() puts the drivetrain pose back at the origin")
        assertEquals(0.0, Vision.drivetrain.state.pose.y, tol, "reset() puts the drivetrain pose back at the origin")
    }
}
