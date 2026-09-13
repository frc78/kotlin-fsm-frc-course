package course.l8t4

import frc.stubs.geometry.Pose2d
import frc.stubs.geometry.Rotation2d
import frc.stubs.swerve.SwerveDrivetrain
import kotlin.test.Test
import kotlin.test.assertEquals

class OdometryTest {
    private val tol = 1e-9
    private val degTol = 1e-6

    @Test fun drive_forward_one_meter() {
        val p = poseAfterOdometry(Pose2d(), forwardMeters = 1.0, turnDegrees = 0.0)
        assertEquals(1.0, p.x, tol, "1 m forward at 0° should move +X by 1")
        assertEquals(0.0, p.y, tol, "1 m forward at 0° should not move Y")
        assertEquals(0.0, p.rotation.degrees, degTol, "no turn should keep 0°")
    }

    @Test fun rotate_in_place() {
        val p = poseAfterOdometry(Pose2d(), forwardMeters = 0.0, turnDegrees = 90.0)
        assertEquals(0.0, p.x, tol, "a pure turn should not move X")
        assertEquals(0.0, p.y, tol, "a pure turn should not move Y")
        assertEquals(90.0, p.rotation.degrees, degTol, "turning 90° from 0° should give 90°")
    }

    @Test fun forward_then_turn_then_forward() {
        var p = Pose2d()
        p = poseAfterOdometry(p, 1.0, 0.0)   // (1, 0) at 0°
        p = poseAfterOdometry(p, 0.0, 90.0)  // (1, 0) at 90°
        p = poseAfterOdometry(p, 1.0, 0.0)   // (1, 1) at 90°
        assertEquals(1.0, p.x, degTol, "after the trace X should be 1")
        assertEquals(1.0, p.y, degTol, "forward at 90° is field +Y, so Y should be 1")
        assertEquals(90.0, p.rotation.degrees, degTol, "heading should stay 90°")
    }

    @Test fun forward_at_heading_uses_robot_frame() {
        val start = Pose2d(2.0, 3.0, Rotation2d.fromDegrees(180.0))
        val p = poseAfterOdometry(start, forwardMeters = 1.0, turnDegrees = 0.0)
        assertEquals(1.0, p.x, degTol, "forward at 180° is field -X, so X should drop from 2 to 1")
        assertEquals(3.0, p.y, degTol, "forward at 180° should not change Y")
        assertEquals(180.0, p.rotation.degrees, degTol, "heading should stay 180°")
    }

    @Test fun drivetrain_reset_then_read_state() {
        val d = SwerveDrivetrain()
        d.resetPose(Pose2d(4.0, 3.0, Rotation2d.fromDegrees(90.0)))
        assertEquals(
            Pose2d(4.0, 3.0, Rotation2d.fromDegrees(90.0)),
            d.state.pose,
            "state.pose should return the pose given to resetPose",
        )
    }
}
