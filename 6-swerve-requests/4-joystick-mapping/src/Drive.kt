package course.l6t4

import frc.stubs.swerve.*

object Drive {
    val drivetrain = SwerveDrivetrain()

    val maxSpeedMetersPerSecond = 4.5
    val maxTurnRadiansPerSecond = 6.0

    fun drive(leftX: Double, leftY: Double, rightX: Double) {
        // TODO: see task.md.
        TODO()
    }

    fun reset() {
        drivetrain.setControl(Idle)
    }
}
