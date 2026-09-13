package course.l7t2

import frc.stubs.Subsystem
import frc.stubs.superstructure.Arm
import frc.stubs.superstructure.Elevator
import frc.stubs.superstructure.Intake

enum class RobotState(
    val elevator: Elevator.State,
    val arm: Arm.State,
    val intake: Intake.Request,
) {
    STOWED(Elevator.State.STOWED, Arm.State.STOWED, Intake.Request.STOP),
    INTAKE_GROUND(Elevator.State.LOW, Arm.State.GROUND, Intake.Request.INTAKE),
    SCORE_L4(Elevator.State.HIGH, Arm.State.SCORE, Intake.Request.STOP),
    CLIMB_PREP(Elevator.State.STOWED, Arm.State.CLIMB, Intake.Request.STOP);
}

class Superstructure : Subsystem {
    val elevator = Elevator()
    val arm = Arm()
    val intake = Intake()

    var commandedRobotState: RobotState = RobotState.STOWED

    override fun periodic() {
        stateActions()
        elevator.tick()
        arm.tick()
        intake.tick()
    }

    private fun stateActions() {
        // TODO: see task.md.
        TODO()
    }
}
