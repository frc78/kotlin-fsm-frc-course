package course.l2t5

import frc.stubs.*

object EjectingIntake : Subsystem {
    enum class State { IDLE, INTAKING, EJECTING }

    internal val motor = TalonFX(canId = 24)
    internal val ejectTimer = Timer()

    var state: State = State.IDLE
        private set
    private var previousState: State? = null

    var commandedIntake: Boolean = false
    var commandedEject: Boolean = false

    override fun periodic() {
        stateTransitions()
        onEnter()
        stateActions()
        previousState = state // entry-edge bookkeeping (see task 3)
    }

    private fun stateTransitions() {
        // TODO: see task.md.
        TODO()
    }

    private fun onEnter() {
        // TODO: see task.md.
        TODO()
    }

    private fun stateActions() {
        // TODO: see task.md.
        TODO()
    }

    fun reset() {
        state = State.IDLE
        previousState = null
        commandedIntake = false
        commandedEject = false
        motor.stopMotor()
        ejectTimer.stop()
        ejectTimer.reset()
    }
}
