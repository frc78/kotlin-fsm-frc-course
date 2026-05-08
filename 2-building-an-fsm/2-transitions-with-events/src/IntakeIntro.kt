package course.l2t2

import frc.stubs.*

object IntakeIntro : Subsystem {
    enum class State { IDLE, INTAKING, HOLDING }

    internal val motor = TalonFX(canId = 22)
    internal val canRange = CANrange(canId = 33)

    var state: State = State.IDLE
        private set

    var commandedIntake: Boolean = false

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
        state = State.IDLE
        commandedIntake = false
        motor.stopMotor()
        canRange.simulateDistance(Double.POSITIVE_INFINITY)
    }
}
