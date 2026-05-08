package course.l6t1

import frc.stubs.swerve.FieldCentric
import kotlin.test.Test
import kotlin.test.assertEquals

class DriveTest {
    @Test fun applies_field_centric_with_vx_only() {
        Drive.teleopDrive(1.0, 0.0, 0.0)
        assertEquals(
            FieldCentric(velocityX = 1.0, velocityY = 0.0, rotationalRate = 0.0),
            Drive.drivetrain.lastRequest
        )
    }

    @Test fun applies_field_centric_with_all_axes() {
        Drive.teleopDrive(1.0, 0.5, 0.2)
        assertEquals(
            FieldCentric(velocityX = 1.0, velocityY = 0.5, rotationalRate = 0.2),
            Drive.drivetrain.lastRequest
        )
    }

    @Test fun applies_zero_velocity_field_centric() {
        Drive.teleopDrive(0.0, 0.0, 0.0)
        assertEquals(FieldCentric(), Drive.drivetrain.lastRequest)
    }

    @Test fun applies_negative_velocities() {
        Drive.teleopDrive(-1.5, -0.7, -1.0)
        assertEquals(
            FieldCentric(velocityX = -1.5, velocityY = -0.7, rotationalRate = -1.0),
            Drive.drivetrain.lastRequest
        )
    }
}
