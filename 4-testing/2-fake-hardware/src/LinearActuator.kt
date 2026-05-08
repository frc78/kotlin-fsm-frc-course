package course.l4t2

import frc.stubs.*

class LinearActuator(
    private val motor: TalonFX,
    private val limit: DigitalInput,
) : Subsystem {

    enum class State { RETRACTED, EXTENDING, EXTENDED }

    var state: State = State.RETRACTED
        private set

    var commandedExtend: Boolean = false

    override fun periodic() {
        // TODO: implement transitions and actions per task.md.
        //
        // Suggested split — same two-method pattern as before:
        //   stateTransitions() — update `state`
        //   stateActions()     — drive the motor
        // Or do it inline here. Either is fine.
        TODO()
    }
}
