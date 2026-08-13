package frc.stubs.lightning

// LIGHTNING's gripper-arm wrist (Kraken X44 at 59:1, #25 chain on a floating
// dead axle). Faster than the elevator: 2 ticks to reach a setpoint instead
// of 3. Same conventions as the Elevator stub — commandedTarget arms a move,
// tick() advances it, state reports the last SETTLED setpoint.
//
// Angles are course values; 2056 never published theirs.
class Wrist {
    enum class Setpoint(val degrees: Double) {
        STOWED(90.0),
        CORAL_PICKUP(-20.0),
        SCORE(35.0),
        DUNK(10.0),
        SPIT(0.0),
        ALGAE_PICKUP(-10.0),
        CARRY(60.0),
        NET(120.0),
        PROCESSOR(-5.0),
    }

    var commandedTarget: Setpoint = Setpoint.STOWED
        set(value) {
            if (field != value) {
                field = value
                ticksRemaining = if (value == state) 0 else stepsToReach
            }
        }

    var state: Setpoint = Setpoint.STOWED
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

    fun reset() {
        commandedTarget = Setpoint.STOWED
        state = Setpoint.STOWED
        ticksRemaining = 0
    }
}
