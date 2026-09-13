package course.l7t5

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
        data class RetractArm(val target: RobotState) : Transition()
        data class MoveElevator(val target: RobotState) : Transition()
        data class ExtendArm(val target: RobotState) : Transition()
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

    private fun stateTransitions() {
        // A new goal restarts the transition from wherever the mechanisms are now.
        if (transitionTargetState() != commandedRobotState) {
            transition = startTransition(commandedRobotState)
        }
        // TODO: see task.md.
        TODO()
    }

    private fun stateActions() {
        // TODO: see task.md.
        TODO()
    }

    fun atTarget(): Boolean = transition is Transition.Settled

    private fun transitionTargetState(): RobotState = when (val t = transition) {
        is Transition.Settled -> t.at
        is Transition.RetractArm -> t.target
        is Transition.MoveElevator -> t.target
        is Transition.ExtendArm -> t.target
    }

    // "Settled at X" means the mechanism has stopped moving AND it stopped at X.
    // A mechanism in flight is settled nowhere.
    private fun elevatorSettledAt(s: Elevator.State) = elevator.atTarget() && elevator.state == s
    private fun armSettledAt(s: Arm.State) = arm.atTarget() && arm.state == s
    private fun intakeSettledAt(r: Intake.Request) = intake.requestReached() && intake.request == r

    // Decides which phase to begin in, based on what is already done.
    private fun startTransition(target: RobotState): Transition = when {
        !armSettledAt(Arm.State.STOWED) && !elevatorSettledAt(target.elevator) ->
            Transition.RetractArm(target)
        !elevatorSettledAt(target.elevator) ->
            Transition.MoveElevator(target)
        !armSettledAt(target.arm) || !intakeSettledAt(target.intake) ->
            Transition.ExtendArm(target)
        else ->
            Transition.Settled(target)
    }
}
