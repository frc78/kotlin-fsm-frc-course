package course.l4t1

enum class State { IDLE, INTAKING, HOLDING, EJECTING }

data class Input(
    val commandedIntake: Boolean,
    val commandedEject: Boolean,
    val pieceDetected: Boolean,
    val pieceLost: Boolean,
)

fun transition(current: State, input: Input): State {
    // TODO: implement the seven transitions per task.md, preferring earlier
    // table rows when multiple conditions match. Stay in `current` if none do.
    TODO()
}
