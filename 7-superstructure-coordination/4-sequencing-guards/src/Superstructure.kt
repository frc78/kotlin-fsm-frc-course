package course.l7t4

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

    sealed class Transition {
        data class WaitingForElevator(val target: RobotState) : Transition()
        data class WaitingForArm(val target: RobotState) : Transition()
        data class Settled(val at: RobotState) : Transition()
    }

    var transition: Transition = Transition.Settled(RobotState.STOWED)

    override fun periodic() {
        stateTransitions()
        stateActions()
        elevator.tick()
        arm.tick()
        intake.tick()
    }

    private fun transitionTargetState(): RobotState = when (val t = transition) {
        is Transition.Settled -> t.at
        is Transition.WaitingForElevator -> t.target
        is Transition.WaitingForArm -> t.target
    }

    private fun stateTransitions() {
        // A new goal restarts the sequence. This part is given.
        if (transitionTargetState() != commandedRobotState) {
            transition = Transition.WaitingForElevator(commandedRobotState)
        }
        // TODO: see task.md.
        TODO()
    }

    private fun stateActions() {
        // TODO: see task.md.
        TODO()
    }

    fun atTarget(): Boolean = transition is Transition.Settled
}
