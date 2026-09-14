package course.l7t1

import kotlin.test.Test
import kotlin.test.assertEquals

class PoseTest {
    private fun check(pose: Pose, elevator: Double, arm: Double) {
        assertEquals(elevator, pose.elevatorRotations, "$pose.elevatorRotations should be $elevator (task.md table)")
        assertEquals(arm, pose.armDegrees, "$pose.armDegrees should be $arm (task.md table)")
    }

    @Test fun has_six_poses() {
        assertEquals(6, Pose.entries.size, "the enum should declare exactly six poses")
    }

    @Test fun home() = check(Pose.HOME, 0.0, 90.0)

    @Test fun coral_station() = check(Pose.CORAL_STATION, 2.0, 30.0)

    @Test fun l2() = check(Pose.L2, 4.0, 45.0)

    @Test fun l4() = check(Pose.L4, 14.5, 45.0)

    @Test fun ready_to_climb() = check(Pose.READY_TO_CLIMB, 0.0, 180.0)

    @Test fun fully_climbed() = check(Pose.FULLY_CLIMBED, 0.0, 5.0)
}
