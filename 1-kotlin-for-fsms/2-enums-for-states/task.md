# Enums for States

A finite state machine has a *finite* set of named states. Kotlin's `enum class`
is the simplest way to express that: a fixed list of named singletons.

```kotlin
enum class TrafficLight {
    RED, YELLOW, GREEN
}
```

You then refer to values as `TrafficLight.RED`, `TrafficLight.GREEN`, and so on.
Every value is a unique singleton — there's exactly one `RED`.

## Why enums for states

- The compiler knows the complete list of values, so a `when` on the enum can be
  *exhaustive* — forget a case and the code won't compile. You'll use this in
  the last task of this lesson, and in every subsystem from Lesson 2 on.
- Each value has a `.name` (the source-code identifier) and `.ordinal` (its
  position).
- `EnumClass.entries` is the list of all values (replacing the older `values()`
  method).

## Your task

In `src/IntakeState.kt`, declare an `enum class IntakeState` with four values,
in this order:

1. `IDLE` — nothing happening
2. `INTAKING` — running rollers in to grab a game piece
3. `HOLDING` — game piece secured, holding still
4. `EJECTING` — running rollers out to spit it back out

That's it — no properties, no methods. Just four named states.
