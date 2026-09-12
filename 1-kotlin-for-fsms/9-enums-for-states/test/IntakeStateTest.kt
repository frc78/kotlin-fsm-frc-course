package course.l1t9

import kotlin.test.Test
import kotlin.test.assertEquals

class IntakeStateTest {
    @Test fun has_four_values() {
        assertEquals(4, IntakeState.entries.size)
    }

    @Test fun first_value_is_idle() {
        assertEquals("IDLE", IntakeState.entries[0].name)
    }

    @Test fun second_value_is_intaking() {
        assertEquals("INTAKING", IntakeState.entries[1].name)
    }

    @Test fun third_value_is_holding() {
        assertEquals("HOLDING", IntakeState.entries[2].name)
    }

    @Test fun fourth_value_is_ejecting() {
        assertEquals("EJECTING", IntakeState.entries[3].name)
    }
}
