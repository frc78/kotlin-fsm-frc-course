package course.l1t10

import kotlin.test.Test
import kotlin.test.assertEquals

class RollerStateTest {
    @Test fun stopped_is_zero_volts() {
        assertEquals(0.0, voltsFor(RollerState.STOPPED))
    }

    @Test fun forward_is_eight_volts() {
        assertEquals(8.0, voltsFor(RollerState.FORWARD))
    }

    @Test fun reverse_is_minus_four_volts() {
        assertEquals(-4.0, voltsFor(RollerState.REVERSE))
    }

    @Test fun every_state_has_a_voltage() {
        for (s in RollerState.entries) {
            voltsFor(s)
        }
    }
}
