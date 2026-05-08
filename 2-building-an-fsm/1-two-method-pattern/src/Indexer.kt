package course.l2t1

import frc.stubs.*

object Indexer : Subsystem {
    enum class State { IDLE, INDEXING, JAMMED }

    internal val motor = TalonFX(canId = 21)
    internal val jamSensor = DigitalInput(channel = 0)

    var state: State = State.IDLE
        private set

    var commandedIndex: Boolean = false

    override fun periodic() {
        stateTransitions()
        stateActions()
    }

    private fun stateActions() {
        // TODO: see task.md — drive `motor` based on the current state.
        TODO("complete the when (state) block")
    }

    private fun stateTransitions() {
        // TODO: see task.md — choose the next state based on inputs.
        // Otherwise stay in the current state.
        TODO("complete the when (state) block; assign the result to `state`")
    }

    fun reset() {
        state = State.IDLE
        commandedIndex = false
        motor.stopMotor()
        jamSensor.simulateValue(false)
    }
}
