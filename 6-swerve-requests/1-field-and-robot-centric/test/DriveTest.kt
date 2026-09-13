package course.l6t1

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
            "robotRelative = false should send a FieldCentric carrying vx, vy, omega as velocityX, velocityY, rotationalRate"
        )
    }

    @Test fun robot_centric_when_robot_relative() {
        Drive.teleopDrive(1.0, 0.5, 0.3, robotRelative = true)
        assertEquals(
            RobotCentric(velocityX = 1.0, velocityY = 0.5, rotationalRate = 0.3),
            Drive.drivetrain.lastRequest,
            "robotRelative = true should send a RobotCentric carrying vx, vy, omega as velocityX, velocityY, rotationalRate"
        )
    }

    @Test fun negative_values_pass_through() {
        Drive.teleopDrive(-1.5, -0.7, -1.0, robotRelative = false)
        assertEquals(
            FieldCentric(velocityX = -1.5, velocityY = -0.7, rotationalRate = -1.0),
            Drive.drivetrain.lastRequest,
            "negative vx, vy, omega should pass through unchanged"
        )
    }

    @Test fun zero_velocity_still_sends_a_request() {
        Drive.teleopDrive(0.0, 0.0, 0.0, robotRelative = true)
        assertEquals(
            RobotCentric(),
            Drive.drivetrain.lastRequest,
            "zero velocities should still send a RobotCentric request with every field 0.0"
        )
    }

    @Test fun toggle_changes_request_type() {
        Drive.teleopDrive(1.0, 0.0, 0.0, robotRelative = false)
        assertEquals(
            FieldCentric(velocityX = 1.0),
            Drive.drivetrain.lastRequest,
            "first call with robotRelative = false should send a FieldCentric"
        )
        Drive.teleopDrive(1.0, 0.0, 0.0, robotRelative = true)
        assertEquals(
            RobotCentric(velocityX = 1.0),
            Drive.drivetrain.lastRequest,
            "second call with robotRelative = true should replace it with a RobotCentric"
        )
    }
}
