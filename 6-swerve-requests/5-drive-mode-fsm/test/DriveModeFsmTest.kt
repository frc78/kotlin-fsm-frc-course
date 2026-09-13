package course.l6t5

import frc.stubs.geometry.Rotation2d

import frc.stubs.swerve.FieldCentric
import frc.stubs.swerve.FieldCentricFacingAngle
import frc.stubs.swerve.RobotCentric
import frc.stubs.swerve.SwerveDriveBrake
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DriveModeFsmTest {

    @BeforeTest fun setUp() {
        DriveModeFsm.reset()
    }

    @Test fun default_state_applies_field_centric() {
        DriveModeFsm.requestedVx = 1.0
        DriveModeFsm.requestedVy = 0.5
        DriveModeFsm.requestedOmega = 0.2
        DriveModeFsm.periodic()
        assertEquals(
            DriveModeFsm.State.TELEOP_FIELD, DriveModeFsm.state,
            "with no command flag set the state must be TELEOP_FIELD"
        )
        assertEquals(
            FieldCentric(velocityX = 1.0, velocityY = 0.5, rotationalRate = 0.2),
            DriveModeFsm.drivetrain.lastRequest,
            "TELEOP_FIELD should send a FieldCentric carrying requestedVx, requestedVy, and requestedOmega"
        )
    }

    @Test fun robot_relative_applies_robot_centric() {
        DriveModeFsm.requestedVx = 1.0
        DriveModeFsm.requestedVy = 0.5
        DriveModeFsm.requestedOmega = 0.3
        DriveModeFsm.commandedRobotRelative = true
        DriveModeFsm.periodic()
        assertEquals(
            DriveModeFsm.State.TELEOP_ROBOT, DriveModeFsm.state,
            "commandedRobotRelative alone should select TELEOP_ROBOT"
        )
        assertEquals(
            RobotCentric(velocityX = 1.0, velocityY = 0.5, rotationalRate = 0.3),
            DriveModeFsm.drivetrain.lastRequest,
            "TELEOP_ROBOT should send a RobotCentric carrying all of requestedVx, requestedVy, and requestedOmega"
        )
    }

    @Test fun aim_applies_facing_angle() {
        DriveModeFsm.requestedVx = 0.5
        DriveModeFsm.requestedVy = -0.4
        DriveModeFsm.commandedAim = true
        DriveModeFsm.aimTargetDegrees = 90.0
        DriveModeFsm.periodic()
        assertEquals(
            DriveModeFsm.State.AIMING, DriveModeFsm.state,
            "commandedAim alone should select AIMING"
        )
        assertEquals(
            FieldCentricFacingAngle(velocityX = 0.5, velocityY = -0.4, targetDirection = Rotation2d.fromDegrees(90.0)),
            DriveModeFsm.drivetrain.lastRequest,
            "AIMING should send a FieldCentricFacingAngle carrying requestedVx, requestedVy, and aimTargetDegrees"
        )
    }

    @Test fun brake_applies_brake() {
        DriveModeFsm.commandedBrake = true
        DriveModeFsm.periodic()
        assertEquals(
            DriveModeFsm.State.BRAKED, DriveModeFsm.state,
            "commandedBrake with every requested velocity at 0.0 should select BRAKED"
        )
        assertEquals(
            SwerveDriveBrake, DriveModeFsm.drivetrain.lastRequest,
            "BRAKED should send the SwerveDriveBrake request"
        )
    }

    @Test fun brake_blocked_while_sticks_move() {
        DriveModeFsm.commandedBrake = true
        DriveModeFsm.requestedVx = 1.0
        DriveModeFsm.requestedVy = -0.3
        DriveModeFsm.periodic()
        assertEquals(
            DriveModeFsm.State.TELEOP_FIELD, DriveModeFsm.state,
            "commandedBrake must not select BRAKED while a requested velocity has magnitude 0.05 or more; with no other flag the state is TELEOP_FIELD"
        )
        assertEquals(
            FieldCentric(velocityX = 1.0, velocityY = -0.3, rotationalRate = 0.0),
            DriveModeFsm.drivetrain.lastRequest,
            "while the brake is blocked the drivetrain should still receive the FieldCentric drive request"
        )
    }

    @Test fun brake_blocked_by_rotation_alone() {
        DriveModeFsm.commandedBrake = true
        DriveModeFsm.requestedOmega = -0.2
        DriveModeFsm.periodic()
        assertEquals(
            DriveModeFsm.State.TELEOP_FIELD, DriveModeFsm.state,
            "requestedOmega counts as a moving stick too; brake must be blocked when its magnitude is 0.05 or more"
        )
    }

    @Test fun brake_wins_over_aim() {
        DriveModeFsm.commandedAim = true
        DriveModeFsm.commandedBrake = true
        DriveModeFsm.periodic()
        assertEquals(
            DriveModeFsm.State.BRAKED, DriveModeFsm.state,
            "brake has priority over aim when the sticks are still"
        )
        assertEquals(
            SwerveDriveBrake, DriveModeFsm.drivetrain.lastRequest,
            "BRAKED should send the SwerveDriveBrake request even though commandedAim is also true"
        )
    }

    @Test fun brake_wins_over_robot_relative() {
        DriveModeFsm.commandedRobotRelative = true
        DriveModeFsm.commandedBrake = true
        DriveModeFsm.periodic()
        assertEquals(
            DriveModeFsm.State.BRAKED, DriveModeFsm.state,
            "brake has priority over robot-relative when the sticks are still"
        )
        assertEquals(
            SwerveDriveBrake, DriveModeFsm.drivetrain.lastRequest,
            "BRAKED should send the SwerveDriveBrake request even though commandedRobotRelative is also true"
        )
    }

    @Test fun aim_wins_over_robot_relative() {
        DriveModeFsm.commandedRobotRelative = true
        DriveModeFsm.commandedAim = true
        DriveModeFsm.aimTargetDegrees = 45.0
        DriveModeFsm.requestedVx = 1.0
        DriveModeFsm.periodic()
        assertEquals(
            DriveModeFsm.State.AIMING, DriveModeFsm.state,
            "aim has priority over robot-relative"
        )
        assertEquals(
            FieldCentricFacingAngle(velocityX = 1.0, targetDirection = Rotation2d.fromDegrees(45.0)),
            DriveModeFsm.drivetrain.lastRequest,
            "AIMING should send a FieldCentricFacingAngle, not a RobotCentric, when both flags are set"
        )
    }

    @Test fun mode_changes_per_tick() {
        DriveModeFsm.requestedVx = 1.0
        DriveModeFsm.periodic()
        assertEquals(
            DriveModeFsm.State.TELEOP_FIELD, DriveModeFsm.state,
            "tick 1: no flags, expect TELEOP_FIELD"
        )

        DriveModeFsm.commandedRobotRelative = true
        DriveModeFsm.periodic()
        assertEquals(
            DriveModeFsm.State.TELEOP_ROBOT, DriveModeFsm.state,
            "tick 2: robot-relative flag set, expect TELEOP_ROBOT"
        )

        DriveModeFsm.commandedRobotRelative = false
        DriveModeFsm.commandedAim = true
        DriveModeFsm.periodic()
        assertEquals(
            DriveModeFsm.State.AIMING, DriveModeFsm.state,
            "tick 3: aim flag set, expect AIMING"
        )

        DriveModeFsm.requestedVx = 0.0
        DriveModeFsm.commandedBrake = true
        DriveModeFsm.periodic()
        assertEquals(
            DriveModeFsm.State.BRAKED, DriveModeFsm.state,
            "tick 4: brake flag set and sticks still, expect BRAKED"
        )
    }
}
