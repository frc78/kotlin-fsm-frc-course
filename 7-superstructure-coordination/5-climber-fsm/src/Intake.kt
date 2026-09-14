package course.l7t5

import frc.stubs.ControlRequest
import frc.stubs.Debouncer
import frc.stubs.NeutralOut
import frc.stubs.OI
import frc.stubs.Subsystem
import frc.stubs.TalonFX
import frc.stubs.VoltageOut

// The intake FSM from task 4, complete. It owns "do I hold a piece" and reads
// the superstructure to decide when it is safe to eject.
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
        val stalled = stallDebounce.calculate(motor.getStatorCurrent() > 10.0)
        val scoringPose = SuperStructure.state == Pose.L2 || SuperStructure.state == Pose.L4
        state = when (state) {
            State.IDLE -> if (OI.intake) State.INTAKING else State.IDLE
            State.INTAKING -> when {
                OI.home -> State.IDLE
                stalled -> State.HOLDING
                else -> State.INTAKING
            }
            State.HOLDING ->
                if (OI.score && scoringPose && SuperStructure.atPosition) State.EJECTING else State.HOLDING
            State.EJECTING -> if (!OI.score) State.IDLE else State.EJECTING
        }
    }

    private fun stateActions() {
        motor.setControl(state.control)
    }

    fun reset() {
        state = State.IDLE
        stallDebounce.reset()
        motor.stopMotor()
    }
}
