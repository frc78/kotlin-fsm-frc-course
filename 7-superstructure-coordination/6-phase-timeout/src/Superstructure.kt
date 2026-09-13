package course.l7t6

import frc.stubs.Subsystem
import frc.stubs.Timer
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

    // Watchdog for the current phase. See task.md.
    val phaseTimer = Timer()
    var faulted: Boolean = false
    val phaseTimeoutSeconds = 2.0

    override fun periodic() {
        stateTransitions()
        stateActions()
        elevator.tick()
        arm.tick()
        intake.tick()
    }

    private fun stateTransitions() {
        if (transitionTargetState() != commandedRobotState) {
            enterPhase(startTransition(commandedRobotState))
        }
        checkTimeout()
        when (val t = transition) {
            is Transition.RetractArm ->
                if (armSettledAt(Arm.State.STOWED)) enterPhase(startTransition(t.target))
            is Transition.MoveElevator ->
                if (elevatorSettledAt(t.target.elevator)) enterPhase(startTransition(t.target))
            is Transition.ExtendArm ->
                if (armSettledAt(t.target.arm) && intakeSettledAt(t.target.intake)) {
                    enterPhase(startTransition(t.target))
                }
            is Transition.Settled -> {}
        }
    }

    private fun stateActions() {
        when (val t = transition) {
            is Transition.RetractArm -> {
                arm.commandedTarget = Arm.State.STOWED
            }
            is Transition.MoveElevator -> {
                elevator.commandedTarget = t.target.elevator
            }
            is Transition.ExtendArm -> {
                arm.commandedTarget = t.target.arm
                intake.request = t.target.intake
            }
            is Transition.Settled -> {
                elevator.commandedTarget = t.at.elevator
                arm.commandedTarget = t.at.arm
                intake.request = t.at.intake
            }
        }
    }

    private fun transitionTargetState(): RobotState = when (val t = transition) {
        is Transition.Settled -> t.at
        is Transition.RetractArm -> t.target
        is Transition.MoveElevator -> t.target
        is Transition.ExtendArm -> t.target
    }

    // A mechanism in flight is not at any state. "Settled at X" needs both.
    private fun elevatorSettledAt(s: Elevator.State) = elevator.atTarget() && elevator.state == s
    private fun armSettledAt(s: Arm.State) = arm.atTarget() && arm.state == s
    private fun intakeSettledAt(r: Intake.Request) = intake.requestReached() && intake.request == r

    // Decides which phase to begin in, from what is already settled.
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

    private fun enterPhase(next: Transition) {
        // TODO: see task.md.
        TODO()
    }

    private fun checkTimeout() {
        // TODO: see task.md.
        TODO()
    }

    fun clearFault() {
        // TODO: see task.md.
        TODO()
    }

    fun atTarget(): Boolean = transition is Transition.Settled
}
