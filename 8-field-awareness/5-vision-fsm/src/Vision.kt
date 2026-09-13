package course.l8t5

import frc.stubs.Subsystem
import frc.stubs.geometry.Pose2d
import frc.stubs.swerve.SwerveDrivetrain
import frc.stubs.vision.VisionMeasurement

object Vision : Subsystem {
    enum class State { NO_TARGET, TRACKING, REJECTING }

    val drivetrain = SwerveDrivetrain()

    // null when the camera sees no tag. Set by the camera code.
    var latestMeasurement: VisionMeasurement? = null

    // The robot clock in seconds. Set by the caller.
    var nowSeconds: Double = 0.0

    var state: State = State.NO_TARGET

    var lastAppliedTimestampSeconds: Double = Double.NEGATIVE_INFINITY

    override fun periodic() {
        stateTransitions()
        stateActions()
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
        // TODO: see task.md.
        TODO()
    }
}
