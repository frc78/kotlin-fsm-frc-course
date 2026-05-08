# Sealed Classes — When Enums Aren't Enough

In the previous task, every elevator state had a *fixed* setpoint chosen at
compile time. That works because the elevator only goes to a few preset heights.

But what if a state's data is decided at *runtime*? For example, a shooter's
flywheel target RPM might depend on the distance to the goal — different on
every shot. An enum can't express that.

**Sealed classes** can. Each subclass of a sealed class is its own type, with
its own constructor and properties:

```kotlin
sealed class Result
data class Success(val value: Int) : Result()
data object Empty : Result()
data class Failed(val reason: String) : Result()
```

Like an enum, the compiler knows the *complete* list of subclasses, so `when`
on a sealed type is exhaustive.

## Two flavors of subclass

- **`data object`** — a singleton with no per-instance data. Use for states
  that are simple markers (`Idle`, `Ready`).
- **`data class`** — a class with a constructor. Use for states that carry
  varying data (`SpinningUp(targetRpm)`).

Open `src/ShooterFsmState.kt` and read the sealed class declaration at the top.
Notice how `Idle` and `Ready` use `data object` (no per-instance data) and
`SpinningUp` and `Feeding` use `data class` (each carries a `targetRpm`). When
you build subsystems in Lesson 3, you'll write declarations like these
yourself.

## Your task

Complete the `describe(state)` function. It uses `when` on a sealed type:

```kotlin
fun describe(state: ShooterFsmState): String = when (state) {
    ShooterFsmState.Idle           -> "Idle"
    is ShooterFsmState.SpinningUp  -> "Spinning up to ${state.targetRpm} rpm"
    ShooterFsmState.Ready          -> "Ready to fire"
    is ShooterFsmState.Feeding     -> "Feeding at ${state.targetRpm} rpm"
}
```

Two things to notice:

1. **`data object` branches don't need `is`.** Since there's only one
   instance of `Idle` ever, `state -> ...` matches it directly.
2. **`data class` branches use `is`** because there are many possible
   instances (`SpinningUp(4500.0)`, `SpinningUp(5200.0)`, etc.). Inside an
   `is`-branch, `state` is **smart-cast** to that subclass — you can read
   `state.targetRpm` without writing any explicit cast.

When all four branches are present, the `when` is exhaustive — delete the
`else` line at the bottom.

## Why this matters for FSMs

When a state carries *runtime* data (a target, a deadline, an error message),
sealed classes keep that data with the state. You don't need a separate
`targetRpm` field on the subsystem that goes stale or out of sync with the
state.
