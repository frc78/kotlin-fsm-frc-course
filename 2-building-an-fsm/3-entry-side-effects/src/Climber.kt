package course.l2t3

import frc.stubs.*

object Climber : Subsystem {
    enum class State { STOWED, DEPLOYING, DEPLOYED, CLIMBING }

    internal val motor = TalonFX(canId = 30)

    var state: State = State.STOWED
        private set
    private var previousState: State? = null

    var commandedDeploy: Boolean = false
    var commandedClimb: Boolean = false

    val deploymentLogs: MutableList<String> = mutableListOf()

    override fun periodic() {
        stateTransitions()
        runEntrySideEffects()
        stateActions()
    }

    private fun stateTransitions() {
        state = when (state) {
            State.STOWED -> if (commandedDeploy) State.DEPLOYING else State.STOWED
            State.DEPLOYING -> if (motor.getPosition() >= 5.0) State.DEPLOYED else State.DEPLOYING
            State.DEPLOYED -> if (commandedClimb) State.CLIMBING else State.DEPLOYED
            State.CLIMBING -> State.CLIMBING
        }
    }

    private fun runEntrySideEffects() {
        // TODO: see task.md — append a log line per state on entry, then update
        // `previousState`.
        TODO()
    }

    private fun stateActions() {
        when (state) {
            State.STOWED -> motor.setControl(VoltageOut(0.0))
            State.DEPLOYING -> motor.setControl(VoltageOut(6.0))
            State.DEPLOYED -> motor.setControl(VoltageOut(0.5))
            State.CLIMBING -> motor.setControl(VoltageOut(-12.0))
        }
    }

    fun reset() {
        state = State.STOWED
        previousState = null
        commandedDeploy = false
        commandedClimb = false
        motor.setPosition(0.0)
        deploymentLogs.clear()
    }
}
