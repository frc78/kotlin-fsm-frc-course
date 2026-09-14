package course.l7t3

import frc.stubs.OI
import frc.stubs.superstructure.Arm
import frc.stubs.superstructure.Elevator
import kotlin.math.abs
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

    private fun commandL4() {
        Intake.state = Intake.State.HOLDING
        OI.scoreL4 = true
    }

    private fun runUntilSettled(maxTicks: Int = 12) {
        repeat(maxTicks) {
            SuperStructure.periodic()
            if (SuperStructure.atPosition) return
        }
    }

    // Tick 1: state -> L4. Up: arm commanded 45, arm at 90 so no elevator call.
    // Tick 2: arm arrived at 45, so the elevator is commanded 14.5.
    @Test fun going_up_moves_the_arm_first() {
        commandL4()
        SuperStructure.periodic()
        assertEquals(Pose.L4, SuperStructure.state, "HOME with a held piece and scoreL4 should go to L4")
        assertEquals(45.0, Arm.target, "going up, the arm is commanded to the pose angle on the first tick")
        assertEquals(0.0, Elevator.target, "going up, the elevator must not be commanded until the arm is within 2 degrees")
        SuperStructure.periodic()
        assertEquals(14.5, Elevator.target, "once the arm is within 2 degrees, the elevator is commanded to the pose height")
    }

    // Arm 1 tick, elevator 5 ticks after that: settled by tick 7.
    @Test fun going_up_settles_at_l4() {
        commandL4()
        runUntilSettled()
        assertTrue(SuperStructure.atPosition, "L4 should be reached within 12 ticks")
        assertEquals(14.5, Elevator.position, "elevator should be at 14.5 rotations at L4")
        assertEquals(45.0, Arm.angle, "arm should be at 45 degrees at L4")
    }

    // From settled L4, command L2 (down 10.5 rotations).
    // Tick 1: elevator commanded 4.0, arm commanded to the safe 90.
    // Elevator 14.5 -> 11.5 -> 8.5 -> 5.5 -> 4.0 (arrives on tick 4, arm still 90).
    // Tick 5: within 0.5, arm commanded 45.
    @Test fun going_down_moves_the_elevator_first_with_the_arm_safe() {
        commandL4()
        runUntilSettled()
        OI.scoreL4 = false
        OI.scoreL2 = true
        SuperStructure.periodic()
        assertEquals(Pose.L2, SuperStructure.state, "L4 with a held piece and scoreL2 should go to L2")
        assertEquals(4.0, Elevator.target, "going down, the elevator is commanded to the pose height on the first tick")
        assertEquals(90.0, Arm.target, "going down, the arm is commanded to SAFE_ARM_DEGREES while the elevator is far from its target")
        var ticks = 0
        while (abs(Elevator.position - 4.0) >= 0.5 && ticks < 10) {
            SuperStructure.periodic()
            ticks++
        }
        assertEquals(90.0, Arm.target, "the arm must still be at the safe angle on the tick the elevator arrives")
        SuperStructure.periodic()
        assertEquals(45.0, Arm.target, "once the elevator is within 0.5 rotations, the arm is commanded to the pose angle")
    }

    // Same height, so the "otherwise" row applies: arm first, elevator target unchanged.
    @Test fun level_move_commands_the_arm_and_holds_the_elevator() {
        OI.prepareClimb = true
        SuperStructure.periodic()
        assertEquals(Pose.READY_TO_CLIMB, SuperStructure.state, "HOME with prepareClimb should go to READY_TO_CLIMB")
        assertEquals(180.0, Arm.target, "a level move commands the arm to the pose angle at once")
        assertEquals(0.0, Elevator.target, "a level move leaves the elevator target unchanged until the arm arrives")
        runUntilSettled()
        assertTrue(SuperStructure.atPosition, "READY_TO_CLIMB should settle: arm 90 -> 135 -> 180")
    }

    // Ticks 1-3 toward L4: arm 45, elevator target 14.5, position 0 -> 3 -> 6.
    // Tick 4 with home: state HOME, 0 < 6 so going down: elevator 0, arm safe 90.
    @Test fun reversal_mid_flight_goes_down_with_the_arm_safe() {
        commandL4()
        repeat(3) { SuperStructure.periodic() }
        assertEquals(6.0, Elevator.position, "after 3 ticks the elevator should be mid-flight at 6.0 rotations")
        OI.scoreL4 = false
        OI.home = true
        SuperStructure.periodic()
        assertEquals(Pose.HOME, SuperStructure.state, "home should take priority in L4")
        assertEquals(0.0, Elevator.target, "reversing mid-flight is a down move: elevator commanded to 0.0")
        assertEquals(90.0, Arm.target, "reversing mid-flight is a down move: arm commanded to SAFE_ARM_DEGREES")
        assertFalse(SuperStructure.atPosition, "the elevator is still on its way back down")
        runUntilSettled()
        assertTrue(SuperStructure.atPosition, "HOME should be reached after the reversal")
    }

    @Test fun settled_home_keeps_commanding_home() {
        SuperStructure.periodic()
        assertEquals(0.0, Elevator.target, "at HOME the elevator target is 0.0")
        assertEquals(90.0, Arm.target, "at HOME the arm target is 90.0")
        assertTrue(SuperStructure.atPosition, "the robot starts settled at HOME")
    }
}
