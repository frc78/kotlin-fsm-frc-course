package course.l6t4

import frc.stubs.swerve.FieldCentric
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DriveTest {
    private val tol = 1e-9

    @BeforeTest fun setUp() {
        Drive.reset()
    }

    private fun request(): FieldCentric =
        assertIs<FieldCentric>(Drive.drivetrain.lastRequest, "drive() must send a FieldCentric request")

    private fun assertVelocities(r: FieldCentric, vx: Double, vy: Double, omega: Double) {
        assertEquals(vx, r.velocityX, tol, "velocityX should be -leftY * maxSpeedMetersPerSecond")
        assertEquals(vy, r.velocityY, tol, "velocityY should be -leftX * maxSpeedMetersPerSecond")
        assertEquals(omega, r.rotationalRate, tol, "rotationalRate should be -rightX * maxTurnRadiansPerSecond")
    }

    private fun assertDeadbands(r: FieldCentric) {
        assertEquals(0.45, r.deadband, tol, "deadband should be ten percent of maxSpeedMetersPerSecond")
        assertEquals(0.6, r.rotationalDeadband, tol, "rotationalDeadband should be ten percent of maxTurnRadiansPerSecond")
    }

    @Test fun full_forward_is_plus_x() {
        Drive.drive(0.0, -1.0, 0.0)
        assertVelocities(request(), 4.5, 0.0, 0.0)
    }

    @Test fun left_is_plus_y() {
        Drive.drive(-0.5, 0.0, 0.0)
        assertVelocities(request(), 0.0, 2.25, 0.0)
    }

    @Test fun right_stick_right_is_clockwise() {
        Drive.drive(0.0, 0.0, 0.5)
        assertVelocities(request(), 0.0, 0.0, -3.0)
    }

    @Test fun stick_back_is_minus_x() {
        Drive.drive(0.0, 0.2, 0.0)
        assertVelocities(request(), -0.9, 0.0, 0.0)
    }

    @Test fun all_axes_together() {
        Drive.drive(1.0, -1.0, -1.0)
        assertVelocities(request(), 4.5, -4.5, 6.0)
    }

    @Test fun deadbands_are_ten_percent_of_max() {
        Drive.drive(0.3, -0.7, 0.1)
        assertDeadbands(request())
    }

    @Test fun centered_sticks_send_zero_velocities() {
        Drive.drive(0.0, 0.0, 0.0)
        val r = request()
        assertVelocities(r, 0.0, 0.0, 0.0)
        assertDeadbands(r)
    }
}
