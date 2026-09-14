# Sealed Classes: When Enums Aren't Enough

In the previous task, every elevator state had a *fixed* setpoint chosen at
compile time. That works because the elevator only goes to a few preset heights.

But what if a state's data is decided at *runtime*? For example, a shooter's
flywheel target RPM might depend on the distance to the goal, so it differs on
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
`Idle` and `Ready` use `data object` (no per-instance data), and
`SpinningUp` and `Feeding` use `data class` (each carries a `targetRpm`). The
Lesson 3 Shooter is built on a hierarchy shaped just like this one.

## Your task

Complete the `describe(state)` function. It uses `when` on a sealed type
to return a different string for each subclass:

| State          | Returned string                       |
|----------------|---------------------------------------|
| `Idle`         | `"Idle"`                              |
| `SpinningUp`   | `"Spinning up to <rpm> rpm"`          |
| `Ready`        | `"Ready to fire"`                     |
| `Feeding`      | `"Feeding at <rpm> rpm"`              |

Use a string template to interpolate the `targetRpm` value carried by
each `SpinningUp` and `Feeding` instance.

Two things to know about `when` over a sealed class:

1. **`data object` branches don't need `is`.** Since there's only one
   instance of `Idle` ever, the match goes by identity:
   `ShooterFsmState.Idle -> ...`.
2. **`data class` branches use `is`** because there are many possible
   instances (`SpinningUp(4500.0)`, `SpinningUp(5200.0)`, etc.). Inside an
   `is`-branch, `state` is **smart-cast** to that subclass, so you can read
   `state.targetRpm` without writing any explicit cast.

When all four branches are present, the `when` is exhaustive. Delete the
`else` line at the bottom.

## Why this matters for FSMs

When a state carries *runtime* data (a target, a deadline, an error message),
sealed classes keep that data with the state. You don't need a separate
`targetRpm` field on the subsystem that goes stale or out of sync with the
state.
