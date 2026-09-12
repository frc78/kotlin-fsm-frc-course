package course.l1t6

import kotlin.math.roundToInt

object Battery {
    var volts: Double = 12.6

    // TODO: see task.md.
    fun isLow(): Boolean = TODO()

    // TODO: see task.md.
    fun percent(): Int = TODO()

    fun reset() {
        volts = 12.6
    }
}
