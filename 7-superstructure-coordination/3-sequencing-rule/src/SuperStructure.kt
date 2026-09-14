package course.l7t3

import frc.stubs.OI
import frc.stubs.Subsystem
import frc.stubs.superstructure.Arm
import frc.stubs.superstructure.Elevator
import kotlin.math.abs

object SuperStructure : Subsystem {
    // The arm angle that clears the elevator's path.
    const val SAFE_ARM_DEGREES = 90.0

    var state: Pose = Pose.HOME

    // At the pose: both mechanisms were sent the pose setpoints and both arrived.
    val atPosition: Boolean
        get() = Elevator.target == state.elevatorRotations && Arm.target == state.armDegrees &&
            Elevator.atPosition && Arm.atPosition

    override fun periodic() {
        stateTransitions()
        stateActions()
        Elevator.simulationPeriodic()
        Arm.simulationPeriodic()
    }

    // Task 2's transitions, already written. First matching row wins.
    private fun stateTransitions() {
        val holding = Intake.state == Intake.State.HOLDING
        state = when (state) {
            Pose.HOME -> when {
                OI.intake && !holding -> Pose.CORAL_STATION
                holding && OI.scoreL2 -> Pose.L2
                holding && OI.scoreL4 -> Pose.L4
                OI.prepareClimb -> Pose.READY_TO_CLIMB
                else -> Pose.HOME
            }
            Pose.CORAL_STATION -> if (OI.home) Pose.HOME else Pose.CORAL_STATION
            Pose.L2 -> when {
                OI.home -> Pose.HOME
                holding && OI.scoreL4 -> Pose.L4
                else -> Pose.L2
            }
            Pose.L4 -> when {
                OI.home -> Pose.HOME
                holding && OI.scoreL2 -> Pose.L2
                else -> Pose.L4
            }
            Pose.READY_TO_CLIMB -> when {
                OI.climb -> Pose.FULLY_CLIMBED
                OI.home -> Pose.HOME
                else -> Pose.READY_TO_CLIMB
            }
            Pose.FULLY_CLIMBED -> Pose.FULLY_CLIMBED
        }
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
