package course.l7t6

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PhaseTimeoutTest {
    private lateinit var s: Superstructure

    @BeforeTest fun setUp() {
        s = Superstructure()
    }

    private fun runUntilSettled(maxTicks: Int = 25) {
        repeat(maxTicks) {
            s.periodic()
            if (s.atTarget()) return
        }
    }

    @Test fun normal_ascent_never_faults() {
        s.commandedRobotState = RobotState.SCORE_L4
        runUntilSettled()
        assertEquals(
            Superstructure.Transition.Settled(RobotState.SCORE_L4), s.transition,
            "with no time advanced the ascent must settle at SCORE_L4",
        )
        assertFalse(s.faulted, "a phase that settles in time must not fault")
        assertEquals(RobotState.SCORE_L4, s.commandedRobotState, "the goal must not change without a fault")
    }

    @Test fun stuck_elevator_phase_faults_and_heads_home() {
        s.commandedRobotState = RobotState.SCORE_L4
        s.periodic()   // enters MoveElevator(SCORE_L4); the timer starts here
        s.phaseTimer.simulateAdvance(2.1)
        s.periodic()
        assertTrue(s.faulted, "MoveElevator ran past phaseTimeoutSeconds, so faulted must be true")
        assertEquals(RobotState.STOWED, s.commandedRobotState, "a fault must set the goal to STOWED")
        assertEquals(
            Superstructure.Transition.MoveElevator(RobotState.STOWED), s.transition,
            "the arm is safe and the elevator is in flight, so the way home starts in MoveElevator(STOWED)",
        )
        runUntilSettled()
        assertEquals(
            Superstructure.Transition.Settled(RobotState.STOWED), s.transition,
            "after a fault the robot must settle at STOWED",
        )
    }

    @Test fun phase_under_the_limit_does_not_fault() {
        s.commandedRobotState = RobotState.SCORE_L4
        s.periodic()
        s.phaseTimer.simulateAdvance(1.9)
        s.periodic()
        assertFalse(s.faulted, "1.9 s is under the 2.0 s limit, so no fault")
        assertEquals(RobotState.SCORE_L4, s.commandedRobotState, "the goal must stay SCORE_L4 without a fault")
    }

    @Test fun settled_never_times_out() {
        s.commandedRobotState = RobotState.SCORE_L4
        runUntilSettled()
        s.phaseTimer.simulateAdvance(10.0)
        s.periodic()
        assertFalse(s.faulted, "Settled is not a phase; checkTimeout must ignore it")
        assertEquals(
            Superstructure.Transition.Settled(RobotState.SCORE_L4), s.transition,
            "a settled robot must stay settled however long it waits",
        )
    }

    @Test fun timer_restarts_on_each_phase() {
        s.commandedRobotState = RobotState.SCORE_L4
        s.periodic()   // MoveElevator begins
        s.phaseTimer.simulateAdvance(1.5)
        repeat(3) { s.periodic() }   // elevator settles, ExtendArm begins
        assertEquals(
            Superstructure.Transition.ExtendArm(RobotState.SCORE_L4), s.transition,
            "after the elevator settles the transition must be in ExtendArm",
        )
        s.phaseTimer.simulateAdvance(1.5)
        s.periodic()
        assertFalse(
            s.faulted,
            "1.5 s in MoveElevator plus 1.5 s in ExtendArm must not fault: enterPhase restarts the timer for each new phase",
        )
    }

    @Test fun stuck_arm_phase_retracts_first() {
        s.commandedRobotState = RobotState.SCORE_L4
        repeat(4) { s.periodic() }   // ExtendArm begins on tick 4; arm is now in flight
        assertEquals(
            Superstructure.Transition.ExtendArm(RobotState.SCORE_L4), s.transition,
            "setup: the transition should be in ExtendArm after 4 ticks",
        )
        s.phaseTimer.simulateAdvance(2.1)
        s.periodic()
        assertTrue(s.faulted, "ExtendArm ran past the limit, so faulted must be true")
        assertEquals(
            Superstructure.Transition.RetractArm(RobotState.STOWED), s.transition,
            "the arm is in flight and the elevator is HIGH, so the way home starts in RetractArm(STOWED)",
        )
        runUntilSettled()
        assertEquals(
            Superstructure.Transition.Settled(RobotState.STOWED), s.transition,
            "after a fault mid-ExtendArm the robot must still settle at STOWED",
        )
    }

    @Test fun clear_fault_then_follow_a_new_goal() {
        s.commandedRobotState = RobotState.SCORE_L4
        s.periodic()
        s.phaseTimer.simulateAdvance(2.1)
        s.periodic()
        assertTrue(s.faulted, "setup: the fault must be raised")
        runUntilSettled()
        s.clearFault()
        assertFalse(s.faulted, "clearFault() must set faulted to false")
        s.commandedRobotState = RobotState.SCORE_L4
        runUntilSettled()
        assertEquals(
            Superstructure.Transition.Settled(RobotState.SCORE_L4), s.transition,
            "after clearFault() a new goal must be followed to Settled",
        )
        assertFalse(s.faulted, "a normal move after clearFault() must not fault again")
    }
}
