package course.l2t1

import frc.stubs.*

object Indexer : Subsystem {
    enum class State { IDLE, INDEXING, JAMMED }

    val motor = TalonFX(canId = 21)
    val jamSensor = DigitalInput(channel = 0)

    var state: State = State.IDLE

    var commandedIndex: Boolean = false

    override fun periodic() {
        stateTransitions()
        stateActions()
    }

    private fun stateActions() {
        // TODO: see task.md.
        TODO()
    }

    private fun stateTransitions() {
        // TODO: see task.md.
        TODO()
    }

    fun reset() {
        state = State.IDLE
        commandedIndex = false
        motor.stopMotor()
        jamSensor.simulateValue(false)
    }
}
