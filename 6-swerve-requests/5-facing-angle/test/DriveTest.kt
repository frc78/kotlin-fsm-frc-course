package course.l6t5

import frc.stubs.swerve.FieldCentricFacingAngle
import kotlin.test.Test
import kotlin.test.assertEquals

class DriveTest {
    @Test fun aims_with_zero_velocity() {
        Drive.aimWhileDriving(0.0, 0.0, 90.0)
        assertEquals(
            FieldCentricFacingAngle(velocityX = 0.0, velocityY = 0.0, targetDirection = 90.0),
            Drive.drivetrain.lastRequest
        )
    }

    @Test fun aims_with_translation() {
        Drive.aimWhileDriving(1.5, -0.5, 180.0)
        assertEquals(
            FieldCentricFacingAngle(velocityX = 1.5, velocityY = -0.5, targetDirection = 180.0),
            Drive.drivetrain.lastRequest
        )
    }

    @Test fun changing_target_changes_request() {
        Drive.aimWhileDriving(1.0, 0.0, 0.0)
        assertEquals(
            FieldCentricFacingAngle(velocityX = 1.0, targetDirection = 0.0),
            Drive.drivetrain.lastRequest
        )
        Drive.aimWhileDriving(1.0, 0.0, 45.0)
        assertEquals(
            FieldCentricFacingAngle(velocityX = 1.0, targetDirection = 45.0),
            Drive.drivetrain.lastRequest
        )
    }
}
