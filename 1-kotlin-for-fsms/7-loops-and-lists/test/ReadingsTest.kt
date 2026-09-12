package course.l1t7

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReadingsTest {
    private val eps = 1e-9

    @Test fun average_of_empty_list_is_zero() {
        assertEquals(0.0, averageVolts(emptyList()), eps)
    }

    @Test fun average_of_one_reading_is_that_reading() {
        assertEquals(12.4, averageVolts(listOf(12.4)), eps)
    }

    @Test fun average_of_several_readings() {
        assertEquals(12.0, averageVolts(listOf(12.6, 11.8, 11.6)), eps)
    }

    @Test fun average_of_four_readings() {
        assertEquals(11.75, averageVolts(listOf(12.0, 11.5, 12.0, 11.5)), eps)
    }

    @Test fun no_modules_means_no_fault() {
        assertFalse(anyModuleFaulted(emptyList()))
    }

    @Test fun all_false_means_no_fault() {
        assertFalse(anyModuleFaulted(listOf(false, false, false, false)))
    }

    @Test fun one_true_means_a_fault() {
        assertTrue(anyModuleFaulted(listOf(false, false, true, false)))
    }

    @Test fun last_module_faulted_is_still_a_fault() {
        assertTrue(anyModuleFaulted(listOf(false, false, false, true)))
    }
}
