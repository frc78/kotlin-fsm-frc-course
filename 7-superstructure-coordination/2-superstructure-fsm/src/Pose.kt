package course.l7t2

// One pose = one coherent position of the elevator and the arm. Solved in task 1.
enum class Pose(val elevatorRotations: Double, val armDegrees: Double) {
    HOME(0.0, 90.0),
    CORAL_STATION(2.0, 30.0),
    L2(4.0, 45.0),
    L4(14.5, 45.0),
    READY_TO_CLIMB(0.0, 180.0),
    FULLY_CLIMBED(0.0, 5.0);
}
