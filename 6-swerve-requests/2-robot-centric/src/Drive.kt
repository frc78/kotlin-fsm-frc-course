package course.l6t2

import frc.stubs.swerve.*

object Drive {
    val drivetrain = SwerveDrivetrain()

    fun teleopDrive(vx: Double, vy: Double, omega: Double, robotRelative: Boolean) {
        // TODO: see task.md.
        TODO()
    }

    fun reset() {
        drivetrain.setControl(Idle)
    }
}
