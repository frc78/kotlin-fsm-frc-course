package course.l2t2

import frc.stubs.*

object IntakeIntro : Subsystem {
    enum class State { IDLE, INTAKING, HOLDING }

    val motor = TalonFX(canId = 22)
    val canRange = CANrange(canId = 33)

    var state: State = State.IDLE

    var commandedIntake: Boolean = false

    override fun periodic() {
        // TODO: see task.md.
        TODO()
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
