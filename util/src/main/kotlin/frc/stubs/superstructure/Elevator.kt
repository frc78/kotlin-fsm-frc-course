package frc.stubs.superstructure

import kotlin.math.abs
import kotlin.math.sign

// The elevator as the superstructure sees it. Mirrors the real Elevator object:
// goTo(height) sends a MotionMagic request, position reads the encoder, and
// atPosition compares the two. Motion is simulated as a fixed step per tick, so
// atPosition stays false until the carriage arrives and a reversal takes the
// distance back.
object Elevator {
    var position: Double = 0.0
        private set

    var target: Double = 0.0
        private set

    val atPosition: Boolean
        get() = abs(position - target) < 0.1

    private const val STEP_ROTATIONS = 3.0

    fun goTo(rotations: Double) {
        target = rotations
    }

    fun simulationPeriodic() {
        val remaining = target - position
        position = if (abs(remaining) <= STEP_ROTATIONS) target else position + sign(remaining) * STEP_ROTATIONS
    }

    fun reset() {
        position = 0.0
        target = 0.0
    }
}
