# Enums for States

You know four types: `Int`, `Double`, `Boolean`, `String`. Now you make a
type of your own. An **enum** is a type with a fixed list of named values,
and nothing else. It is the simplest way to say "this thing is in exactly
one of these modes".

```kotlin
enum class TrafficLight {
    RED, YELLOW, GREEN
}
```

`TrafficLight` is now a type, like `Boolean`. A `Boolean` has two possible
values. A `TrafficLight` has three: `TrafficLight.RED`, `TrafficLight.YELLOW`,
and `TrafficLight.GREEN`. There is exactly one `RED`. A finite state
machine has a *finite* set of named states, so an enum is the natural fit.

## Why enums for states

- The compiler knows the complete list of values, so a `when` on the enum can be
  *exhaustive*: forget a case and the code won't compile. You will use this in
  task 10, and in every subsystem from Lesson 2 on.
- Each value has a `.name` (the source-code identifier) and `.ordinal` (its
  position).
- `EnumClass.entries` is the list of all values (replacing the older `values()`
  method).

## Your task

In `src/IntakeState.kt`, declare an `enum class IntakeState` with four values,
in this order:

1. `IDLE`: nothing happening
2. `INTAKING`: running rollers in to grab a game piece
3. `HOLDING`: game piece secured, holding still
4. `EJECTING`: running rollers out to spit it back out

The enum has four named states and nothing else.
