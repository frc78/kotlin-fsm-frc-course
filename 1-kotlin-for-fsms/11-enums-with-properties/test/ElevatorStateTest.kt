package course.l1t11

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ElevatorStateTest {
    private fun byName(name: String) =
        ElevatorState.entries.find { it.name == name }

    @Test fun has_four_values() {
        assertEquals(4, ElevatorState.entries.size)
    }

    @Test fun stowed_target_is_zero() {
        val v = byName("STOWED")
        assertNotNull(v, "expected a STOWED value")
        assertEquals(0.0, v.targetRotations)
    }

    @Test fun low_target_is_four() {
        val v = byName("LOW")
        assertNotNull(v, "expected a LOW value")
        assertEquals(4.0, v.targetRotations)
    }

    @Test fun mid_target_is_nine_point_five() {
        val v = byName("MID")
        assertNotNull(v, "expected a MID value")
        assertEquals(9.5, v.targetRotations)
    }

    @Test fun high_target_is_fourteen_point_five() {
        val v = byName("HIGH")
        assertNotNull(v, "expected a HIGH value")
        assertEquals(14.5, v.targetRotations)
    }

    @Test fun targets_increase_in_declaration_order() {
        val targets = ElevatorState.entries.map { it.targetRotations }
        assertEquals(
            targets.sorted(), targets,
            "states should be declared in increasing target order"
        )
    }
}
