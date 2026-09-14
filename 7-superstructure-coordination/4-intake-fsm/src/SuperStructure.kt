package course.l7t4

import frc.stubs.OI
import frc.stubs.Subsystem
import frc.stubs.superstructure.Arm
import frc.stubs.superstructure.Elevator
import kotlin.math.abs

// Solved in tasks 2 and 3. Given here so the intake has a peer to read.
object SuperStructure : Subsystem {
    const val SAFE_ARM_DEGREES = 90.0

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

    // The sequencing rule from task 3: going down, the elevator moves first
    // while the arm waits at the safe angle; otherwise the arm moves first.
    private fun stateActions() {
        if (state.elevatorRotations < Elevator.position) {
            Elevator.goTo(state.elevatorRotations)
            if (abs(Elevator.position - state.elevatorRotations) < 0.5) {
                Arm.goTo(state.armDegrees)
            } else {
                Arm.goTo(SAFE_ARM_DEGREES)
            }
        } else {
            Arm.goTo(state.armDegrees)
            if (abs(Arm.angle - state.armDegrees) < 2.0) {
                Elevator.goTo(state.elevatorRotations)
            }
        }
    }

    fun reset() {
        state = Pose.HOME
        Elevator.reset()
        Arm.reset()
    }
}
