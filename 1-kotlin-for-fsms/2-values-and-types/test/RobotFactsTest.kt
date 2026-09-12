package course.l1t2

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RobotFactsTest {
    @Test fun battery_volts_is_12_point_6() {
        assertEquals(12.6, RobotFacts.batteryVolts)
    }

    @Test fun match_seconds_is_150() {
        assertEquals(150, RobotFacts.matchSeconds)
    }

    @Test fun has_game_piece_is_false() {
        assertFalse(RobotFacts.hasGamePiece, "hasGamePiece must be false")
    }

    @Test fun robot_name_is_not_empty() {
        assertTrue(RobotFacts.robotName.isNotEmpty(), "robotName must not be empty")
    }

    @Test fun describe_uses_all_three_values() {
        assertEquals(
            "${RobotFacts.robotName}: 12.6 V, 150 s",
            RobotFacts.describe()
        )
    }
}
