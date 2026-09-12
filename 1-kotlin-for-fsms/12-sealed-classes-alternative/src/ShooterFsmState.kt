package course.l1t12

// Read carefully — this is the shape of the sealed class hierarchy the
// Lesson 3 Shooter is built on. Notice the two subclass forms:
//
//   data object Idle               -> a singleton with no per-instance data
//   data class  SpinningUp(...)    -> a class with a constructor argument
//
// In Lesson 3 (Shooter) you'll build a full FSM on a hierarchy just like this.

sealed class ShooterFsmState {
    data object Idle : ShooterFsmState()
    data class SpinningUp(val targetRpm: Double) : ShooterFsmState()
    data object Ready : ShooterFsmState()
    data class Feeding(val targetRpm: Double) : ShooterFsmState()
}

// `when` on a sealed class is exhaustive — the compiler tracks the complete
// list of subclasses, so it can prove every branch is covered.
fun describe(state: ShooterFsmState): String = when (state) {
    // TODO: write the four branches per task.md, then DELETE this `else` line.
    else -> TODO("complete the when branches and remove this else")
}
