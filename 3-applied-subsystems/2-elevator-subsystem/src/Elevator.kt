package course.l3t2

import frc.stubs.*
import kotlin.math.abs

object Elevator : Subsystem {
    enum class State(val targetRotations: Double) {
        STOWED(0.0),
        LOW(4.0),
        MID(9.5),
        HIGH(14.5);
    }

    internal val motor = TalonFX(canId = 50)

    var state: State = State.STOWED
        private set

    var commandedTarget: State = State.STOWED

    override fun periodic() {
        stateTransitions()
        stateActions()
    }

    private fun stateTransitions() {
        // TODO: state = commandedTarget
        TODO()
    }

    private fun stateActions() {
        // TODO: motor.setControl(PositionVoltage(state.targetRotations))
        TODO()
    }

    fun atTarget(): Boolean {
        // TODO: return abs(motor.getPosition() - state.targetRotations) < 0.1
        TODO()
    }

    fun reset() {
        state = State.STOWED
        commandedTarget = State.STOWED
        motor.setPosition(0.0)
    }
}
