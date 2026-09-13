package course.l7t5

import frc.stubs.superstructure.Arm
import frc.stubs.superstructure.Elevator
import frc.stubs.superstructure.Intake
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
        // The arm starts settled at STOWED (safe), so RetractArm is skipped.
        assertEquals(
            Superstructure.Transition.MoveElevator(RobotState.SCORE_L4),
            s.transition,
            "from STOWED the arm is already safe, so the ascent must begin in MoveElevator",
        )
        assertEquals(
            Elevator.State.HIGH, s.elevator.commandedTarget,
            "MoveElevator must command the elevator to the goal's elevator setpoint",
        )
        assertEquals(
            Arm.State.STOWED, s.arm.commandedTarget,
            "MoveElevator must not command the arm",
        )
    }

    @Test fun ascent_settles_at_score_l4() {
        s.commandedRobotState = RobotState.SCORE_L4
        runUntilSettled()
        assertEquals(
            Superstructure.Transition.Settled(RobotState.SCORE_L4),
            s.transition,
            "after MoveElevator then ExtendArm the superstructure must settle at SCORE_L4",
        )
        assertEquals(Elevator.State.HIGH, s.elevator.state, "elevator must end at HIGH")
        assertEquals(Arm.State.SCORE, s.arm.state, "arm must end at SCORE")
        assertEquals(Intake.Request.STOP, s.intake.request, "SCORE_L4 asks the rollers to STOP")
    }

    @Test fun descent_arm_retracts_first() {
        s.commandedRobotState = RobotState.SCORE_L4
        runUntilSettled()
        s.commandedRobotState = RobotState.STOWED
        s.periodic()
        // The elevator must move (HIGH -> STOWED) and the arm is out, so retract first.
        assertEquals(
            Superstructure.Transition.RetractArm(RobotState.STOWED),
            s.transition,
            "SCORE_L4 -> STOWED moves the elevator, so the arm must retract first",
        )
        assertEquals(
            Arm.State.STOWED, s.arm.commandedTarget,
            "RetractArm must command the arm to STOWED",
        )
        assertNotEquals(
            Elevator.State.STOWED, s.elevator.commandedTarget,
            "RetractArm must not command the elevator yet",
        )
    }

    @Test fun descent_elevator_moves_after_arm_retracted() {
        s.commandedRobotState = RobotState.SCORE_L4
        runUntilSettled()
        s.commandedRobotState = RobotState.STOWED
        // Tick 1 enters RetractArm and commands the arm (2 ticks). Tick 2 the
        // arm arrives. Tick 3 stateTransitions() sees arm.atTarget() and
        // enters MoveElevator. Tick 4 the elevator is on its way.
        repeat(4) { s.periodic() }
        assertEquals(Arm.State.STOWED, s.arm.state, "arm must be settled at STOWED after 4 ticks")
        assertTrue(
            s.transition is Superstructure.Transition.MoveElevator ||
                s.transition is Superstructure.Transition.Settled,
            "after the arm retracts the phase must be MoveElevator (or Settled); was ${s.transition}",
        )
        assertEquals(
            Elevator.State.STOWED, s.elevator.commandedTarget,
            "MoveElevator must command the elevator to STOWED",
        )
    }

    @Test fun descent_settles_at_stowed() {
        s.commandedRobotState = RobotState.SCORE_L4
        runUntilSettled()
        s.commandedRobotState = RobotState.STOWED
        runUntilSettled()
        assertEquals(
            Superstructure.Transition.Settled(RobotState.STOWED),
            s.transition,
            "RetractArm then MoveElevator must end Settled at STOWED",
        )
    }

    @Test fun direct_transition_between_extended_poses() {
        s.commandedRobotState = RobotState.SCORE_L4
        runUntilSettled()
        // Both poses have the arm extended; going between them must pass through safe.
        s.commandedRobotState = RobotState.INTAKE_GROUND
        s.periodic()
        assertEquals(
            Superstructure.Transition.RetractArm(RobotState.INTAKE_GROUND),
            s.transition,
            "SCORE_L4 -> INTAKE_GROUND moves the elevator, so the arm must retract first",
        )
        assertEquals(
            Arm.State.STOWED,
            s.arm.commandedTarget,
            "RetractArm must command the arm to STOWED, not to the target pose's arm state",
        )
        runUntilSettled()
        assertEquals(
            Superstructure.Transition.Settled(RobotState.INTAKE_GROUND),
            s.transition,
            "after retract -> move -> extend, the superstructure must settle at INTAKE_GROUND",
        )
    }

    @Test fun climb_prep_skips_straight_to_extend_arm() {
        s.commandedRobotState = RobotState.CLIMB_PREP
        s.periodic()
        // CLIMB_PREP keeps the elevator at STOWED, so RetractArm and MoveElevator are skipped.
        assertEquals(
            Superstructure.Transition.ExtendArm(RobotState.CLIMB_PREP),
            s.transition,
            "the elevator is already at CLIMB_PREP's height, so the transition must begin at ExtendArm",
        )
        runUntilSettled()
        assertEquals(
            Superstructure.Transition.Settled(RobotState.CLIMB_PREP),
            s.transition,
            "the arm swing is all that is needed to settle at CLIMB_PREP",
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
            s.transition,
            "after an abort to STOWED the superstructure must settle at STOWED",
        )
        assertEquals(
            Elevator.State.STOWED,
            s.elevator.state,
            "after aborting to STOWED the elevator must end at STOWED (does Settled keep commanding the pose?)",
        )
        assertEquals(
            Arm.State.STOWED,
            s.arm.state,
            "after aborting to STOWED the arm must end at STOWED (does Settled keep commanding the pose?)",
        )
    }

    @Test fun abort_mid_flight_takes_time_to_return() {
        s.commandedRobotState = RobotState.SCORE_L4
        s.periodic()  // tick 1: MoveElevator, elevator commanded HIGH (3 ticks)
        s.periodic()  // tick 2: elevator one tick up, two to go

        s.commandedRobotState = RobotState.STOWED
        s.periodic()  // tick 3: abort. The elevator reverses; it needs the 1 tick it traveled.
        assertFalse(
            s.elevator.atTarget(),
            "right after the abort the elevator is still in flight; it cannot be at target",
        )
        assertEquals(
            Superstructure.Transition.MoveElevator(RobotState.STOWED),
            s.transition,
            "the arm is safe and the elevator is in flight, so the abort must begin in MoveElevator(STOWED)",
        )

        repeat(2) { s.periodic() }
        assertTrue(s.elevator.atTarget(), "the elevator must be back at target within two more ticks")
        assertEquals(Elevator.State.STOWED, s.elevator.state, "the elevator must have returned to STOWED")
    }

    @Test fun multi_state_cycle_returns_home() {
        s.commandedRobotState = RobotState.INTAKE_GROUND
        runUntilSettled()
        assertTrue(s.atTarget(), "must settle at INTAKE_GROUND")

        s.commandedRobotState = RobotState.STOWED
        runUntilSettled()
        assertTrue(s.atTarget(), "must settle at STOWED after INTAKE_GROUND")

        s.commandedRobotState = RobotState.SCORE_L4
        runUntilSettled()
        assertTrue(s.atTarget(), "must settle at SCORE_L4")

        s.commandedRobotState = RobotState.STOWED
        runUntilSettled()
        assertEquals(
            Superstructure.Transition.Settled(RobotState.STOWED),
            s.transition,
            "the full cycle must end Settled at STOWED",
        )
    }
}
