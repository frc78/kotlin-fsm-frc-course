package course.l6t4

import frc.stubs.swerve.*

object Drive {
    val drivetrain = SwerveDrivetrain()

    fun pointWheels(directionDegrees: Double) {
        // TODO: see task.md.
        TODO()
    }

    fun reset() {
        drivetrain.setControl(Idle)
    }
}
