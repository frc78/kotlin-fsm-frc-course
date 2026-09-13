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
        assertEquals(
            Superstructure.Transition.Settled(RobotState.STOWED), s.transition,
            "a fresh Superstructure starts Settled(STOWED)",
        )
        assertTrue(s.atTarget(), "Settled means atTarget() is true")
    }

    @Test fun commanding_score_l4_starts_waiting_for_elevator() {
        s.commandedRobotState = RobotState.SCORE_L4
        s.periodic()
        assertEquals(
            Superstructure.Transition.WaitingForElevator(RobotState.SCORE_L4), s.transition,
            "a new goal must start at WaitingForElevator(goal); the elevator is still at STOWED, so the phase must not advance yet (check elevator.state, not only atTarget())",
        )
        assertEquals(
            Elevator.State.HIGH, s.elevator.commandedTarget,
            "WaitingForElevator must command the elevator to the goal's elevator setpoint",
        )
    }

    @Test fun arm_is_NOT_commanded_to_score_during_elevator_phase() {
        s.commandedRobotState = RobotState.SCORE_L4
        s.periodic()
        assertEquals(
            Arm.State.STOWED, s.arm.commandedTarget,
            "WaitingForElevator must not command the arm; it keeps its initial STOWED command",
        )
    }

    @Test fun arm_commanded_after_elevator_settles() {
        s.commandedRobotState = RobotState.SCORE_L4
        // tick 1: WaitingForElevator, elevator commanded HIGH (3 ticks to go)
        // tick 2, 3: elevator moving; settles at the end of tick 3
        // tick 4: stateTransitions() sees the settled elevator -> WaitingForArm; actions command the arm
        repeat(4) { s.periodic() }
        assertEquals(
            Superstructure.Transition.WaitingForArm(RobotState.SCORE_L4), s.transition,
            "one tick after the elevator settles at HIGH the phase must be WaitingForArm",
        )
        assertEquals(
            Arm.State.SCORE, s.arm.commandedTarget,
            "WaitingForArm must command the arm to the goal's arm setpoint",
        )
        assertEquals(
            Intake.Request.STOP, s.intake.request,
            "WaitingForArm must command the intake to the goal's request",
        )
    }

    @Test fun fully_settled_after_enough_ticks() {
        s.commandedRobotState = RobotState.SCORE_L4
        // tick 4: WaitingForArm, arm commanded (2 ticks to go)
        // tick 5: arm settles at the end of the tick
        // tick 6: stateTransitions() sees the settled arm -> Settled(SCORE_L4)
        repeat(8) { s.periodic() }
        assertEquals(
            Superstructure.Transition.Settled(RobotState.SCORE_L4), s.transition,
            "after the elevator (3 ticks) and then the arm (2 ticks) settle, the phase must be Settled(SCORE_L4)",
        )
        assertTrue(s.atTarget(), "Settled means atTarget() is true")
    }

    @Test fun atTarget_is_false_during_transition() {
        s.commandedRobotState = RobotState.SCORE_L4
        s.periodic()
        assertFalse(s.atTarget(), "atTarget() must be false while a transition is in progress")
    }

    @Test fun new_goal_after_settling_restarts_the_transition() {
        s.commandedRobotState = RobotState.SCORE_L4
        repeat(8) { s.periodic() }
        s.commandedRobotState = RobotState.CLIMB_PREP
        s.periodic()
        assertEquals(
            Superstructure.Transition.WaitingForElevator(RobotState.CLIMB_PREP), s.transition,
            "a new goal after settling must restart at WaitingForElevator(newGoal)",
        )
        assertEquals(
            Elevator.State.STOWED, s.elevator.commandedTarget,
            "WaitingForElevator must command the elevator to the new goal's elevator setpoint",
        )
        assertEquals(
            Arm.State.SCORE, s.arm.commandedTarget,
            "WaitingForElevator must not change the arm command",
        )
    }

    @Test fun settled_keeps_commanding_the_pose() {
        s.commandedRobotState = RobotState.SCORE_L4
        repeat(8) { s.periodic() }
        s.arm.commandedTarget = Arm.State.STOWED   // something else moved the arm
        s.periodic()
        assertEquals(
            Arm.State.SCORE, s.arm.commandedTarget,
            "Settled(at) must command at.arm every tick (task.md table, Settled row)",
        )
    }
}
