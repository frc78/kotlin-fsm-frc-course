package course.l7t4

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

    sealed class Transition {
        data class WaitingForElevator(val target: RobotState) : Transition()
        data class WaitingForArm(val target: RobotState) : Transition()
        data class Settled(val at: RobotState) : Transition()
    }

    var transition: Transition = Transition.Settled(RobotState.STOWED)
        private set

    fun periodic() {
        // Detect a newly-commanded state and restart the transition.
        if (transitionTargetState() != commandedRobotState) {
            transition = Transition.WaitingForElevator(commandedRobotState)
        }
        stateActions()
        elevator.tick()
        arm.tick()
        intake.tick()
        advanceTransition()
    }

    private fun transitionTargetState(): RobotState = when (val t = transition) {
        is Transition.Settled -> t.at
        is Transition.WaitingForElevator -> t.target
        is Transition.WaitingForArm -> t.target
    }

    private fun stateActions() {
        // TODO: command subsystems based on the current `transition` phase.
        //
        // Settled(at):
        //   elevator.commandedTarget = at.elevator
        //   arm.commandedTarget      = at.arm
        //   intake.commandedMode     = at.intake
        //
        // WaitingForElevator(t): command ONLY the elevator.
        //   elevator.commandedTarget = t.target.elevator
        //   (don't touch arm or intake)
        //
        // WaitingForArm(t): command everything.
        //   elevator.commandedTarget = t.target.elevator
        //   arm.commandedTarget      = t.target.arm
        //   intake.commandedMode     = t.target.intake
        TODO()
    }

    private fun advanceTransition() {
        // TODO: advance the transition phase if applicable.
        //
        //   WaitingForElevator(t) -> WaitingForArm(t.target) when elevator.atTarget()
        //   WaitingForArm(t)      -> Settled(t.target)       when arm.atTarget() && intake.modeReached()
        //   Settled(...)          -> stays settled
        TODO()
    }

    fun atTarget(): Boolean = transition is Transition.Settled
}
