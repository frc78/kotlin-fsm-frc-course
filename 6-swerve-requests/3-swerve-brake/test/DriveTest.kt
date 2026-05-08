package course.l6t3

import frc.stubs.swerve.SwerveDriveBrake
import kotlin.test.Test
import kotlin.test.assertEquals

class DriveTest {
    @Test fun applies_brake() {
        Drive.applyBrake()
        assertEquals(SwerveDriveBrake, Drive.drivetrain.lastRequest)
    }

    @Test fun brake_is_a_singleton_object() {
        // Sanity check: SwerveDriveBrake is a `data object`, so referencing it
        // twice yields the same instance. (Not strictly necessary for the
        // student to grasp — but worth seeing in a test.)
        Drive.applyBrake()
        val first = Drive.drivetrain.lastRequest
        Drive.applyBrake()
        val second = Drive.drivetrain.lastRequest
        assertEquals(first, second)
    }
}
