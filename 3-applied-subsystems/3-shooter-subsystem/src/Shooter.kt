package course.l3t3

import frc.stubs.*

sealed class FsmState {
    data object Idle : FsmState()
    data class SpinningUp(val targetRpm: Double) : FsmState()
    data class Ready(val targetRpm: Double) : FsmState()
    data class Feeding(val targetRpm: Double) : FsmState()
}

object Shooter : Subsystem {
    val flywheel = TalonFX(canId = 60)
    val feeder = TalonFX(canId = 61)

    var state: FsmState = FsmState.Idle

    var commandedTargetRpm: Double? = null
    var commandedFire: Boolean = false

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
        state = FsmState.Idle
        commandedTargetRpm = null
        commandedFire = false
        flywheel.stopMotor()
        feeder.stopMotor()
    }
}
