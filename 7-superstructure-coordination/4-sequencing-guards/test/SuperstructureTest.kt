package course.l7t4

import frc.stubs.superstructure.Arm
import frc.stubs.superstructure.Elevator
import frc.stubs.superstructure.Intake
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SuperstructureTest {
    private lateinit var s: Superstructure

    @BeforeTest fun setUp() {
        s = Superstructure()
    }

    @Test fun starts_settled_at_stowed() {
        assertEquals(Superstructure.Transition.Settled(RobotState.STOWED), s.transition)
        assertTrue(s.atTarget())
    }

    @Test fun commanding_score_l4_starts_waiting_for_elevator() {
        s.commandedRobotState = RobotState.SCORE_L4
        s.periodic()
        assertEquals(
            Superstructure.Transition.WaitingForElevator(RobotState.SCORE_L4),
            s.transition
        )
        assertEquals(Elevator.State.HIGH, s.elevator.commandedTarget)
    }

    @Test fun arm_is_NOT_commanded_to_score_during_elevator_phase() {
        s.commandedRobotState = RobotState.SCORE_L4
        s.periodic()
        // Elevator is moving; arm should still be commanded to STOWED (its initial value).
        assertEquals(Arm.State.STOWED, s.arm.commandedTarget)
    }

    @Test fun arm_commanded_after_elevator_settles() {
        s.commandedRobotState = RobotState.SCORE_L4
        repeat(4) { s.periodic() }   // give elevator time to reach HIGH (3 ticks)
        // Now the transition should have advanced to WaitingForArm.
        assertEquals(
            Superstructure.Transition.WaitingForArm(RobotState.SCORE_L4),
            s.transition
        )
        assertEquals(Arm.State.SCORE, s.arm.commandedTarget)
        assertEquals(Intake.Mode.HOLDING, s.intake.commandedMode)
    }

    @Test fun fully_settled_after_enough_ticks() {
        s.commandedRobotState = RobotState.SCORE_L4
        repeat(8) { s.periodic() }
        assertEquals(
            Superstructure.Transition.Settled(RobotState.SCORE_L4),
            s.transition
        )
        assertTrue(s.atTarget())
    }

    @Test fun atTarget_is_false_during_transition() {
        s.commandedRobotState = RobotState.SCORE_L4
        s.periodic()
        assertFalse(s.atTarget())
    }

    @Test fun new_goal_after_settling_restarts_the_transition() {
        s.commandedRobotState = RobotState.SCORE_L4
        repeat(8) { s.periodic() }
        s.commandedRobotState = RobotState.CLIMB_PREP
        s.periodic()
        assertEquals(
            Superstructure.Transition.WaitingForElevator(RobotState.CLIMB_PREP),
            s.transition,
            "a new goal after settling must restart at WaitingForElevator(newGoal)"
        )
        assertEquals(
            Elevator.State.STOWED, s.elevator.commandedTarget,
            "WaitingForElevator must command the elevator to the new goal's elevator setpoint"
        )
        assertEquals(
            Arm.State.SCORE, s.arm.commandedTarget,
            "WaitingForElevator must not change the arm command"
        )
    }

    @Test fun settled_keeps_commanding_the_pose() {
        s.commandedRobotState = RobotState.SCORE_L4
        repeat(8) { s.periodic() }
        s.arm.commandedTarget = Arm.State.STOWED   // something else moved the arm
        s.periodic()
        assertEquals(
            Arm.State.SCORE, s.arm.commandedTarget,
            "Settled(at) must command at.arm every tick (task.md table, Settled row)"
        )
    }
}
