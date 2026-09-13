package course.l8t4

import frc.stubs.geometry.Rotation2d
import kotlin.test.Test
import kotlin.test.assertEquals

class SpeedsTest {
    private val tol = 1e-9

    @Test fun robot_facing_zero_passes_through() {
        val s = fieldToRobotSpeeds(1.0, 0.5, 0.2, Rotation2d.fromDegrees(0.0))
        assertEquals(1.0, s.vxMetersPerSecond, tol)
        assertEquals(0.5, s.vyMetersPerSecond, tol)
        assertEquals(0.2, s.omegaRadiansPerSecond, tol)
    }

    @Test fun robot_facing_ninety_rotates_field_x_into_robot_negative_y() {
        // Field +x at 1 m/s. Robot facing 90° (along field +y). In robot frame, field +x is to my right.
        // For the robot, +y is left. So this becomes vx = 0, vy = -1.
        val s = fieldToRobotSpeeds(1.0, 0.0, 0.0, Rotation2d.fromDegrees(90.0))
        assertEquals(0.0, s.vxMetersPerSecond, tol)
        assertEquals(-1.0, s.vyMetersPerSecond, tol)
    }

    @Test fun robot_facing_one_eighty_negates_translation() {
        val s = fieldToRobotSpeeds(1.0, 0.5, 0.0, Rotation2d.fromDegrees(180.0))
        assertEquals(-1.0, s.vxMetersPerSecond, tol)
        assertEquals(-0.5, s.vyMetersPerSecond, tol)
    }

    @Test fun omega_passes_through_unchanged() {
        val s = fieldToRobotSpeeds(0.0, 0.0, 1.5, Rotation2d.fromDegrees(45.0))
        assertEquals(1.5, s.omegaRadiansPerSecond, tol)
    }
}
