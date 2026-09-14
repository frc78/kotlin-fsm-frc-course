package frc.stubs.superstructure

import kotlin.math.abs
import kotlin.math.sign

// The arm as the superstructure sees it. Mirrors the real Pivot object:
// goTo(angle) sends a MotionMagic request, angle reads the encoder, and
// atPosition compares the two. Motion is a fixed step per tick.
object Arm {
    var angle: Double = 90.0
        private set

    var target: Double = 90.0
        private set

    val atPosition: Boolean
        get() = abs(angle - target) < 2.0

    private const val STEP_DEGREES = 45.0

    fun goTo(degrees: Double) {
        target = degrees
    }

    fun simulationPeriodic() {
        val remaining = target - angle
        angle = if (abs(remaining) <= STEP_DEGREES) target else angle + sign(remaining) * STEP_DEGREES
    }

    fun reset() {
        angle = 90.0
        target = 90.0
    }
}
