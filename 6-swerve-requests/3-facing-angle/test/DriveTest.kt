package course.l6t3

import frc.stubs.geometry.Rotation2d
import frc.stubs.swerve.FieldCentricFacingAngle
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DriveTest {
    @BeforeTest fun setUp() {
        Drive.reset()
    }

    @Test fun aims_with_zero_velocity() {
        Drive.aimWhileDriving(0.0, 0.0, 90.0)
        assertEquals(
            FieldCentricFacingAngle(velocityX = 0.0, velocityY = 0.0, targetDirection = Rotation2d.fromDegrees(90.0)),
            Drive.drivetrain.lastRequest,
            "aimWhileDriving(0.0, 0.0, 90.0) should send a FieldCentricFacingAngle with targetDirection = 90° and zero translation"
        )
    }

    @Test fun aims_with_translation() {
        Drive.aimWhileDriving(1.5, -0.5, 180.0)
        assertEquals(
            FieldCentricFacingAngle(velocityX = 1.5, velocityY = -0.5, targetDirection = Rotation2d.fromDegrees(180.0)),
            Drive.drivetrain.lastRequest,
            "aimWhileDriving(1.5, -0.5, 180.0) should carry vx and vy as velocityX and velocityY, and targetDegrees as a Rotation2d in targetDirection"
        )
    }

    @Test fun changing_target_changes_request() {
        Drive.aimWhileDriving(1.0, 0.0, 0.0)
        assertEquals(
            FieldCentricFacingAngle(velocityX = 1.0, targetDirection = Rotation2d.fromDegrees(0.0)),
            Drive.drivetrain.lastRequest,
            "first call should send targetDirection = 0°"
        )
        Drive.aimWhileDriving(1.0, 0.0, 45.0)
        assertEquals(
            FieldCentricFacingAngle(velocityX = 1.0, targetDirection = Rotation2d.fromDegrees(45.0)),
            Drive.drivetrain.lastRequest,
            "second call should send a new request with targetDirection = 45°"
        )
    }
}
