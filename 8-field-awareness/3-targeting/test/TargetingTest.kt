package course.l8t3

import frc.stubs.geometry.Pose2d
import frc.stubs.geometry.Rotation2d
import frc.stubs.geometry.Translation2d
import frc.stubs.swerve.FieldCentricFacingAngle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TargetingTest {
    private val tol = 1e-6

    @Test fun aim_at_goal_directly_along_x() {
        val request = aimAtGoal(
            robotPose = Pose2d(0.0, 0.0, Rotation2d.fromDegrees(45.0)),
            goalPosition = Translation2d(5.0, 0.0),
            vx = 0.0, vy = 0.0,
        )
        val r = assertIs<FieldCentricFacingAngle>(request)
        assertEquals(0.0, r.targetDirection.degrees, tol)
    }

    @Test fun aim_at_goal_directly_along_y() {
        val request = aimAtGoal(
            robotPose = Pose2d(0.0, 0.0, Rotation2d()),
            goalPosition = Translation2d(0.0, 5.0),
            vx = 0.0, vy = 0.0,
        )
        val r = assertIs<FieldCentricFacingAngle>(request)
        assertEquals(90.0, r.targetDirection.degrees, tol)
    }

    @Test fun aim_passes_translation_through() {
        val request = aimAtGoal(
            robotPose = Pose2d(2.0, 0.0, Rotation2d()),
            goalPosition = Translation2d(5.0, 0.0),
            vx = 1.5, vy = -0.7,
        )
        val r = assertIs<FieldCentricFacingAngle>(request)
        assertEquals(1.5, r.velocityX, tol)
        assertEquals(-0.7, r.velocityY, tol)
        assertEquals(0.0, r.targetDirection.degrees, tol)
    }

    @Test fun aim_at_goal_behind_robot() {
        val request = aimAtGoal(
            robotPose = Pose2d(5.0, 0.0, Rotation2d()),
            goalPosition = Translation2d(0.0, 0.0),
            vx = 0.0, vy = 0.0,
        )
        val r = assertIs<FieldCentricFacingAngle>(request)
        assertEquals(180.0, r.targetDirection.degrees, tol, "goal straight behind the robot is at 180°")
    }

    @Test fun aim_when_robot_offset_from_origin() {
        // Robot at (1, 1), goal at (4, 5). Delta = (3, 4). Heading = atan2(4, 3) ≈ 53.13°.
        val request = aimAtGoal(
            robotPose = Pose2d(1.0, 1.0, Rotation2d()),
            goalPosition = Translation2d(4.0, 5.0),
            vx = 0.0, vy = 0.0,
        )
        val r = assertIs<FieldCentricFacingAngle>(request)
        assertEquals(53.130102, r.targetDirection.degrees, 1e-4)
    }
}
