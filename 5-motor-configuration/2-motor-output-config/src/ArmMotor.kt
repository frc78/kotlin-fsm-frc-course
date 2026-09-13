package course.l5t2

import frc.stubs.*

object ArmMotor {
    var motor = TalonFX(canId = 25)

    // TODO: see task.md.
    fun configure() {
        TODO()
    }

    fun reset() {
        motor = TalonFX(canId = 25)
    }
}
