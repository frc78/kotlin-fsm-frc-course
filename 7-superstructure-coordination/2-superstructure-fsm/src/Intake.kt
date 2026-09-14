package course.l7t2

// Stand-in for the intake FSM you build in task 4. The superstructure only
// reads its state. Tests set it directly.
object Intake {
    enum class State { IDLE, INTAKING, HOLDING, EJECTING }

    var state: State = State.IDLE

    fun reset() {
        state = State.IDLE
    }
}
