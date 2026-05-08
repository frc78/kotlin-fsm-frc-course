package course.l1t4

// Read carefully — this is the sealed class hierarchy you'll use in the next
// task. Notice the two subclass forms:
//
//   data object Idle               -> a singleton with no per-instance data
//   data class  SpinningUp(...)    -> a class with a constructor argument
//
// You'll write subclasses like these yourself in Lesson 3 (Shooter).

sealed class ShooterFsmState {
    data object Idle : ShooterFsmState()
    data class SpinningUp(val targetRpm: Double) : ShooterFsmState()
    data object Ready : ShooterFsmState()
    data class Feeding(val targetRpm: Double) : ShooterFsmState()
}

// `when` on a sealed class is exhaustive — the compiler tracks the complete
// list of subclasses, so it can prove every branch is covered.
fun describe(state: ShooterFsmState): String = when (state) {
    // TODO: complete the four branches:
    //
    //   ShooterFsmState.Idle           -> "Idle"
    //   is ShooterFsmState.SpinningUp  -> "Spinning up to ${state.targetRpm} rpm"
    //   ShooterFsmState.Ready          -> "Ready to fire"
    //   is ShooterFsmState.Feeding     -> "Feeding at ${state.targetRpm} rpm"
    //
    // Notice the `is` keyword on the `data class` branches: it tests "is this
    // value an instance of SpinningUp?" and inside that branch, `state` is
    // smart-cast to `ShooterFsmState.SpinningUp`, so you can read
    // `state.targetRpm` without any explicit cast.
    //
    // Once your four branches are present, DELETE this `else` line — `when`
    // becomes exhaustive without it.
    else -> TODO("complete the when branches and remove this else")
}
