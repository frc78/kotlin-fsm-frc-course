package course.l1t1

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RobotIntroTest {
    @BeforeTest fun setUp() {
        RobotIntro.matchScore = 0
    }

    @Test fun team_name_is_set() {
        assertTrue(
            RobotIntro.teamName.isNotEmpty(),
            "teamName should be replaced with a non-empty string"
        )
    }

    @Test fun match_score_starts_at_zero() {
        assertEquals(0, RobotIntro.matchScore)
    }

    @Test fun greeting_uses_template() {
        assertEquals("Hello from ${RobotIntro.teamName}!", RobotIntro.greeting())
    }

    @Test fun describe_score_reflects_current_score() {
        RobotIntro.matchScore = 0
        assertEquals("Score: 0", RobotIntro.describeScore())

        RobotIntro.matchScore = 42
        assertEquals("Score: 42", RobotIntro.describeScore())
    }
}
