package course.l4t2

import frc.stubs.*

class LinearActuator(
    private val motor: TalonFX,
    private val limit: DigitalInput,
) : Subsystem {

    enum class State { RETRACTED, EXTENDING, EXTENDED }

    var state: State = State.RETRACTED

    var commandedExtend: Boolean = false

    override fun periodic() {
        // TODO: see task.md.
        TODO()
    }
}
