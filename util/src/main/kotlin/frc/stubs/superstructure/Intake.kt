package frc.stubs.superstructure

// A roller intake as the superstructure sees it. The superstructure asks the
// rollers to run, stop, or eject. The intake decides by itself whether it holds
// a piece, from its own sensor. A request change takes spinUpTicks ticks to
// take effect: rollers need time to spin up or down.
class Intake {
    enum class Request { STOP, INTAKE, EJECT }
    enum class Mode { IDLE, INTAKING, HOLDING, EJECTING }

    var request: Request = Request.STOP
        set(value) {
            if (field != value) {
                field = value
                ticksRemaining = spinUpTicks
            }
        }

    var mode: Mode = Mode.IDLE
        private set

    private var hasPiece = false
    private var ticksRemaining = 0
    private val spinUpTicks = 4

    // Test hook: the beam-break sensor sees a piece or not.
    fun simulatePieceDetected(present: Boolean) {
        hasPiece = present
    }

    fun tick() {
        if (ticksRemaining > 0) ticksRemaining--
        if (ticksRemaining == 0) mode = modeFor(request)
    }

    fun requestReached(): Boolean = ticksRemaining == 0 && mode == modeFor(request)

    private fun modeFor(r: Request): Mode = when (r) {
        Request.STOP -> if (hasPiece) Mode.HOLDING else Mode.IDLE
        Request.INTAKE -> Mode.INTAKING
        Request.EJECT -> Mode.EJECTING
    }
}
