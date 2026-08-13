package course.l9t4

import frc.stubs.lightning.Elevator
import frc.stubs.lightning.Wrist
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SuperstructureTest {
    @BeforeTest fun setUp() {
        Superstructure.reset()
    }

    @Test fun starts_home_with_mechanisms_stowed() {
        Superstructure.periodic()
        assertEquals(SuperState.HOME, Superstructure.state,
            "No buttons pressed: the superstructure should sit in HOME")
        assertEquals(Elevator.Setpoint.HOME, Superstructure.elevator.commandedTarget,
            "HOME should command the elevator to its HOME setpoint")
        assertEquals(Wrist.Setpoint.STOWED, Superstructure.wrist.commandedTarget,
            "HOME should command the wrist to STOWED")
    }

    @Test fun pickup_button_moves_to_coral_pickup() {
        Superstructure.commandedCoralPickup = true
        Superstructure.periodic()
        assertEquals(SuperState.CORAL_PICKUP, Superstructure.state,
            "commandedCoralPickup from HOME should enter CORAL_PICKUP")
        assertEquals(Elevator.Setpoint.CORAL_PICKUP, Superstructure.elevator.commandedTarget,
            "CORAL_PICKUP should command the elevator to CORAL_PICKUP")
        assertEquals(Wrist.Setpoint.CORAL_PICKUP, Superstructure.wrist.commandedTarget,
            "CORAL_PICKUP should command the wrist to CORAL_PICKUP")
    }

    @Test fun l4_with_coral_raises_to_l4_score() {
        Superstructure.coralInGripper = true
        Superstructure.commandedL4 = true
        Superstructure.periodic()
        assertEquals(SuperState.L4_SCORE, Superstructure.state,
            "L4 with coral in the gripper should go to L4_SCORE")
        assertEquals(Elevator.Setpoint.L4, Superstructure.elevator.commandedTarget,
            "L4_SCORE should command the elevator to L4")
        assertEquals(Wrist.Setpoint.SCORE, Superstructure.wrist.commandedTarget,
            "L4_SCORE should command the wrist to SCORE")
    }

    @Test fun l4_without_coral_stays_home() {
        Superstructure.commandedL4 = true
        Superstructure.periodic()
        assertEquals(SuperState.HOME, Superstructure.state,
            "No coral in the gripper: the FSM checks the gripper before raising, so L4 should do nothing")
        assertEquals(Elevator.Setpoint.HOME, Superstructure.elevator.commandedTarget,
            "With the L4 request refused, the elevator should stay commanded to HOME")
    }

    @Test fun at_target_takes_three_ticks() {
        Superstructure.coralInGripper = true
        Superstructure.commandedL4 = true
        Superstructure.periodic()
        assertEquals(SuperState.L4_SCORE, Superstructure.state,
            "L4 with coral in the gripper should go to L4_SCORE")
        assertFalse(Superstructure.atTarget(),
            "Mechanisms take time: one tick after commanding L4, atTarget() should still be false")
        Superstructure.periodic()
        assertFalse(Superstructure.atTarget(),
            "After two ticks the wrist has settled but the 3-tick elevator has not: atTarget() should be false")
        Superstructure.periodic()
        assertTrue(Superstructure.atTarget(),
            "After three ticks both mechanisms have settled: atTarget() should be true")
    }

    @Test fun score_at_l4_dunks_onto_the_branch() {
        Superstructure.coralInGripper = true
        Superstructure.commandedL4 = true
        Superstructure.periodic()
        Superstructure.commandedL4 = false
        Superstructure.commandedScore = true
        Superstructure.periodic()
        assertEquals(SuperState.L4_DUNK, Superstructure.state,
            "Score in L4_SCORE should go to L4_DUNK - the mechanism moves, the gripper stays closed")
        assertEquals(Elevator.Setpoint.L4_DUNK, Superstructure.elevator.commandedTarget,
            "L4_DUNK should drop the elevator to its L4_DUNK setpoint to jam the coral onto the branch")
        assertEquals(Wrist.Setpoint.DUNK, Superstructure.wrist.commandedTarget,
            "L4_DUNK should command the wrist to DUNK")
    }

    @Test fun score_at_l1_spits() {
        Superstructure.coralInGripper = true
        Superstructure.commandedL1 = true
        Superstructure.periodic()
        assertEquals(SuperState.L1_SCORE, Superstructure.state,
            "L1 with coral in the gripper should go to L1_SCORE")
        Superstructure.commandedL1 = false
        Superstructure.commandedScore = true
        Superstructure.periodic()
        assertEquals(SuperState.L1_SPIT, Superstructure.state,
            "L1 is the trough: Score in L1_SCORE should go to L1_SPIT, not a dunk")
        assertEquals(Wrist.Setpoint.SPIT, Superstructure.wrist.commandedTarget,
            "L1_SPIT should command the wrist to SPIT")
        assertEquals(Elevator.Setpoint.L1, Superstructure.elevator.commandedTarget,
            "L1_SPIT keeps the elevator at L1")
    }

    @Test fun score_at_l2_dunks_lower() {
        Superstructure.coralInGripper = true
        Superstructure.commandedL2 = true
        Superstructure.periodic()
        Superstructure.commandedL2 = false
        Superstructure.commandedScore = true
        Superstructure.periodic()
        assertEquals(SuperState.L2_DUNK, Superstructure.state,
            "Score in L2_SCORE should go to L2_DUNK")
        assertEquals(Elevator.Setpoint.L2_DUNK, Superstructure.elevator.commandedTarget,
            "L2_DUNK should drop the elevator to its L2_DUNK setpoint")
    }

    @Test fun score_at_l3_dunks_lower() {
        Superstructure.coralInGripper = true
        Superstructure.commandedL3 = true
        Superstructure.periodic()
        Superstructure.commandedL3 = false
        Superstructure.commandedScore = true
        Superstructure.periodic()
        assertEquals(SuperState.L3_DUNK, Superstructure.state,
            "Score in L3_SCORE should go to L3_DUNK")
        assertEquals(Elevator.Setpoint.L3_DUNK, Superstructure.elevator.commandedTarget,
            "L3_DUNK should drop the elevator to its L3_DUNK setpoint")
    }

    @Test fun level_change_straight_from_a_score_state() {
        Superstructure.coralInGripper = true
        Superstructure.commandedL4 = true
        Superstructure.periodic()
        Superstructure.commandedL4 = false
        Superstructure.commandedL2 = true
        Superstructure.periodic()
        assertEquals(SuperState.L2_SCORE, Superstructure.state,
            "The level rows are global: L4_SCORE should move straight to L2_SCORE without going home first")
        assertEquals(Elevator.Setpoint.L2, Superstructure.elevator.commandedTarget,
            "L2_SCORE should command the elevator to L2")
    }

    @Test fun home_from_a_dunk_returns_home() {
        Superstructure.coralInGripper = true
        Superstructure.commandedL4 = true
        Superstructure.periodic()
        Superstructure.commandedL4 = false
        Superstructure.commandedScore = true
        Superstructure.periodic()
        Superstructure.commandedScore = false
        Superstructure.commandedHome = true
        Superstructure.periodic()
        assertEquals(SuperState.HOME, Superstructure.state,
            "commandedHome is a global row: L4_DUNK should return to HOME")
        assertEquals(Elevator.Setpoint.HOME, Superstructure.elevator.commandedTarget,
            "Back in HOME the elevator should be commanded to HOME")
        assertEquals(Wrist.Setpoint.STOWED, Superstructure.wrist.commandedTarget,
            "Back in HOME the wrist should be commanded to STOWED")
    }

    @Test fun level_button_beats_home() {
        Superstructure.coralInGripper = true
        Superstructure.commandedL3 = true
        Superstructure.commandedHome = true
        Superstructure.periodic()
        assertEquals(SuperState.L3_SCORE, Superstructure.state,
            "commandedHome is the lowest-priority global row: with L3 and home both pressed, L3 wins")
    }

    // Pose-table spot checks: the driving tests above don't reach both halves
    // of every pair, so these pin the remaining rows of the table directly.

    @Test fun l1_score_pose_matches_the_table() {
        assertEquals(Elevator.Setpoint.L1, SuperState.L1_SCORE.elevator,
            "L1_SCORE's elevator setpoint doesn't match the pose table in task.md")
        assertEquals(Wrist.Setpoint.SCORE, SuperState.L1_SCORE.wrist,
            "L1_SCORE's wrist setpoint doesn't match the pose table in task.md")
    }

    @Test fun l2_score_pose_matches_the_table() {
        assertEquals(Elevator.Setpoint.L2, SuperState.L2_SCORE.elevator,
            "L2_SCORE's elevator setpoint doesn't match the pose table in task.md")
        assertEquals(Wrist.Setpoint.SCORE, SuperState.L2_SCORE.wrist,
            "L2_SCORE's wrist setpoint doesn't match the pose table in task.md")
    }

    @Test fun l3_score_pose_matches_the_table() {
        assertEquals(Elevator.Setpoint.L3, SuperState.L3_SCORE.elevator,
            "L3_SCORE's elevator setpoint doesn't match the pose table in task.md")
        assertEquals(Wrist.Setpoint.SCORE, SuperState.L3_SCORE.wrist,
            "L3_SCORE's wrist setpoint doesn't match the pose table in task.md")
    }

    @Test fun l2_dunk_pose_matches_the_table() {
        assertEquals(Elevator.Setpoint.L2_DUNK, SuperState.L2_DUNK.elevator,
            "L2_DUNK's elevator setpoint doesn't match the pose table in task.md")
        assertEquals(Wrist.Setpoint.DUNK, SuperState.L2_DUNK.wrist,
            "L2_DUNK's wrist setpoint doesn't match the pose table in task.md")
    }

    @Test fun l3_dunk_pose_matches_the_table() {
        assertEquals(Elevator.Setpoint.L3_DUNK, SuperState.L3_DUNK.elevator,
            "L3_DUNK's elevator setpoint doesn't match the pose table in task.md")
        assertEquals(Wrist.Setpoint.DUNK, SuperState.L3_DUNK.wrist,
            "L3_DUNK's wrist setpoint doesn't match the pose table in task.md")
    }
}
