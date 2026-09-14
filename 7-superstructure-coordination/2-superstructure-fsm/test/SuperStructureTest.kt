package course.l7t2

import frc.stubs.OI
import frc.stubs.superstructure.Arm
import frc.stubs.superstructure.Elevator
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SuperStructureTest {
    @BeforeTest fun setUp() {
        OI.reset()
        Intake.reset()
        SuperStructure.reset()
    }

    private fun tick(n: Int = 1) = repeat(n) { SuperStructure.periodic() }

    private fun settleAt(pose: Pose) {
        SuperStructure.state = pose
        tick(10)
        OI.reset()
    }

    @Test fun starts_at_home() {
        assertEquals(Pose.HOME, SuperStructure.state, "the superstructure should start at HOME")
    }

    @Test fun home_intake_button_goes_to_coral_station() {
        OI.intake = true
        tick()
        assertEquals(Pose.CORAL_STATION, SuperStructure.state, "HOME + OI.intake with no piece should go to CORAL_STATION")
    }

    @Test fun home_intake_button_is_ignored_while_holding() {
        Intake.state = Intake.State.HOLDING
        OI.intake = true
        tick()
        assertEquals(Pose.HOME, SuperStructure.state, "HOME row 1 needs !holding: OI.intake while HOLDING must not move")
    }

    @Test fun home_score_l2_needs_a_piece() {
        OI.scoreL2 = true
        tick()
        assertEquals(Pose.HOME, SuperStructure.state, "HOME + OI.scoreL2 without a piece must stay HOME")
    }

    @Test fun home_score_l2_while_holding() {
        Intake.state = Intake.State.HOLDING
        OI.scoreL2 = true
        tick()
        assertEquals(Pose.L2, SuperStructure.state, "HOME + holding + OI.scoreL2 should go to L2")
    }

    @Test fun home_score_l4_while_holding() {
        Intake.state = Intake.State.HOLDING
        OI.scoreL4 = true
        tick()
        assertEquals(Pose.L4, SuperStructure.state, "HOME + holding + OI.scoreL4 should go to L4")
    }

    @Test fun home_priority_intake_and_score_both_held_while_holding() {
        Intake.state = Intake.State.HOLDING
        OI.intake = true
        OI.scoreL4 = true
        tick()
        assertEquals(Pose.L4, SuperStructure.state, "with a piece, row 1 (OI.intake && !holding) fails, so row 3 wins: L4")
    }

    @Test fun home_prepare_climb() {
        OI.prepareClimb = true
        tick()
        assertEquals(Pose.READY_TO_CLIMB, SuperStructure.state, "HOME + OI.prepareClimb should go to READY_TO_CLIMB")
    }

    @Test fun coral_station_home_button() {
        settleAt(Pose.CORAL_STATION)
        OI.home = true
        tick()
        assertEquals(Pose.HOME, SuperStructure.state, "CORAL_STATION + OI.home should go to HOME")
    }

    @Test fun coral_station_stays_without_home() {
        settleAt(Pose.CORAL_STATION)
        OI.scoreL4 = true
        Intake.state = Intake.State.HOLDING
        tick()
        assertEquals(Pose.CORAL_STATION, SuperStructure.state, "CORAL_STATION has one exit, OI.home; other buttons must not move it")
    }

    @Test fun l2_home_button() {
        settleAt(Pose.L2)
        OI.home = true
        tick()
        assertEquals(Pose.HOME, SuperStructure.state, "L2 + OI.home should go to HOME")
    }

    @Test fun l2_to_l4_while_holding() {
        settleAt(Pose.L2)
        Intake.state = Intake.State.HOLDING
        OI.scoreL4 = true
        tick()
        assertEquals(Pose.L4, SuperStructure.state, "L2 + holding + OI.scoreL4 should go to L4")
    }

    @Test fun l2_to_l4_needs_a_piece() {
        settleAt(Pose.L2)
        OI.scoreL4 = true
        tick()
        assertEquals(Pose.L2, SuperStructure.state, "L2 + OI.scoreL4 without a piece must stay L2")
    }

    @Test fun l4_home_button_wins_over_score_l2() {
        settleAt(Pose.L4)
        Intake.state = Intake.State.HOLDING
        OI.home = true
        OI.scoreL2 = true
        tick()
        assertEquals(Pose.HOME, SuperStructure.state, "in L4 the OI.home row is listed first, so it wins over scoreL2")
    }

    @Test fun l4_to_l2_while_holding() {
        settleAt(Pose.L4)
        Intake.state = Intake.State.HOLDING
        OI.scoreL2 = true
        tick()
        assertEquals(Pose.L2, SuperStructure.state, "L4 + holding + OI.scoreL2 should go to L2")
    }

    @Test fun ready_to_climb_climb_button() {
        settleAt(Pose.READY_TO_CLIMB)
        OI.climb = true
        tick()
        assertEquals(Pose.FULLY_CLIMBED, SuperStructure.state, "READY_TO_CLIMB + OI.climb should go to FULLY_CLIMBED")
    }

    @Test fun ready_to_climb_home_button() {
        settleAt(Pose.READY_TO_CLIMB)
        OI.home = true
        tick()
        assertEquals(Pose.HOME, SuperStructure.state, "READY_TO_CLIMB + OI.home should go to HOME")
    }

    @Test fun fully_climbed_has_no_exit() {
        settleAt(Pose.FULLY_CLIMBED)
        OI.home = true
        OI.intake = true
        OI.prepareClimb = true
        tick(3)
        assertEquals(Pose.FULLY_CLIMBED, SuperStructure.state, "FULLY_CLIMBED has no exit row; every button must be ignored")
    }

    @Test fun both_targets_commanded_on_first_tick() {
        Intake.state = Intake.State.HOLDING
        OI.scoreL4 = true
        tick()
        assertEquals(14.5, Elevator.target, "stateActions must send Elevator.goTo(state.elevatorRotations) on the tick the pose changes")
        assertEquals(45.0, Arm.target, "stateActions must send Arm.goTo(state.armDegrees) on the tick the pose changes")
    }

    @Test fun home_pose_is_commanded_every_tick() {
        tick()
        assertEquals(0.0, Elevator.target, "HOME must command the elevator to 0.0 rotations")
        assertEquals(90.0, Arm.target, "HOME must command the arm to 90.0 degrees")
    }

    @Test fun at_position_false_while_moving_then_true() {
        Intake.state = Intake.State.HOLDING
        OI.scoreL4 = true
        tick()
        assertFalse(SuperStructure.atPosition, "after 1 tick the elevator is at 3.0 of 14.5 rotations, so atPosition must be false")
        tick(3)
        assertFalse(SuperStructure.atPosition, "after 4 ticks the elevator is at 12.0 of 14.5 rotations, so atPosition must be false")
        tick()
        assertTrue(SuperStructure.atPosition, "after 5 ticks the elevator (5 x 3.0) and the arm (1 x 45) have both arrived")
    }
}
