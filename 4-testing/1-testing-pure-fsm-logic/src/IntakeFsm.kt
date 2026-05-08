package course.l4t1

enum class State { IDLE, INTAKING, HOLDING, EJECTING }

data class Input(
    val commandedIntake: Boolean,
    val commandedEject: Boolean,
    val pieceDetected: Boolean,
    val pieceLost: Boolean,
)

fun transition(current: State, input: Input): State {
    // TODO: see task.md.
    TODO()
}
