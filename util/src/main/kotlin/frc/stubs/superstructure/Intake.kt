package frc.stubs.superstructure

class Intake {
    enum class Mode { IDLE, INTAKING, HOLDING }

    var commandedMode: Mode = Mode.IDLE
        set(value) {
            field = value
            mode = value
        }

    var mode: Mode = Mode.IDLE
        private set

    fun tick() { /* intake snaps immediately */ }

    fun modeReached(): Boolean = mode == commandedMode
}
