package frc.stubs

// Mirrors edu.wpi.first.wpilibj.Timer's API surface: start()/stop()/reset()/
// restart()/get()/hasElapsed(). Real WPILib reads the FPGA clock; this stub
// only advances when a test calls simulateAdvance(), so FSM timing logic can
// be tested tick by tick.
class Timer {
    private var elapsedSeconds = 0.0
    private var running = false

    fun start() {
        running = true
    }

    fun stop() {
        running = false
    }

    fun reset() {
        elapsedSeconds = 0.0
    }

    fun restart() {
        reset()
        start()
    }

    fun get(): Double = elapsedSeconds

    fun hasElapsed(seconds: Double): Boolean = elapsedSeconds >= seconds

    // Test hook: advance simulated time. Like the real clock, time only
    // accumulates on the timer while it is running.
    fun simulateAdvance(seconds: Double) {
        if (running) elapsedSeconds += seconds
    }
}
