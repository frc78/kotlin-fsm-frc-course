package frc.stubs

// Rising-edge debouncer. calculate() returns true only after its input has been
// true for `ticks` consecutive calls. One call per tick. WPILib's Debouncer
// takes seconds and a DebounceType; this stub counts calls so tests can step it.
class Debouncer(private val ticks: Int) {
    private var count = 0

    fun calculate(input: Boolean): Boolean {
        count = if (input) count + 1 else 0
        return count >= ticks
    }

    fun reset() {
        count = 0
    }
}
