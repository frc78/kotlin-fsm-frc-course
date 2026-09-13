package course.l5t3

import frc.stubs.*

object IntakeMotor {
    var motor = TalonFX(canId = 22)

    // TODO: see task.md.
    fun configure() {
        TODO()
    }

    fun reset() {
        motor = TalonFX(canId = 22)
    }
}
