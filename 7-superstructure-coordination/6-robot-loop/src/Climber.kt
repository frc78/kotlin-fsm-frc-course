package course.l7t6

import frc.stubs.ControlRequest
import frc.stubs.PositionVoltage
import frc.stubs.Subsystem
import frc.stubs.TalonFX

// Solved in task 5. Given here.
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
        state = when (state) {
            State.RETRACTED ->
                if (SuperStructure.state == Pose.FULLY_CLIMBED && SuperStructure.atPosition) State.EXTENDED else state
            State.EXTENDED -> state
        }
    }

    private fun stateActions() {
        motor.setControl(state.control)
    }

    fun reset() {
        state = State.RETRACTED
        motor.stopMotor()
    }
}
