package course.l7t5

import frc.stubs.ControlRequest
import frc.stubs.PositionVoltage
import frc.stubs.Subsystem
import frc.stubs.TalonFX

object Climber : Subsystem {
    enum class State(val control: ControlRequest) {
        RETRACTED(PositionVoltage(0.0)),
        EXTENDED(PositionVoltage(72.0)),
    }

    val motor = TalonFX(canId = 16)

    var state: State = State.RETRACTED

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
        state = State.RETRACTED
        motor.stopMotor()
    }
}
