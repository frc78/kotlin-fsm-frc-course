package course.l7t2

import frc.stubs.OI
import frc.stubs.Subsystem
import frc.stubs.superstructure.Arm
import frc.stubs.superstructure.Elevator

object SuperStructure : Subsystem {
    var state: Pose = Pose.HOME

    // Both targets must be the pose setpoints. Under the sequencing rule the
    // elevator is not commanded until the arm arrives, so for one tick it is
    // still "at" its old target.
    val atPosition: Boolean
        get() = Elevator.target == state.elevatorRotations && Arm.target == state.armDegrees &&
            Elevator.atPosition && Arm.atPosition

    override fun periodic() {
        stateTransitions()
        stateActions()
        Elevator.simulationPeriodic()
        Arm.simulationPeriodic()
    }

    private fun stateTransitions() {
        // TODO: see task.md.
        TODO()
    }

    private fun stateActions() {
        // TODO: see task.md.
        TODO()
    }

    fun reset() {
        state = Pose.HOME
        Elevator.reset()
        Arm.reset()
    }
}
