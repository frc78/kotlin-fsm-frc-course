package course.l8t8

// The 2056 coordination pattern in miniature: two singleton FSMs, and the
// consumer (Feeder) transitions by READING the producer's (Intake) state.
// No framework, no events — one FSM's state is just another input.

object Intake {
    enum class State { SEEKING, HOLDING }

    var state: State = State.SEEKING

    private var ticks = 0

    fun periodic() {
        ticks++
        // Pretend the beam break trips on the third loop.
        if (state == State.SEEKING && ticks >= 3) {
            state = State.HOLDING
        }
    }
}

object Feeder {
    enum class State { WAITING, FEEDING }

    var state: State = State.WAITING

    fun periodic() {
        // Cross-FSM trigger: read the other singleton's state directly.
        if (state == State.WAITING && Intake.state == Intake.State.HOLDING) {
            state = State.FEEDING
        }
    }
}

fun main() {
    repeat(5) { tick ->
        Intake.periodic() // producer first...
        Feeder.periodic() // ...consumer after, so it sees THIS tick's state
        println("tick $tick: intake=${Intake.state}  feeder=${Feeder.state}")
    }
    // Try it: swap the two periodic() calls above and run again — the
    // feeder now reacts one tick late. Update order matters.
}
