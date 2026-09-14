package course.l7t6

import frc.stubs.OI
import frc.stubs.Subsystem
import frc.stubs.superstructure.Arm
import frc.stubs.superstructure.Elevator
import kotlin.math.abs

// Solved in tasks 2 and 3. Given here.
object SuperStructure : Subsystem {
    const val SAFE_ARM_DEGREES = 90.0

    var state: Pose = Pose.HOME

    // The target checks matter. Under the sequencing rule the elevator is not
    // commanded until the arm arrives, so for one tick it is "at" its old target.
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
        val holding = Intake.state == Intake.State.HOLDING
        state = when (state) {
            Pose.HOME -> when {
                OI.intake && !holding -> Pose.CORAL_STATION
                holding && OI.scoreL2 -> Pose.L2
                holding && OI.scoreL4 -> Pose.L4
                OI.prepareClimb -> Pose.READY_TO_CLIMB
                else -> state
            }
            Pose.CORAL_STATION -> if (OI.home) Pose.HOME else state
            Pose.L2 -> when {
                OI.home -> Pose.HOME
                holding && OI.scoreL4 -> Pose.L4
                else -> state
            }
            Pose.L4 -> when {
                OI.home -> Pose.HOME
                holding && OI.scoreL2 -> Pose.L2
                else -> state
            }
            Pose.READY_TO_CLIMB -> when {
                OI.climb -> Pose.FULLY_CLIMBED
                OI.home -> Pose.HOME
                else -> state
            }
            Pose.FULLY_CLIMBED -> state
        }
    }

    private fun stateActions() {
        val target = state
        if (target.elevatorRotations < Elevator.position) {
            // Going down: elevator first, arm waits at the safe angle.
            Elevator.goTo(target.elevatorRotations)
            Arm.goTo(SAFE_ARM_DEGREES)
            if (abs(Elevator.position - target.elevatorRotations) < 0.5) {
                Arm.goTo(target.armDegrees)
            }
        } else {
            // Going up or level: arm first, then the elevator.
            Arm.goTo(target.armDegrees)
            if (abs(Arm.angle - target.armDegrees) < 2.0) {
                Elevator.goTo(target.elevatorRotations)
            }
        }
    }

    fun reset() {
        state = Pose.HOME
        Elevator.reset()
        Arm.reset()
    }
}
