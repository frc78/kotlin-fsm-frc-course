package course.l9t1

import frc.stubs.*

object Intake : Subsystem {
    enum class State { STOWED, INTAKING, PURGING }

    // TODO: declare your constants here (see the tables in task.md).

    internal val rollerMotor: TalonFX = TODO() // TODO: see task.md.
    internal val pivotMotor: TalonFX = TODO() // TODO: see task.md.
    internal val beamBreak: DigitalInput = TODO() // TODO: see task.md.

    var state: State = State.STOWED
        private set

    var commandedDeploy: Boolean = false
    var commandedPurge: Boolean = false

    init {
        configureMotors()
    }

    private fun configureMotors() {
        // TODO: see task.md.
        TODO()
    }

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
        state = State.STOWED
        commandedDeploy = false
        commandedPurge = false
        rollerMotor.stopMotor()
        pivotMotor.stopMotor()
        rollerMotor.simulatePosition(0.0)
        pivotMotor.simulatePosition(0.0)
        beamBreak.simulateValue(false)
    }
}
