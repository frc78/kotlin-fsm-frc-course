package course.l7t4

import frc.stubs.ControlRequest
import frc.stubs.Debouncer
import frc.stubs.NeutralOut
import frc.stubs.OI
import frc.stubs.Subsystem
import frc.stubs.TalonFX
import frc.stubs.VoltageOut

object Intake : Subsystem {
    enum class State(val control: ControlRequest) {
        IDLE(NeutralOut),
        INTAKING(VoltageOut(6.0)),
        HOLDING(VoltageOut(1.0)),
        EJECTING(VoltageOut(-6.0)),
    }

    val motor = TalonFX(canId = 15)

    var state: State = State.IDLE

    private val stallDebounce = Debouncer(5)

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
        stallDebounce.reset()
        motor.stopMotor()
    }
}
