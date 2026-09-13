package course.l5t4

import frc.stubs.*

object Flywheel {
    var motor = TalonFX(canId = 60)

    // TODO: see task.md.
    fun configure() {
        TODO()
    }

    // TODO: see task.md.
    fun runAtRps(rps: Double) {
        TODO()
    }

    fun reset() {
        motor = TalonFX(canId = 60)
    }
}
