package course.l7t6

import frc.stubs.ControlRequest
import frc.stubs.Debouncer
import frc.stubs.NeutralOut
import frc.stubs.OI
import frc.stubs.Subsystem
import frc.stubs.TalonFX
import frc.stubs.VoltageOut

// Solved in task 4. Given here.
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
            State.IDLE -> if (OI.intake) State.INTAKING else state
            State.INTAKING -> when {
                OI.home -> State.IDLE
                stalled -> State.HOLDING
                else -> state
            }
            State.HOLDING ->
                if (OI.score && scoringPose && SuperStructure.atPosition) State.EJECTING else state
            State.EJECTING -> if (!OI.score) State.IDLE else state
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
