package course.l1t15

// A complete finite state machine in about twenty lines.
//
// Run main() and watch the machine walk through a wash cycle. Then try adding
// a RINSING state between DRAINING and SPINNING — the moment you add the enum
// value, the compiler points you at every `when` that needs a new branch.

enum class WashState { FILLING, WASHING, DRAINING, SPINNING, DONE }

// Transitions: given the current state and how long we've been in it,
// decide the next state. A pure decision — no side effects.
fun transition(state: WashState, ticksInState: Int): WashState = when (state) {
    WashState.FILLING -> if (ticksInState >= 2) WashState.WASHING else state
    WashState.WASHING -> if (ticksInState >= 3) WashState.DRAINING else state
    WashState.DRAINING -> if (ticksInState >= 2) WashState.SPINNING else state
    WashState.SPINNING -> if (ticksInState >= 2) WashState.DONE else state
    WashState.DONE -> state
}

// Actions: what the machine DOES in each state, every tick.
fun action(state: WashState): String = when (state) {
    WashState.FILLING -> "valve open, water rising"
    WashState.WASHING -> "drum agitating"
    WashState.DRAINING -> "pump running, water out"
    WashState.SPINNING -> "drum at full speed"
    WashState.DONE -> "everything off — come unload"
}

fun main() {
    var state = WashState.FILLING
    var ticksInState = 0
    repeat(14) { tick ->
        val next = transition(state, ticksInState) // 1. decide
        ticksInState = if (next == state) ticksInState + 1 else 0
        state = next
        println("tick ${tick.toString().padStart(2)} | ${state.name.padEnd(8)} | ${action(state)}") // 2. act
    }
}
