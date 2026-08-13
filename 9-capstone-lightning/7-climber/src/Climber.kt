package course.l9t7

import frc.stubs.*

object Climber : Subsystem {
    enum class State { STOWED, DEPLOYING, DEPLOYED, CLIMBING, CLIMBED, FAULTED }

    // Hardware — see the wiring table in task.md.
    internal val winchMotor: TalonFX = TODO() // TODO: see task.md.
    internal val climbTimer: Timer = TODO() // TODO: see task.md.

    // TODO: declare your constants here (see the tables in task.md).

    var state: State = State.STOWED
        private set
    private var previousState: State? = null

    var commandedDeploy: Boolean = false
    var commandedClimb: Boolean = false

    init {
        configureMotors()
    }

    private fun configureMotors() {
        // TODO: see task.md.
        TODO()
    }

    override fun periodic() {
        stateTransitions()
        onEnter()
        stateActions()
        previousState = state
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
        state = State.STOWED
        previousState = null
        commandedDeploy = false
        commandedClimb = false
        winchMotor.stopMotor()
        winchMotor.simulatePosition(0.0)
        climbTimer.stop()
        climbTimer.reset()
    }
}
