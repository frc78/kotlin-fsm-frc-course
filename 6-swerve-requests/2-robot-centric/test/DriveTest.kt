package course.l6t2

import frc.stubs.swerve.FieldCentric
import frc.stubs.swerve.RobotCentric
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DriveTest {
    @BeforeTest fun setUp() {
        Drive.reset()
    }

    @Test fun field_centric_when_not_robot_relative() {
        Drive.teleopDrive(1.0, 0.5, 0.3, robotRelative = false)
        assertEquals(
            FieldCentric(velocityX = 1.0, velocityY = 0.5, rotationalRate = 0.3),
            Drive.drivetrain.lastRequest,
            "teleopDrive(1.0, 0.5, 0.3, robotRelative = false) should send a FieldCentric request carrying all three values (vx, vy, and omega)"
        )
    }

    @Test fun robot_centric_when_robot_relative() {
        Drive.teleopDrive(1.0, 0.5, 0.3, robotRelative = true)
        assertEquals(
            RobotCentric(velocityX = 1.0, velocityY = 0.5, rotationalRate = 0.3),
            Drive.drivetrain.lastRequest,
            "teleopDrive(1.0, 0.5, 0.3, robotRelative = true) should send a RobotCentric request carrying all three values (vx, vy, and omega)"
        )
    }

    @Test fun toggle_changes_request_type() {
        Drive.teleopDrive(1.0, 0.0, 0.0, robotRelative = false)
        assertEquals(
            FieldCentric(velocityX = 1.0),
            Drive.drivetrain.lastRequest,
            "with robotRelative = false the request type must be FieldCentric"
        )
        Drive.teleopDrive(1.0, 0.0, 0.0, robotRelative = true)
        assertEquals(
            RobotCentric(velocityX = 1.0),
            Drive.drivetrain.lastRequest,
            "flipping robotRelative to true on the next call must switch the request type to RobotCentric"
        )
    }
}
