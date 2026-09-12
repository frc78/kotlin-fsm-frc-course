package course.l1t3

import kotlin.test.Test
import kotlin.test.assertEquals

class ConversionsTest {
    private val eps = 1e-9

    @Test fun full_fraction_is_twelve_volts() {
        assertEquals(12.0, fractionToVolts(1.0), eps)
    }

    @Test fun half_fraction_is_six_volts() {
        assertEquals(6.0, fractionToVolts(0.5), eps)
    }

    @Test fun negative_fraction_is_negative_volts() {
        assertEquals(-3.0, fractionToVolts(-0.25), eps)
    }

    @Test fun four_to_one_ratio_divides_by_four() {
        assertEquals(2.5, mechanismRotations(10.0, 4.0), eps)
    }

    @Test fun one_to_one_ratio_changes_nothing() {
        assertEquals(7.25, mechanismRotations(7.25, 1.0), eps)
    }

    @Test fun fifty_one_to_one_ratio() {
        assertEquals(1.0, mechanismRotations(51.0, 51.0), eps)
    }

    @Test fun one_rotation_is_360_degrees() {
        assertEquals(360.0, rotationsToDegrees(1.0), eps)
    }

    @Test fun quarter_rotation_is_90_degrees() {
        assertEquals(90.0, rotationsToDegrees(0.25), eps)
    }

    @Test fun negative_rotations_give_negative_degrees() {
        assertEquals(-180.0, rotationsToDegrees(-0.5), eps)
    }
}
