package course.l9t8

import frc.stubs.geometry.Pose2d
import frc.stubs.geometry.Rotation2d
import frc.stubs.lightning.Elevator
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LightningTest {
    @BeforeTest fun setUp() {
        Lightning.reset()
    }

    @Test fun everything_starts_stowed_empty_and_dark() {
        Lightning.periodic()
        assertEquals(MiniIntake.State.STOWED, MiniIntake.state,
            "at kickoff the intake should be STOWED")
        assertEquals(MiniGripper.State.EMPTY, MiniGripper.state,
            "at kickoff the gripper should be EMPTY")
        assertEquals(MiniSuperstructure.State.HOME, MiniSuperstructure.state,
            "at kickoff the superstructure should be HOME")
        assertEquals(MiniDriveBase.State.FIELD_DRIVE, MiniDriveBase.state,
            "at kickoff the drivebase should be in FIELD_DRIVE")
        assertEquals(LedColor.OFF, Lightning.ledColor(),
            "no coral, no auto-align, not ready to score — the LEDs should be OFF")
    }

    @Test fun gripper_state_reaches_superstructure_on_the_same_tick() {
        MiniGripper.cradleBeam.simulateValue(true)
        Lightning.periodic()
        assertEquals(MiniGripper.State.HANDOFF, MiniGripper.state,
            "the cradle beam break should start the handoff automatically")
        MiniGripper.gripperBeam.simulateValue(true)
        MiniSuperstructure.commandedL4 = true
        Lightning.periodic()
        assertEquals(MiniSuperstructure.State.L4_SCORE, MiniSuperstructure.state,
            "the gripper has the coral but the superstructure doesn't know — wire " +
                "coralInGripper from MiniGripper.hasCoral() in Lightning.periodic(), after " +
                "MiniGripper's update and before MiniSuperstructure's")
    }

    @Test fun l4_without_coral_stays_home() {
        MiniSuperstructure.commandedL4 = true
        Lightning.periodic()
        Lightning.periodic()
        assertEquals(MiniSuperstructure.State.HOME, MiniSuperstructure.state,
            "the gripper is EMPTY, so the wired coralInGripper guard must keep the superstructure HOME")
    }

    @Test fun led_white_when_holding_coral() {
        MiniGripper.cradleBeam.simulateValue(true)
        Lightning.periodic()
        MiniGripper.gripperBeam.simulateValue(true)
        Lightning.periodic()
        assertEquals(LedColor.WHITE, Lightning.ledColor(),
            "coral aboard and nothing else going on — solid WHITE (binder p.25)")
    }

    @Test fun led_red_wins_over_white_during_auto_align() {
        MiniGripper.cradleBeam.simulateValue(true)
        Lightning.periodic()
        MiniGripper.gripperBeam.simulateValue(true)
        Lightning.periodic()
        MiniDriveBase.commandedAutoAlign = true
        MiniDriveBase.tagVisible = true
        MiniDriveBase.robotPose = Pose2d(5.0, 5.0, Rotation2d.fromDegrees(0.0))
        Lightning.periodic()
        assertEquals(MiniDriveBase.State.AUTO_ALIGN, MiniDriveBase.state,
            "align held with a tag visible should put the drivebase in AUTO_ALIGN")
        assertEquals(LedColor.RED, Lightning.ledColor(),
            "holding a coral AND auto-aligning — RED outranks WHITE in the priority table")
    }

    @Test fun ready_to_score_false_without_l4_score_selected() {
        Lightning.periodic()
        assertFalse(Lightning.readyToScore(),
            "mechanisms settled and the robot happens to be at the pole, but no L4_SCORE " +
                "selected — readyToScore() must check the superstructure state")
    }

    @Test fun ready_to_score_false_while_mechanisms_still_moving() {
        MiniSuperstructure.coralInGripper = true
        MiniSuperstructure.commandedL4 = true
        MiniSuperstructure.periodic()
        assertEquals(MiniSuperstructure.State.L4_SCORE, MiniSuperstructure.state,
            "L4 with a coral should select L4_SCORE")
        assertFalse(Lightning.readyToScore(),
            "L4_SCORE selected and at the pole, but the elevator is still travelling — " +
                "readyToScore() must check atTarget()")
    }

    @Test fun ready_to_score_false_when_not_at_pole() {
        MiniSuperstructure.coralInGripper = true
        MiniSuperstructure.commandedL4 = true
        repeat(4) { MiniSuperstructure.periodic() }
        assertTrue(MiniSuperstructure.atTarget(),
            "after four loops the elevator and wrist should be settled at L4")
        MiniDriveBase.robotPose = Pose2d(5.0, 5.0, Rotation2d.fromDegrees(0.0))
        assertFalse(Lightning.readyToScore(),
            "L4 raised and settled, but LIGHTNING is meters from the pole — " +
                "readyToScore() must check atPole()")
    }

    @Test fun full_coral_cycle() {
        // Kickoff.
        Lightning.periodic()
        assertEquals(LedColor.OFF, Lightning.ledColor(),
            "kickoff: no coral, no align — the LEDs start dark")

        // The driver holds intake deploy; the slap-down drops to the carpet.
        MiniIntake.commandedDeploy = true
        Lightning.periodic()
        assertEquals(MiniIntake.State.INTAKING, MiniIntake.state,
            "deploy held: the slap-down intake should be INTAKING")

        // A coral trips the intake beam break — deploy is STILL held.
        MiniIntake.beamBreak.simulateValue(true)
        Lightning.periodic()
        assertEquals(MiniIntake.State.STOWED, MiniIntake.state,
            "coral acquired: the intake should auto-retract even though deploy is still held")

        // The coral rolls on into the Coral Cradle.
        MiniIntake.commandedDeploy = false
        MiniIntake.beamBreak.simulateValue(false)
        MiniGripper.cradleBeam.simulateValue(true)
        Lightning.periodic()
        assertEquals(MiniGripper.State.HANDOFF, MiniGripper.state,
            "cradle beam break: the handoff should start by itself — nobody pressed anything")

        // The gripper's own beam break confirms possession.
        MiniGripper.gripperBeam.simulateValue(true)
        MiniGripper.cradleBeam.simulateValue(false)
        Lightning.periodic()
        assertEquals(MiniGripper.State.HOLDING_CORAL, MiniGripper.state,
            "gripper beam break: LIGHTNING has the coral")
        assertEquals(LedColor.WHITE, Lightning.ledColor(),
            "coral aboard: the LEDs go solid WHITE so the driver knows")

        // The driver presses and holds L4 with an AprilTag in view; the robot
        // is still ~2 m from the reef pole.
        MiniSuperstructure.commandedL4 = true
        MiniDriveBase.commandedAutoAlign = true
        MiniDriveBase.tagVisible = true
        MiniDriveBase.targetPole = Pose2d(2.0, 1.0, Rotation2d.fromDegrees(60.0))
        Lightning.periodic()
        assertEquals(MiniSuperstructure.State.L4_SCORE, MiniSuperstructure.state,
            "L4 held with a coral aboard: the superstructure should head for L4_SCORE — " +
                "if it stayed HOME, check the coralInGripper wiring in periodic()")
        assertEquals(MiniDriveBase.State.AUTO_ALIGN, MiniDriveBase.state,
            "align held with a tag visible: the drivebase should be in AUTO_ALIGN")
        assertEquals(LedColor.RED, Lightning.ledColor(),
            "while lining up, RED outranks the coral's WHITE")
        assertFalse(Lightning.readyToScore(),
            "elevator still travelling and robot off the pole — not ready yet")

        // Two more loops: the elevator finishes its travel; the drive hasn't arrived.
        Lightning.periodic()
        Lightning.periodic()
        assertTrue(MiniSuperstructure.atTarget(),
            "after three loops at L4 the elevator and wrist should be settled")
        assertFalse(Lightning.readyToScore(),
            "mechanisms settled but LIGHTNING is still driving — not ready until atPole()")

        // Auto-align delivers the robot to within 2 cm of the pole.
        MiniDriveBase.robotPose = Pose2d(1.98, 1.0, Rotation2d.fromDegrees(60.0))
        Lightning.periodic()
        assertTrue(Lightning.readyToScore(),
            "L4_SCORE + atTarget + atPole: everything is lined up — ready to score")
        assertEquals(LedColor.BLUE, Lightning.ledColor(),
            "ready to score: solid BLUE (and the driver's controller rumbles)")

        // Score! The gripper never opens — the whole mechanism dunks the coral
        // onto the branch.
        MiniSuperstructure.commandedScore = true
        Lightning.periodic()
        assertEquals(MiniSuperstructure.State.L4_DUNK, MiniSuperstructure.state,
            "Score pressed: the superstructure should dunk, driving the mechanism DOWN " +
                "to jam the coral onto the branch")
        assertEquals(Elevator.Setpoint.L4_DUNK, MiniSuperstructure.elevator.commandedTarget,
            "the dunk drops the elevator target from L4 to L4_DUNK")
        assertEquals(LedColor.RED, Lightning.ledColor(),
            "coral placed, no longer ready to score — back to RED while align is still held")
    }
}
