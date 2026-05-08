package course.l8t3

import frc.stubs.geometry.Pose2d
import frc.stubs.geometry.Translation2d
import frc.stubs.swerve.FieldCentricFacingAngle
import frc.stubs.swerve.SwerveRequest

fun aimAtGoal(
    robotPose: Pose2d,
    goalPosition: Translation2d,
    vx: Double,
    vy: Double,
): SwerveRequest {
    // TODO:
    //   1. Compute target heading FROM robotPose.translation TO goalPosition.
    //      val targetHeading = (goalPosition - robotPose.translation).getAngle()
    //
    //   2. Build a FieldCentricFacingAngle with vx, vy, and targetHeading.degrees.
    //      FieldCentricFacingAngle()
    //          .withVelocityX(vx)
    //          .withVelocityY(vy)
    //          .withTargetDirection(targetHeading.degrees)
    TODO()
}
