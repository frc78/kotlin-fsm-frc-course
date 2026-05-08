package course.l7t5

import frc.stubs.superstructure.Arm
import frc.stubs.superstructure.Elevator
import frc.stubs.superstructure.Intake
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SuperstructureTest {
    private lateinit var s: Superstructure

    @BeforeTest fun setUp() {
        s = Superstructure()
    }

    private fun runUntilSettled(maxTicks: Int = 20) {
        repeat(maxTicks) {
            s.periodic()
            if (s.atTarget()) return
        }
    }

    @Test fun ascent_elevator_before_arm() {
        s.commandedRobotState = RobotState.SCORE_L4
        s.periodic()
        // Arm starts STOWED — already safe — so we should go straight to MoveElevator.
        assertEquals(
            Superstructure.Transition.MoveElevator(RobotState.SCORE_L4),
            s.transition
        )
        assertEquals(Elevator.State.HIGH, s.elevator.commandedTarget)
        assertEquals(Arm.State.STOWED, s.arm.commandedTarget)
    }

    @Test fun ascent_settles_at_score_l4() {
        s.commandedRobotState = RobotState.SCORE_L4
        runUntilSettled()
        assertEquals(
            Superstructure.Transition.Settled(RobotState.SCORE_L4),
            s.transition
        )
        assertEquals(Elevator.State.HIGH, s.elevator.state)
        assertEquals(Arm.State.SCORE, s.arm.state)
        assertEquals(Intake.Mode.HOLDING, s.intake.mode)
    }

    @Test fun descent_arm_retracts_first() {
        s.commandedRobotState = RobotState.SCORE_L4
        runUntilSettled()
        // Now we're at SCORE_L4. Command STOWED.
        s.commandedRobotState = RobotState.STOWED
        s.periodic()
        // Arm needs to retract first (because elevator targets STOWED, different from current HIGH).
        assertEquals(
            Superstructure.Transition.RetractArm(RobotState.STOWED),
            s.transition
        )
        assertEquals(Arm.State.STOWED, s.arm.commandedTarget)
        // Elevator should still be commanded HIGH (or unchanged) — not yet asked to descend.
        assertNotEquals(Elevator.State.STOWED, s.elevator.commandedTarget)
    }

    @Test fun descent_elevator_moves_after_arm_retracted() {
        s.commandedRobotState = RobotState.SCORE_L4
        runUntilSettled()
        s.commandedRobotState = RobotState.STOWED
        // 2 ticks for arm to retract + a tick to advance phase
        repeat(4) { s.periodic() }
        assertEquals(Arm.State.STOWED, s.arm.state)
        // Now elevator should be in MoveElevator phase or settled
        assertTrue(
            s.transition is Superstructure.Transition.MoveElevator ||
                s.transition is Superstructure.Transition.Settled,
            "transition is ${s.transition}",
        )
        assertEquals(Elevator.State.STOWED, s.elevator.commandedTarget)
    }

    @Test fun descent_settles_at_stowed() {
        s.commandedRobotState = RobotState.SCORE_L4
        runUntilSettled()
        s.commandedRobotState = RobotState.STOWED
        runUntilSettled()
        assertEquals(
            Superstructure.Transition.Settled(RobotState.STOWED),
            s.transition
        )
    }

    @Test fun mid_ascent_abort_returns_to_stowed_safely() {
        s.commandedRobotState = RobotState.SCORE_L4
        s.periodic()  // start moving elevator
        s.periodic()  // mid-flight (elevator still moving)

        // Abort: cancel back to STOWED before reaching HIGH.
        s.commandedRobotState = RobotState.STOWED
        runUntilSettled()
        assertEquals(
            Superstructure.Transition.Settled(RobotState.STOWED),
            s.transition
        )
        assertEquals(Elevator.State.STOWED, s.elevator.state)
        assertEquals(Arm.State.STOWED, s.arm.state)
    }

    @Test fun multi_state_cycle_returns_home() {
        s.commandedRobotState = RobotState.INTAKE_GROUND
        runUntilSettled()
        assertTrue(s.atTarget())

        s.commandedRobotState = RobotState.STOWED
        runUntilSettled()
        assertTrue(s.atTarget())

        s.commandedRobotState = RobotState.SCORE_L4
        runUntilSettled()
        assertTrue(s.atTarget())

        s.commandedRobotState = RobotState.STOWED
        runUntilSettled()
        assertEquals(
            Superstructure.Transition.Settled(RobotState.STOWED),
            s.transition
        )
    }
}
