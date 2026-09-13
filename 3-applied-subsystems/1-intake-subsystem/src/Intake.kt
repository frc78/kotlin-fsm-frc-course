package course.l3t1

import frc.stubs.*

object Intake : Subsystem {
    enum class State { IDLE, INTAKING, HOLDING, EJECTING }

    val motor = TalonFX(canId = 22)
    val canRange = CANrange(canId = 33)

    var state: State = State.IDLE

    var commandedIntake: Boolean = false
    var commandedEject: Boolean = false

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
        commandedEject = false
        motor.stopMotor()
        canRange.simulateDistance(Double.POSITIVE_INFINITY)
    }
}
