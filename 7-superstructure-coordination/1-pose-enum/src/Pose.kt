package course.l7t1

// One pose = one coherent position of the mechanisms that move together.
// TODO: see task.md.
enum class Pose(val elevatorRotations: Double, val armDegrees: Double) {
    HOME(0.0, 90.0),
    CORAL_STATION(0.0, 90.0),
    L2(0.0, 90.0),
    L4(0.0, 90.0),
    READY_TO_CLIMB(0.0, 90.0),
    FULLY_CLIMBED(0.0, 90.0);
}
