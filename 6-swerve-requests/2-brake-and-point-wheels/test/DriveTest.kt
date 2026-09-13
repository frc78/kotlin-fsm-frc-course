package course.l6t2

import frc.stubs.geometry.Rotation2d
import frc.stubs.swerve.PointWheelsAt
import frc.stubs.swerve.SwerveDriveBrake
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DriveTest {
    @BeforeTest fun setUp() {
        Drive.reset()
    }

    @Test fun applies_brake() {
        Drive.applyBrake()
        assertEquals(
            SwerveDriveBrake,
            Drive.drivetrain.lastRequest,
            "applyBrake() should send the SwerveDriveBrake request, not a zero-velocity drive request",
        )
    }

    @Test fun points_wheels_at_zero() {
        Drive.pointWheels(0.0)
        assertEquals(
            PointWheelsAt(Rotation2d.fromDegrees(0.0)),
            Drive.drivetrain.lastRequest,
            "pointWheels(0.0) should send PointWheelsAt with moduleDirection = Rotation2d.fromDegrees(0.0)",
        )
    }

    @Test fun points_wheels_at_45() {
        Drive.pointWheels(45.0)
        assertEquals(
            PointWheelsAt(Rotation2d.fromDegrees(45.0)),
            Drive.drivetrain.lastRequest,
            "pointWheels(45.0) should send PointWheelsAt with moduleDirection = Rotation2d.fromDegrees(45.0)",
        )
    }

    @Test fun points_wheels_at_negative() {
        Drive.pointWheels(-90.0)
        assertEquals(
            PointWheelsAt(Rotation2d.fromDegrees(-90.0)),
            Drive.drivetrain.lastRequest,
            "pointWheels(-90.0) should pass a negative angle through unchanged",
        )
    }

    @Test fun brake_after_point_replaces_request() {
        Drive.pointWheels(45.0)
        Drive.applyBrake()
        assertEquals(
            SwerveDriveBrake,
            Drive.drivetrain.lastRequest,
            "applyBrake() after pointWheels() should replace the request with SwerveDriveBrake",
        )
    }
}
