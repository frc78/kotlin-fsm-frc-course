package course.l6t3

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
            "applyBrake() should send the SwerveDriveBrake request, not a zero-velocity drive request"
        )
    }
}
