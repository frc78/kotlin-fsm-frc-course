package frc.stubs.superstructure

class Elevator {
    enum class State(val targetRotations: Double) {
        STOWED(0.0), LOW(4.0), MID(9.5), HIGH(14.5)
    }

    var commandedTarget: State = State.STOWED
        set(value) {
            if (field != value) {
                field = value
                ticksRemaining = if (value == state) stepsToReach - ticksRemaining else stepsToReach
            }
        }

    var state: State = State.STOWED
        private set

    private var ticksRemaining: Int = 0
    private val stepsToReach = 3

    fun tick() {
        if (ticksRemaining > 0) {
            ticksRemaining--
            if (ticksRemaining == 0) state = commandedTarget
        }
    }

    fun atTarget(): Boolean = ticksRemaining == 0 && state == commandedTarget
}
