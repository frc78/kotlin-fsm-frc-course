package course.l3t1

import frc.stubs.*

object Intake : Subsystem {
    enum class State { IDLE, INTAKING, HOLDING, EJECTING }

    internal val motor = TalonFX(canId = 22)
    internal val canRange = CANrange(canId = 33)

    var state: State = State.IDLE
        private set

    var commandedIntake: Boolean = false
    var commandedEject: Boolean = false

    override fun periodic() {
        stateTransitions()
        stateActions()
    }

    private fun stateTransitions() {
        // TODO: implement transitions per task.md.
        // Detection thresholds: detected if distance < 0.05 m; lost if distance > 0.10 m.
        TODO()
    }

    private fun stateActions() {
        // TODO: implement actions per task.md.
        TODO()
    }

    fun reset() {
        state = State.IDLE
        commandedIntake = false
        commandedEject = false
        motor.stopMotor()
        canRange.simulateDistance(Double.POSITIVE_INFINITY)
    }
}
