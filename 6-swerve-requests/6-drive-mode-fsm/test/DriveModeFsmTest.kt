package course.l6t6

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
        assertEquals(DriveModeFsm.State.TELEOP_FIELD, DriveModeFsm.state)
        assertEquals(
            FieldCentric(velocityX = 1.0, velocityY = 0.5, rotationalRate = 0.2),
            DriveModeFsm.drivetrain.lastRequest
        )
    }

    @Test fun robot_relative_applies_robot_centric() {
        DriveModeFsm.requestedVx = 1.0
        DriveModeFsm.requestedVy = 0.0
        DriveModeFsm.requestedOmega = 0.0
        DriveModeFsm.commandedRobotRelative = true
        DriveModeFsm.periodic()
        assertEquals(DriveModeFsm.State.TELEOP_ROBOT, DriveModeFsm.state)
        assertEquals(
            RobotCentric(velocityX = 1.0),
            DriveModeFsm.drivetrain.lastRequest
        )
    }

    @Test fun aim_applies_facing_angle() {
        DriveModeFsm.requestedVx = 0.5
        DriveModeFsm.requestedVy = 0.0
        DriveModeFsm.commandedAim = true
        DriveModeFsm.aimTargetDegrees = 90.0
        DriveModeFsm.periodic()
        assertEquals(DriveModeFsm.State.AIMING, DriveModeFsm.state)
        assertEquals(
            FieldCentricFacingAngle(velocityX = 0.5, velocityY = 0.0, targetDirection = 90.0),
            DriveModeFsm.drivetrain.lastRequest
        )
    }

    @Test fun brake_applies_brake() {
        DriveModeFsm.commandedBrake = true
        DriveModeFsm.periodic()
        assertEquals(DriveModeFsm.State.BRAKED, DriveModeFsm.state)
        assertEquals(SwerveDriveBrake, DriveModeFsm.drivetrain.lastRequest)
    }

    @Test fun brake_wins_over_aim() {
        DriveModeFsm.commandedAim = true
        DriveModeFsm.commandedBrake = true
        DriveModeFsm.periodic()
        assertEquals(DriveModeFsm.State.BRAKED, DriveModeFsm.state)
        assertEquals(SwerveDriveBrake, DriveModeFsm.drivetrain.lastRequest)
    }

    @Test fun aim_wins_over_robot_relative() {
        DriveModeFsm.commandedRobotRelative = true
        DriveModeFsm.commandedAim = true
        DriveModeFsm.aimTargetDegrees = 45.0
        DriveModeFsm.requestedVx = 1.0
        DriveModeFsm.periodic()
        assertEquals(DriveModeFsm.State.AIMING, DriveModeFsm.state)
        assertEquals(
            FieldCentricFacingAngle(velocityX = 1.0, targetDirection = 45.0),
            DriveModeFsm.drivetrain.lastRequest
        )
    }

    @Test fun mode_changes_per_tick() {
        DriveModeFsm.requestedVx = 1.0
        DriveModeFsm.periodic()
        assertEquals(DriveModeFsm.State.TELEOP_FIELD, DriveModeFsm.state)

        DriveModeFsm.commandedRobotRelative = true
        DriveModeFsm.periodic()
        assertEquals(DriveModeFsm.State.TELEOP_ROBOT, DriveModeFsm.state)

        DriveModeFsm.commandedRobotRelative = false
        DriveModeFsm.commandedAim = true
        DriveModeFsm.periodic()
        assertEquals(DriveModeFsm.State.AIMING, DriveModeFsm.state)

        DriveModeFsm.commandedBrake = true
        DriveModeFsm.periodic()
        assertEquals(DriveModeFsm.State.BRAKED, DriveModeFsm.state)
    }
}
