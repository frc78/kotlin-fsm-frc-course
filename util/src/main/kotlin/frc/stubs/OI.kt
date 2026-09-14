package frc.stubs

// Operator interface. The real OI reads an XboxController and a button board
// and exposes functions such as OI.home(). This stub exposes booleans that
// tests set directly. Every state machine reads its driver input from here.
object OI {
    var home = false
    var intake = false
    var scoreL2 = false
    var scoreL4 = false
    var score = false
    var prepareClimb = false
    var climb = false

    fun reset() {
        home = false
        intake = false
        scoreL2 = false
        scoreL4 = false
        score = false
        prepareClimb = false
        climb = false
    }
}
