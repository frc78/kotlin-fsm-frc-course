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

    @Test fun atTarget_false_while_only_arm_still_moving() {
        // CLIMB_PREP keeps the elevator at STOWED — only the arm has to move.
        s.commandedRobotState = RobotState.CLIMB_PREP
        s.periodic()
        assertFalse(
            s.atTarget(),
            "the elevator is already at STOWED but the arm is still swinging to CLIMB — atTarget() must check every subsystem",
        )
    }

    @Test fun atTarget_false_while_only_elevator_still_moving() {
        s.commandedRobotState = RobotState.SCORE_L4
        repeat(2) { s.periodic() }
        assertFalse(
            s.atTarget(),
            "the arm (2 ticks) has settled but the elevator (3 ticks) hasn't — atTarget() must check every subsystem",
        )
    }

    @Test fun atTarget_false_while_only_intake_still_spinning_up() {
        // INTAKE_GROUND changes all three. After 3 ticks the elevator (3) and
        // the arm (2) are settled. The rollers (4) are not.
        s.commandedRobotState = RobotState.INTAKE_GROUND
        repeat(3) { s.periodic() }
        assertTrue(s.elevator.atTarget(), "elevator should be settled after 3 ticks")
        assertTrue(s.arm.atTarget(), "arm should be settled after 3 ticks")
        assertFalse(
            s.atTarget(),
            "the rollers need 4 ticks to spin up — atTarget() must include intake.requestReached()",
        )
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
