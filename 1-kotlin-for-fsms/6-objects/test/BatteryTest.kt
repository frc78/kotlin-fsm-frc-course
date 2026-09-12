package course.l1t6

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BatteryTest {
    @BeforeTest fun setUp() {
        Battery.reset()
    }

    @Test fun full_battery_is_not_low() {
        assertFalse(Battery.isLow(), "12.6 V is not low")
    }

    @Test fun below_11_point_5_is_low() {
        Battery.volts = 11.4
        assertTrue(Battery.isLow(), "11.4 V is low")
    }

    @Test fun exactly_11_point_5_is_not_low() {
        Battery.volts = 11.5
        assertFalse(Battery.isLow(), "11.5 V is the limit and is not low")
    }

    @Test fun full_battery_is_100_percent() {
        assertEquals(100, Battery.percent())
    }

    @Test fun ten_volts_is_0_percent() {
        Battery.volts = 10.0
        assertEquals(0, Battery.percent())
    }

    @Test fun midpoint_is_50_percent() {
        Battery.volts = 11.3
        assertEquals(50, Battery.percent())
    }

    @Test fun percent_rounds_to_nearest_whole_number() {
        Battery.volts = 11.0
        assertEquals(38, Battery.percent(), "1.0 / 2.6 * 100 = 38.46, rounds to 38")
    }

    @Test fun percent_never_goes_above_100() {
        Battery.volts = 13.2
        assertEquals(100, Battery.percent())
    }

    @Test fun percent_never_goes_below_0() {
        Battery.volts = 9.0
        assertEquals(0, Battery.percent())
    }

    @Test fun reset_restores_full_voltage() {
        Battery.volts = 9.0
        Battery.reset()
        assertEquals(12.6, Battery.volts)
    }
}
