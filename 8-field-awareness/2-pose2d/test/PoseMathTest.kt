package course.l8t2

import frc.stubs.geometry.Pose2d
import frc.stubs.geometry.Rotation2d
import frc.stubs.geometry.Translation2d
import kotlin.test.Test
import kotlin.test.assertEquals

class PoseMathTest {
    private val tol = 1e-9

    @Test fun piece_in_front_of_robot_at_origin_is_at_offset() {
        // Robot at origin facing +x; piece 2m forward.
        val piece = gamePieceFieldPosition(
            robotPose = Pose2d(0.0, 0.0, Rotation2d()),
            pieceInRobotFrame = Translation2d(2.0, 0.0),
        )
        assertEquals(2.0, piece.x, tol)
        assertEquals(0.0, piece.y, tol)
    }

    @Test fun piece_offset_from_robot_in_position() {
        // Robot at (5, 5) facing +x; piece 1m forward.
        val piece = gamePieceFieldPosition(
            robotPose = Pose2d(5.0, 5.0, Rotation2d()),
            pieceInRobotFrame = Translation2d(1.0, 0.0),
        )
        assertEquals(6.0, piece.x, tol)
        assertEquals(5.0, piece.y, tol)
    }

    @Test fun piece_in_front_when_robot_facing_ninety() {
        // Robot at origin facing +y (90°); piece 1m forward in robot frame
        // should be 1m along +y in field frame.
        val piece = gamePieceFieldPosition(
            robotPose = Pose2d(0.0, 0.0, Rotation2d.fromDegrees(90.0)),
            pieceInRobotFrame = Translation2d(1.0, 0.0),
        )
        assertEquals(0.0, piece.x, tol)
        assertEquals(1.0, piece.y, tol)
    }

    @Test fun opponent_directly_in_front_relative_to_me() {
        val me = Pose2d(0.0, 0.0, Rotation2d())
        val opponent = Pose2d(3.0, 0.0, Rotation2d())
        val rel = opponentRelativeToMe(me, opponent)
        assertEquals(3.0, rel.x, tol)
        assertEquals(0.0, rel.y, tol)
    }

    @Test fun opponent_to_my_left_when_im_facing_zero() {
        val me = Pose2d(0.0, 0.0, Rotation2d())
        val opponent = Pose2d(0.0, 2.0, Rotation2d())
        val rel = opponentRelativeToMe(me, opponent)
        assertEquals(0.0, rel.x, tol)
        assertEquals(2.0, rel.y, tol)
    }

    @Test fun opponent_in_front_of_me_when_im_facing_ninety() {
        // I'm at origin facing +y. Opponent is at (0, 5) on the field.
        // From my frame, that's 5m forward.
        val me = Pose2d(0.0, 0.0, Rotation2d.fromDegrees(90.0))
        val opponent = Pose2d(0.0, 5.0, Rotation2d.fromDegrees(90.0))
        val rel = opponentRelativeToMe(me, opponent)
        assertEquals(5.0, rel.x, tol)
        assertEquals(0.0, rel.y, tol)
    }
}
