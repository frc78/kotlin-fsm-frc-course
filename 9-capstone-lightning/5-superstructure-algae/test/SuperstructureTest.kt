package course.l9t5

import frc.stubs.lightning.Elevator
import frc.stubs.lightning.Wrist
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SuperstructureTest {
    @BeforeTest fun setUp() {
        Superstructure.reset()
    }

    /** Floor-pickup path into the carry pose: floor button, grab, home. */
    private fun driveToAlgaeHome() {
        Superstructure.commandedAlgaeFloor = true
        Superstructure.periodic()
        assertEquals(SuperState.ALGAE_FLOOR_PICKUP, Superstructure.state,
            "Setup: commandedAlgaeFloor from HOME should enter ALGAE_FLOOR_PICKUP")
        Superstructure.commandedAlgaeFloor = false
        Superstructure.algaeInGripper = true
        Superstructure.commandedHome = true
        Superstructure.periodic()
        assertEquals(SuperState.ALGAE_HOME, Superstructure.state,
            "Home while holding algae should land in ALGAE_HOME (the carry pose), not HOME - the algae rows run before the plain home row")
        Superstructure.commandedHome = false
    }

    @Test fun l2_with_empty_gripper_goes_for_algae() {
        Superstructure.commandedL2 = true
        Superstructure.periodic()
        assertEquals(SuperState.L2_ALGAE_PICKUP, Superstructure.state,
            "Same button, no coral: L2 with an empty gripper means grab the algae, so HOME should enter L2_ALGAE_PICKUP")
        assertEquals(Elevator.Setpoint.ALGAE_L2, Superstructure.elevator.commandedTarget,
            "L2_ALGAE_PICKUP should command the elevator to ALGAE_L2")
        assertEquals(Wrist.Setpoint.ALGAE_PICKUP, Superstructure.wrist.commandedTarget,
            "L2_ALGAE_PICKUP should command the wrist to ALGAE_PICKUP")
    }

    @Test fun l3_with_empty_gripper_goes_for_algae() {
        Superstructure.commandedL3 = true
        Superstructure.periodic()
        assertEquals(SuperState.L3_ALGAE_PICKUP, Superstructure.state,
            "L3 with an empty gripper should enter L3_ALGAE_PICKUP")
        assertEquals(Elevator.Setpoint.ALGAE_L3, Superstructure.elevator.commandedTarget,
            "L3_ALGAE_PICKUP should command the elevator to ALGAE_L3")
    }

    @Test fun l2_with_coral_still_scores() {
        Superstructure.coralInGripper = true
        Superstructure.commandedL2 = true
        Superstructure.periodic()
        assertEquals(SuperState.L2_SCORE, Superstructure.state,
            "With coral in the gripper the same L2 button must still mean L2_SCORE - check the !coralInGripper guard on your algae row")
    }

    @Test fun algae_floor_button_starts_floor_pickup() {
        Superstructure.commandedAlgaeFloor = true
        Superstructure.periodic()
        assertEquals(SuperState.ALGAE_FLOOR_PICKUP, Superstructure.state,
            "commandedAlgaeFloor from HOME should enter ALGAE_FLOOR_PICKUP")
        assertEquals(Elevator.Setpoint.ALGAE_FLOOR, Superstructure.elevator.commandedTarget,
            "ALGAE_FLOOR_PICKUP should command the elevator to ALGAE_FLOOR")
        assertEquals(Wrist.Setpoint.ALGAE_PICKUP, Superstructure.wrist.commandedTarget,
            "ALGAE_FLOOR_PICKUP should command the wrist to ALGAE_PICKUP")
    }

    @Test fun home_with_algae_goes_to_the_carry_pose() {
        Superstructure.commandedL2 = true
        Superstructure.periodic()
        assertEquals(SuperState.L2_ALGAE_PICKUP, Superstructure.state,
            "Setup: L2 with an empty gripper should enter L2_ALGAE_PICKUP")
        Superstructure.commandedL2 = false
        Superstructure.algaeInGripper = true
        Superstructure.commandedHome = true
        Superstructure.periodic()
        assertEquals(SuperState.ALGAE_HOME, Superstructure.state,
            "Home while holding algae should go to ALGAE_HOME, not HOME - carrying algae keeps you in the algae branch")
        assertEquals(Elevator.Setpoint.ALGAE_CARRY, Superstructure.elevator.commandedTarget,
            "ALGAE_HOME should command the elevator to ALGAE_CARRY")
        assertEquals(Wrist.Setpoint.CARRY, Superstructure.wrist.commandedTarget,
            "ALGAE_HOME should command the wrist to CARRY")
    }

    @Test fun home_without_algae_returns_home() {
        Superstructure.commandedL3 = true
        Superstructure.periodic()
        assertEquals(SuperState.L3_ALGAE_PICKUP, Superstructure.state,
            "Setup: L3 with an empty gripper should enter L3_ALGAE_PICKUP")
        Superstructure.commandedL3 = false
        Superstructure.commandedHome = true
        Superstructure.periodic()
        assertEquals(SuperState.HOME, Superstructure.state,
            "Came back empty: with no algae in the gripper no algae row matches, so home falls through to the plain HOME row")
    }

    @Test fun l4_from_algae_home_scores_in_the_net() {
        driveToAlgaeHome()
        Superstructure.commandedL4 = true
        Superstructure.periodic()
        assertEquals(SuperState.NET_SCORE, Superstructure.state,
            "From ALGAE_HOME the L4 button means the net: expected NET_SCORE")
        assertEquals(Elevator.Setpoint.NET, Superstructure.elevator.commandedTarget,
            "NET_SCORE should command the elevator to NET")
        assertEquals(Wrist.Setpoint.NET, Superstructure.wrist.commandedTarget,
            "NET_SCORE should command the wrist to NET")
    }

    @Test fun l1_from_algae_home_goes_to_the_processor() {
        Superstructure.commandedL3 = true
        Superstructure.periodic()
        assertEquals(SuperState.L3_ALGAE_PICKUP, Superstructure.state,
            "Setup: L3 with an empty gripper should enter L3_ALGAE_PICKUP")
        Superstructure.commandedL3 = false
        Superstructure.algaeInGripper = true
        Superstructure.commandedHome = true
        Superstructure.periodic()
        assertEquals(SuperState.ALGAE_HOME, Superstructure.state,
            "Setup: home while holding algae should carry it - L3_ALGAE_PICKUP goes to ALGAE_HOME")
        Superstructure.commandedHome = false
        Superstructure.commandedL1 = true
        Superstructure.periodic()
        assertEquals(SuperState.PROCESSOR_SCORE, Superstructure.state,
            "From ALGAE_HOME the L1 button means the processor: expected PROCESSOR_SCORE")
        assertEquals(Elevator.Setpoint.PROCESSOR, Superstructure.elevator.commandedTarget,
            "PROCESSOR_SCORE should command the elevator to PROCESSOR")
        assertEquals(Wrist.Setpoint.PROCESSOR, Superstructure.wrist.commandedTarget,
            "PROCESSOR_SCORE should command the wrist to PROCESSOR")
    }

    @Test fun home_with_algae_from_the_net_returns_to_carry() {
        driveToAlgaeHome()
        Superstructure.commandedL4 = true
        Superstructure.periodic()
        assertEquals(SuperState.NET_SCORE, Superstructure.state,
            "Setup: from ALGAE_HOME the L4 button should enter NET_SCORE")
        Superstructure.commandedL4 = false
        Superstructure.commandedHome = true
        Superstructure.periodic()
        assertEquals(SuperState.ALGAE_HOME, Superstructure.state,
            "Still holding the algae after lining up the net: home should return to ALGAE_HOME, not HOME")
    }

    @Test fun home_with_algae_from_the_processor_returns_to_carry() {
        driveToAlgaeHome()
        Superstructure.commandedL1 = true
        Superstructure.periodic()
        assertEquals(SuperState.PROCESSOR_SCORE, Superstructure.state,
            "Setup: from ALGAE_HOME the L1 button should enter PROCESSOR_SCORE")
        Superstructure.commandedL1 = false
        Superstructure.commandedHome = true
        Superstructure.periodic()
        assertEquals(SuperState.ALGAE_HOME, Superstructure.state,
            "Still holding the algae: home from PROCESSOR_SCORE should return to ALGAE_HOME")
    }

    @Test fun home_after_scoring_the_algae_returns_home() {
        driveToAlgaeHome()
        Superstructure.commandedL1 = true
        Superstructure.periodic()
        assertEquals(SuperState.PROCESSOR_SCORE, Superstructure.state,
            "Setup: from ALGAE_HOME the L1 button should enter PROCESSOR_SCORE")
        Superstructure.commandedL1 = false
        Superstructure.algaeInGripper = false // the ball is in the processor now
        Superstructure.commandedHome = true
        Superstructure.periodic()
        assertEquals(SuperState.HOME, Superstructure.state,
            "The algae is gone: no algae row matches, so home falls through to plain HOME")
        assertEquals(Elevator.Setpoint.HOME, Superstructure.elevator.commandedTarget,
            "Back in HOME the elevator should be commanded to HOME")
    }

    // Pose-table spot check: the driving tests above never assert
    // L3_ALGAE_PICKUP's wrist half, so this pins the full row directly.

    @Test fun l3_algae_pickup_pose_matches_the_table() {
        assertEquals(Elevator.Setpoint.ALGAE_L3, SuperState.L3_ALGAE_PICKUP.elevator,
            "L3_ALGAE_PICKUP's elevator setpoint doesn't match the pose table in task.md")
        assertEquals(Wrist.Setpoint.ALGAE_PICKUP, SuperState.L3_ALGAE_PICKUP.wrist,
            "L3_ALGAE_PICKUP's wrist setpoint doesn't match the pose table in task.md")
    }
}
