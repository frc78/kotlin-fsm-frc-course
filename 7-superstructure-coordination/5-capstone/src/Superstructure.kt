package course.l7t5

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
        data class RetractArm(val target: RobotState) : Transition()
        data class MoveElevator(val target: RobotState) : Transition()
        data class ExtendArm(val target: RobotState) : Transition()
        data class Settled(val at: RobotState) : Transition()
    }

    var transition: Transition = Transition.Settled(RobotState.STOWED)
        private set

    fun periodic() {
        if (transitionTargetState() != commandedRobotState) {
            transition = startTransition(commandedRobotState)
        }
        stateActions()
        elevator.tick()
        arm.tick()
        intake.tick()
        advanceTransition()
    }

    private fun transitionTargetState(): RobotState = when (val t = transition) {
        is Transition.Settled -> t.at
        is Transition.RetractArm -> t.target
        is Transition.MoveElevator -> t.target
        is Transition.ExtendArm -> t.target
    }

    // Decides which phase to begin in based on what's already done.
    // Read this carefully — it's the brain of the whole machine.
    private fun startTransition(target: RobotState): Transition = when {
        arm.state != Arm.State.STOWED && target.elevator != elevator.state ->
            Transition.RetractArm(target)
        elevator.state != target.elevator ->
            Transition.MoveElevator(target)
        arm.state != target.arm || intake.mode != target.intake ->
            Transition.ExtendArm(target)
        else ->
            Transition.Settled(target)
    }

    private fun stateActions() {
        // TODO: command subsystems based on the current `transition` phase.
        //
        // Settled(at):
        //   elevator.commandedTarget = at.elevator
        //   arm.commandedTarget      = at.arm
        //   intake.commandedMode     = at.intake
        //
        // RetractArm(t):  arm.commandedTarget = Arm.State.STOWED
        //                 (don't touch elevator/intake)
        //
        // MoveElevator(t): elevator.commandedTarget = t.target.elevator
        //                  (arm is already at STOWED; don't touch)
        //
        // ExtendArm(t): arm.commandedTarget = t.target.arm
        //               intake.commandedMode = t.target.intake
        //               (elevator is already at target; don't touch)
        TODO()
    }

    private fun advanceTransition() {
        // TODO: advance to the next phase when the current one completes.
        //
        //   RetractArm(t)   -> startTransition(t.target) when arm.atTarget()
        //   MoveElevator(t) -> startTransition(t.target) when elevator.atTarget()
        //   ExtendArm(t)    -> startTransition(t.target) when arm.atTarget() && intake.modeReached()
        //   Settled(...)    -> stays settled
        //
        // (Calling startTransition again with the same target is the trick —
        // it'll automatically pick the next applicable phase, or Settled if
        // we're done.)
        TODO()
    }

    fun atTarget(): Boolean = transition is Transition.Settled
}
