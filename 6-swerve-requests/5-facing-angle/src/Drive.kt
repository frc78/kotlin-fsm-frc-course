package course.l6t5

import frc.stubs.swerve.*

object Drive {
    val drivetrain = SwerveDrivetrain()

    fun aimWhileDriving(vx: Double, vy: Double, targetDegrees: Double) {
        // TODO: see task.md.
        TODO()
    }

    fun reset() {
        drivetrain.setControl(Idle)
    }
}
