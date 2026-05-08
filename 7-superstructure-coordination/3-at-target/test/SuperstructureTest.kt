package course.l7t3

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SuperstructureTest {
    private lateinit var s: Superstructure

    @BeforeTest fun setUp() {
        s = Superstructure()
    }

    @Test fun atTarget_true_for_initial_stowed_after_one_tick() {
        s.periodic()
        assertTrue(s.atTarget())
    }

    @Test fun atTarget_false_immediately_after_commanding_new_state() {
        s.commandedRobotState = RobotState.SCORE_L4
        s.periodic()
        assertFalse(s.atTarget(), "elevator hasn't settled yet")
    }

    @Test fun atTarget_true_after_subsystems_settle() {
        s.commandedRobotState = RobotState.SCORE_L4
        repeat(6) { s.periodic() }
        assertTrue(s.atTarget())
    }

    @Test fun atTarget_flips_back_to_false_when_state_changes() {
        s.commandedRobotState = RobotState.SCORE_L4
        repeat(6) { s.periodic() }
        assertTrue(s.atTarget())
        s.commandedRobotState = RobotState.INTAKE_GROUND
        s.periodic()
        assertFalse(s.atTarget())
    }
}
