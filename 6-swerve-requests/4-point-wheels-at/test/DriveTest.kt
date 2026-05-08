package course.l6t4

import frc.stubs.swerve.PointWheelsAt
import kotlin.test.Test
import kotlin.test.assertEquals

class DriveTest {
    @Test fun points_wheels_at_zero() {
        Drive.pointWheels(0.0)
        assertEquals(PointWheelsAt(moduleDirection = 0.0), Drive.drivetrain.lastRequest)
    }

    @Test fun points_wheels_at_45() {
        Drive.pointWheels(45.0)
        assertEquals(PointWheelsAt(moduleDirection = 45.0), Drive.drivetrain.lastRequest)
    }

    @Test fun points_wheels_at_negative_angle() {
        Drive.pointWheels(-90.0)
        assertEquals(PointWheelsAt(moduleDirection = -90.0), Drive.drivetrain.lastRequest)
    }
}
