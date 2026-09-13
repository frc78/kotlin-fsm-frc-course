package course.l2t4

import frc.stubs.*
import kotlin.math.abs

object Pivot : Subsystem {
    enum class State { STOWED, MOVING, AT_TARGET }

    val motor = TalonFX(canId = 40)

    var state: State = State.STOWED

    var targetRotations: Double = 0.0
    var commandedMove: Boolean = false
    var elevatorClear: Boolean = true

    override fun periodic() {
        stateTransitions()
        stateActions()
    }

    private fun stateTransitions() {
        // TODO: see task.md.
        TODO()
    }

    private fun stateActions() {
        when (state) {
            State.STOWED -> motor.stopMotor()
            State.MOVING -> motor.setControl(PositionVoltage(targetRotations))
            State.AT_TARGET -> motor.stopMotor()
        }
    }

    fun reset() {
        state = State.STOWED
        targetRotations = 0.0
        commandedMove = false
        elevatorClear = true
        motor.setPosition(0.0)
    }
}
