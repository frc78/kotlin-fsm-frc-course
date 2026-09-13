package course.l6t2

import frc.stubs.swerve.*

object Drive {
    val drivetrain = SwerveDrivetrain()

    fun applyBrake() {
        // TODO: see task.md.
        TODO()
    }

    fun reset() {
        drivetrain.setControl(Idle)
    }
}
