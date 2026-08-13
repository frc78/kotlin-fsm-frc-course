package course.l9t2

import frc.stubs.*

object Straightenator : Subsystem {
    enum class State { IDLE, FEEDING, UNJAMMING }

    // TODO: declare your constants here (see the tables in task.md).

    internal val leftMotor: TalonFX = TODO() // TODO: see task.md.
    internal val rightMotor: TalonFX = TODO() // TODO: see task.md.
    internal val unjamTimer: Timer = TODO() // TODO: see task.md.

    init {
        configureMotors()
    }

    var state: State = State.IDLE
        private set
    private var previousState: State? = null

    var commandedFeed: Boolean = false

    override fun periodic() {
        stateTransitions()
        onEnter()
        stateActions()
        previousState = state
    }

    private fun configureMotors() {
        // TODO: see task.md.
        TODO()
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
        commandedFeed = false
        leftMotor.stopMotor()
        rightMotor.stopMotor()
        leftMotor.simulateStatorCurrent(0.0)
        rightMotor.simulateStatorCurrent(0.0)
        unjamTimer.stop()
        unjamTimer.reset()
    }
}
