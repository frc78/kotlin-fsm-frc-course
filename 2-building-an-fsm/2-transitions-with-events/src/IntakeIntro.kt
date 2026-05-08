package course.l2t2

import frc.stubs.*

object IntakeIntro : Subsystem {
    enum class State { IDLE, INTAKING, HOLDING }

    internal val motor = TalonFX(canId = 22)
    internal val canRange = CANrange(canId = 33)

    var state: State = State.IDLE
        private set

    var commandedIntake: Boolean = false

    override fun periodic() {
        stateTransitions()
        stateActions()
    }

    private fun stateTransitions() {
        // TODO:
        //   IDLE     + commandedIntake                          -> INTAKING
        //   INTAKING + canRange.getDistance() < 0.05            -> HOLDING
        //   INTAKING + !commandedIntake                         -> IDLE
        //   HOLDING  + !commandedIntake                         -> IDLE
        TODO()
    }

    private fun stateActions() {
        // TODO:
        //   IDLE     -> 0.0 V
        //   INTAKING -> 6.0 V
        //   HOLDING  -> 1.0 V
        TODO()
    }

    fun reset() {
        state = State.IDLE
        commandedIntake = false
        motor.stopMotor()
        canRange.simulateDistance(Double.POSITIVE_INFINITY)
    }
}
