package course.l8t2

import frc.stubs.geometry.Pose2d
import frc.stubs.geometry.Translation2d

fun gamePieceFieldPosition(
    robotPose: Pose2d,
    pieceInRobotFrame: Translation2d,
): Translation2d {
    // TODO: rotate the piece offset by the robot's heading, then translate.
    //   pieceInRobotFrame.rotateBy(robotPose.rotation) + robotPose.translation
    TODO()
}

fun opponentRelativeToMe(myPose: Pose2d, opponentPose: Pose2d): Pose2d {
    // TODO: opponentPose.relativeTo(myPose)
    TODO()
}
