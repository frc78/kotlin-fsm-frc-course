package course.l9t6

import frc.stubs.geometry.Pose2d
import frc.stubs.geometry.Rotation2d
import frc.stubs.swerve.FieldCentric
import frc.stubs.swerve.FieldCentricFacingAngle
import frc.stubs.swerve.RobotCentric
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DriveBaseTest {

    @BeforeTest fun setUp() {
        DriveBase.reset()
    }

    @Test fun starts_in_field_drive_passing_sticks_through() {
        DriveBase.stickVx = 1.5
        DriveBase.stickVy = 0.5
        DriveBase.stickOmega = 2.0
        DriveBase.periodic()
        assertEquals(
            DriveBase.State.FIELD_DRIVE, DriveBase.state,
            "with no buttons held, the drivebase should stay in FIELD_DRIVE"
        )
        assertEquals(
            FieldCentric(velocityX = 1.5, velocityY = 0.5, rotationalRate = 2.0),
            DriveBase.drivetrain.lastRequest,
            "FIELD_DRIVE should send a FieldCentric carrying stickVx, stickVy, and stickOmega"
        )
    }

    @Test fun right_stick_click_switches_to_robot_drive() {
        DriveBase.stickVx = 1.0
        DriveBase.stickVy = 0.5
        DriveBase.stickOmega = 0.25
        DriveBase.commandedRobotDrive = true
        DriveBase.periodic()
        assertEquals(
            DriveBase.State.ROBOT_DRIVE, DriveBase.state,
            "commandedRobotDrive from FIELD_DRIVE should switch to ROBOT_DRIVE"
        )
        assertEquals(
            RobotCentric(velocityX = 1.0, velocityY = 0.5, rotationalRate = 0.25),
            DriveBase.drivetrain.lastRequest,
            "ROBOT_DRIVE should send a RobotCentric carrying stickVx, stickVy, and stickOmega"
        )
    }

    @Test fun robot_drive_latches_until_left_stick_click() {
        DriveBase.commandedRobotDrive = true
        DriveBase.periodic()
        DriveBase.commandedRobotDrive = false
        DriveBase.periodic()
        assertEquals(
            DriveBase.State.ROBOT_DRIVE, DriveBase.state,
            "R3 is a click, not a hold: releasing it should NOT leave ROBOT_DRIVE — only L3 does"
        )
        DriveBase.commandedFieldDrive = true
        DriveBase.stickVx = 1.0
        DriveBase.periodic()
        assertEquals(
            DriveBase.State.FIELD_DRIVE, DriveBase.state,
            "commandedFieldDrive (L3) should return ROBOT_DRIVE to FIELD_DRIVE"
        )
        assertEquals(
            FieldCentric(velocityX = 1.0),
            DriveBase.drivetrain.lastRequest,
            "back in FIELD_DRIVE the request should be FieldCentric again"
        )
    }

    @Test fun intake_assist_lets_vision_own_the_sideways_axis() {
        DriveBase.commandedIntakeAssist = true
        DriveBase.stickVx = 1.0
        DriveBase.stickVy = 0.75 // deliberately nonzero: ASSISTED_DRIVE must ignore it
        DriveBase.stickOmega = 0.5
        DriveBase.coralOffsetDegrees = 10.0
        DriveBase.periodic()
        assertEquals(
            DriveBase.State.ASSISTED_DRIVE, DriveBase.state,
            "holding the intake button in FIELD_DRIVE should enter ASSISTED_DRIVE"
        )
        assertEquals(
            RobotCentric(velocityX = 1.0, velocityY = 0.3, rotationalRate = 0.5),
            DriveBase.drivetrain.lastRequest,
            "in ASSISTED_DRIVE vision owns the Y axis: velocityY = 0.03 * coralOffsetDegrees = 0.3, not the driver's stickVy"
        )
    }

    @Test fun releasing_the_intake_button_returns_to_field_drive() {
        DriveBase.commandedIntakeAssist = true
        DriveBase.periodic()
        DriveBase.commandedIntakeAssist = false
        DriveBase.periodic()
        assertEquals(
            DriveBase.State.FIELD_DRIVE, DriveBase.state,
            "releasing the intake button should drop ASSISTED_DRIVE back to FIELD_DRIVE"
        )
    }

    @Test fun auto_align_without_a_visible_tag_stays_in_field_drive() {
        DriveBase.commandedAutoAlign = true
        DriveBase.tagVisible = false
        DriveBase.stickVx = 1.0
        DriveBase.stickVy = 0.5
        DriveBase.stickOmega = 0.25
        DriveBase.periodic()
        assertEquals(
            DriveBase.State.FIELD_DRIVE, DriveBase.state,
            "auto-align requires a visible tag to start: with no tag the FSM must stay in FIELD_DRIVE"
        )
        assertEquals(
            FieldCentric(velocityX = 1.0, velocityY = 0.5, rotationalRate = 0.25),
            DriveBase.drivetrain.lastRequest,
            "with no tag the driver keeps normal field-centric control"
        )
    }

    @Test fun auto_align_with_a_tag_drives_to_the_pole() {
        val pole = Pose2d(2.0, 2.5, Rotation2d.fromDegrees(60.0))
        DriveBase.commandedAutoAlign = true
        DriveBase.tagVisible = true
        DriveBase.robotPose = Pose2d(1.0, 3.0, Rotation2d.fromDegrees(0.0))
        DriveBase.targetPole = pole
        DriveBase.stickVx = 1.0 // sticks must all be ignored while aligning
        DriveBase.stickVy = 1.0
        DriveBase.stickOmega = 1.0
        DriveBase.periodic()
        assertEquals(
            DriveBase.State.AUTO_ALIGN, DriveBase.state,
            "align button + visible tag should enter AUTO_ALIGN"
        )
        assertEquals(
            FieldCentricFacingAngle(
                velocityX = 4.0,
                velocityY = -2.0,
                targetDirection = pole.rotation.degrees,
            ),
            DriveBase.drivetrain.lastRequest,
            "AUTO_ALIGN is a P controller: velocity = 4.0 * (pole - robot) per axis, heading locked to the pole's angle, sticks ignored"
        )
    }

    @Test fun losing_the_tag_mid_align_does_not_abort() {
        DriveBase.commandedAutoAlign = true
        DriveBase.tagVisible = true
        DriveBase.periodic()
        DriveBase.tagVisible = false
        DriveBase.periodic()
        assertEquals(
            DriveBase.State.AUTO_ALIGN, DriveBase.state,
            "the tag is required to START auto-align, not to continue it: only releasing the button exits"
        )
    }

    @Test fun releasing_the_align_button_returns_to_field_drive() {
        DriveBase.commandedAutoAlign = true
        DriveBase.tagVisible = true
        DriveBase.periodic()
        DriveBase.commandedAutoAlign = false
        DriveBase.periodic()
        assertEquals(
            DriveBase.State.FIELD_DRIVE, DriveBase.state,
            "releasing the level button should drop AUTO_ALIGN back to FIELD_DRIVE"
        )
    }

    @Test fun auto_align_beats_intake_assist_when_both_held() {
        DriveBase.commandedAutoAlign = true
        DriveBase.tagVisible = true
        DriveBase.commandedIntakeAssist = true
        DriveBase.periodic()
        assertEquals(
            DriveBase.State.AUTO_ALIGN, DriveBase.state,
            "with align (tag visible) and assist both held, AUTO_ALIGN is the higher-priority transition"
        )
    }
}
