package course.l6t4

import frc.stubs.swerve.PointWheelsAt
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DriveTest {
    @BeforeTest fun setUp() {
        Drive.reset()
    }

    @Test fun points_wheels_at_zero() {
        Drive.pointWheels(0.0)
        assertEquals(
            PointWheelsAt(moduleDirection = 0.0),
            Drive.drivetrain.lastRequest,
            "pointWheels(0.0) should send a PointWheelsAt with moduleDirection = 0.0"
        )
    }

    @Test fun points_wheels_at_45() {
        Drive.pointWheels(45.0)
        assertEquals(
            PointWheelsAt(moduleDirection = 45.0),
            Drive.drivetrain.lastRequest,
            "pointWheels(45.0) should send a PointWheelsAt with moduleDirection = 45.0"
        )
    }

    @Test fun points_wheels_at_negative_angle() {
        Drive.pointWheels(-90.0)
        assertEquals(
            PointWheelsAt(moduleDirection = -90.0),
            Drive.drivetrain.lastRequest,
            "pointWheels(-90.0) should pass a negative direction through unchanged"
        )
    }
}
