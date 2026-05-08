package course.l3t3

import frc.stubs.*

sealed class FsmState {
    data object Idle : FsmState()
    data class SpinningUp(val targetRpm: Double) : FsmState()
    data class Ready(val targetRpm: Double) : FsmState()
    data class Feeding(val targetRpm: Double) : FsmState()
}

object Shooter : Subsystem {
    internal val flywheel = TalonFX(canId = 60)
    internal val feeder = TalonFX(canId = 61)

    var state: FsmState = FsmState.Idle
        private set

    var commandedTargetRpm: Double? = null
    var commandedFire: Boolean = false

    override fun periodic() {
        stateTransitions()
        stateActions()
    }

    private fun stateTransitions() {
        // TODO: implement the six transitions per task.md.
        // Hint: use `when (val s = state)` so smart casts give you `s.targetRpm`
        // inside `is` branches.
        TODO()
    }

    private fun stateActions() {
        // TODO: in each state, drive flywheel and feeder per task.md.
        // Use `s.targetRpm.rpm` to convert rpm -> rotations-per-second for VelocityVoltage.
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
