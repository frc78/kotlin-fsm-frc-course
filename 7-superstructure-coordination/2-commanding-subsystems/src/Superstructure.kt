package course.l7t2

import frc.stubs.superstructure.Arm
import frc.stubs.superstructure.Elevator
import frc.stubs.superstructure.Intake

enum class RobotState(
    val elevator: Elevator.State,
    val arm: Arm.State,
    val intake: Intake.Mode,
) {
    STOWED(Elevator.State.STOWED, Arm.State.STOWED, Intake.Mode.IDLE),
    INTAKE_GROUND(Elevator.State.LOW, Arm.State.GROUND, Intake.Mode.INTAKING),
    SCORE_L4(Elevator.State.HIGH, Arm.State.SCORE, Intake.Mode.HOLDING),
    CLIMB_PREP(Elevator.State.STOWED, Arm.State.CLIMB, Intake.Mode.IDLE);
}

class Superstructure {
    val elevator = Elevator()
    val arm = Arm()
    val intake = Intake()

    var commandedRobotState: RobotState = RobotState.STOWED

    fun periodic() {
        stateActions()
        elevator.tick()
        arm.tick()
        intake.tick()
    }

    private fun stateActions() {
        // TODO: push each per-subsystem setpoint from `commandedRobotState`:
        //   elevator.commandedTarget = commandedRobotState.elevator
        //   arm.commandedTarget = commandedRobotState.arm
        //   intake.commandedMode = commandedRobotState.intake
        TODO()
    }
}
