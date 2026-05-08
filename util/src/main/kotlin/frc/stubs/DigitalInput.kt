package frc.stubs

class DigitalInput(val channel: Int) {
    private var value = false

    fun get(): Boolean = value

    fun simulateValue(v: Boolean) { value = v }
}
