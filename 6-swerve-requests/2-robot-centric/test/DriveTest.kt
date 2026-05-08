package course.l6t2

import frc.stubs.swerve.FieldCentric
import frc.stubs.swerve.RobotCentric
import kotlin.test.Test
import kotlin.test.assertEquals

class DriveTest {
    @Test fun field_centric_when_not_robot_relative() {
        Drive.teleopDrive(1.0, 0.5, 0.0, robotRelative = false)
        assertEquals(
            FieldCentric(velocityX = 1.0, velocityY = 0.5, rotationalRate = 0.0),
            Drive.drivetrain.lastRequest
        )
    }

    @Test fun robot_centric_when_robot_relative() {
        Drive.teleopDrive(1.0, 0.5, 0.0, robotRelative = true)
        assertEquals(
            RobotCentric(velocityX = 1.0, velocityY = 0.5, rotationalRate = 0.0),
            Drive.drivetrain.lastRequest
        )
    }

    @Test fun toggle_changes_request_type() {
        Drive.teleopDrive(1.0, 0.0, 0.0, robotRelative = false)
        assertEquals(
            FieldCentric(velocityX = 1.0),
            Drive.drivetrain.lastRequest
        )
        Drive.teleopDrive(1.0, 0.0, 0.0, robotRelative = true)
        assertEquals(
            RobotCentric(velocityX = 1.0),
            Drive.drivetrain.lastRequest
        )
    }
}
