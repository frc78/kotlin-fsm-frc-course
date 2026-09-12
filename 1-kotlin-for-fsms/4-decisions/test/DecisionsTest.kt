package course.l1t4

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DecisionsTest {
    @Test fun at_target_when_exactly_on_target() {
        assertTrue(isAtTarget(5.0, 5.0, 0.1))
    }

    @Test fun at_target_when_inside_tolerance() {
        assertTrue(isAtTarget(5.05, 5.0, 0.1))
        assertTrue(isAtTarget(4.95, 5.0, 0.1))
    }

    @Test fun at_target_when_exactly_at_tolerance_edge() {
        assertTrue(isAtTarget(5.1, 5.0, 0.1), "error equal to tolerance counts as at target")
    }

    @Test fun not_at_target_when_outside_tolerance() {
        assertFalse(isAtTarget(5.2, 5.0, 0.1))
        assertFalse(isAtTarget(4.8, 5.0, 0.1))
    }

    @Test fun intake_runs_when_commanded_and_empty() {
        assertTrue(shouldRunIntake(commanded = true, hasGamePiece = false))
    }

    @Test fun intake_stops_when_holding_a_piece() {
        assertFalse(shouldRunIntake(commanded = true, hasGamePiece = true))
    }

    @Test fun intake_stops_when_not_commanded() {
        assertFalse(shouldRunIntake(commanded = false, hasGamePiece = false))
        assertFalse(shouldRunIntake(commanded = false, hasGamePiece = true))
    }

    @Test fun clamp_leaves_in_range_values_alone() {
        assertEquals(6.0, clampVolts(6.0))
        assertEquals(-6.0, clampVolts(-6.0))
        assertEquals(0.0, clampVolts(0.0))
    }

    @Test fun clamp_limits_high_values_to_12() {
        assertEquals(12.0, clampVolts(15.0))
        assertEquals(12.0, clampVolts(12.0))
    }

    @Test fun clamp_limits_low_values_to_minus_12() {
        assertEquals(-12.0, clampVolts(-20.0))
        assertEquals(-12.0, clampVolts(-12.0))
    }
}
