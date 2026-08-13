package frc.stubs.lightning

// LIGHTNING's 2-stage belt-driven elevator (2x Kraken X60 through 5:1 on a
// 30T pulley — full range of motion in ~0.6 s on the real robot). Same
// simulation conventions as frc.stubs.superstructure from Lesson 7: setting
// commandedTarget arms a move that completes after a fixed number of tick()
// calls, and state reports the last SETTLED setpoint.
//
// 2056 never published their real setpoint numbers; these rotations are
// course values.
class Elevator {
    enum class Setpoint(val rotations: Double) {
        HOME(0.0),
        CORAL_PICKUP(1.5),
        L1(4.0),
        L2(8.0),
        L3(14.0),
        L4(22.0),
        L2_DUNK(6.5),
        L3_DUNK(12.5),
        L4_DUNK(20.5),
        ALGAE_FLOOR(2.0),
        ALGAE_L2(9.0),
        ALGAE_L3(15.0),
        ALGAE_CARRY(5.0),
        NET(24.0),
        PROCESSOR(3.0),
    }

    var commandedTarget: Setpoint = Setpoint.HOME
        set(value) {
            if (field != value) {
                field = value
                ticksRemaining = if (value == state) 0 else stepsToReach
            }
        }

    var state: Setpoint = Setpoint.HOME
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

    fun reset() {
        commandedTarget = Setpoint.HOME
        state = Setpoint.HOME
        ticksRemaining = 0
    }
}
