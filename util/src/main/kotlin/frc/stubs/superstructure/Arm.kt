package frc.stubs.superstructure

class Arm {
    enum class State(val angleDegrees: Double) {
        STOWED(90.0), GROUND(-30.0), SCORE(45.0), CLIMB(180.0)
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
    private val stepsToReach = 2

    fun tick() {
        if (ticksRemaining > 0) {
            ticksRemaining--
            if (ticksRemaining == 0) state = commandedTarget
        }
    }

    fun atTarget(): Boolean = ticksRemaining == 0 && state == commandedTarget
}
