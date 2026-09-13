package course.l7t1

import frc.stubs.superstructure.Arm
import frc.stubs.superstructure.Elevator
import frc.stubs.superstructure.Intake

enum class RobotState(
    val elevator: Elevator.State,
    val arm: Arm.State,
    val intake: Intake.Request,
) {
    // The placeholder values below are all (STOWED, STOWED, STOP). That's
    // correct only for STOWED itself. Update the other three to the values
    // listed in task.md.
    STOWED(Elevator.State.STOWED, Arm.State.STOWED, Intake.Request.STOP),
    INTAKE_GROUND(Elevator.State.STOWED, Arm.State.STOWED, Intake.Request.STOP),
    SCORE_L4(Elevator.State.STOWED, Arm.State.STOWED, Intake.Request.STOP),
    CLIMB_PREP(Elevator.State.STOWED, Arm.State.STOWED, Intake.Request.STOP);
}
