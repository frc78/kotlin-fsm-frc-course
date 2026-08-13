package course.l9t3

import frc.stubs.*

object Gripper : Subsystem {
    enum class State { EMPTY, HANDOFF, HOLDING_CORAL, ALGAE_INTAKE, HOLDING_ALGAE, RELEASING }

    // TODO: declare your constants here (see the tables in task.md).

    internal val motor: TalonFX = TODO() // TODO: see task.md.
    internal val cradleBeamBreak: DigitalInput = TODO() // TODO: see task.md.
    internal val gripperBeamBreak: DigitalInput = TODO() // TODO: see task.md.

    var state: State = State.EMPTY
        private set

    var commandedAlgaeIntake: Boolean = false
    var commandedRelease: Boolean = false

    init {
        configureMotors()
    }

    private fun configureMotors() {
        // TODO: see task.md.
        TODO()
    }

    /** Read by the superstructure in later tasks. */
    fun hasCoral(): Boolean = state == State.HOLDING_CORAL

    /** Read by the superstructure in later tasks. */
    fun hasAlgae(): Boolean = state == State.HOLDING_ALGAE

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
        state = State.EMPTY
        commandedAlgaeIntake = false
        commandedRelease = false
        motor.stopMotor()
        motor.simulateStatorCurrent(0.0)
        gripperBeamBreak.simulateValue(false)
        cradleBeamBreak.simulateValue(false)
    }
}
