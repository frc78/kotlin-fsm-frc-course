# Enums with Properties

A plain enum is a fixed list of names. But Kotlin enums can also carry *data* —
each value passes arguments to a constructor, just like a class.

```kotlin
enum class Color(val hex: String) {
    RED("#ff0000"),
    GREEN("#00ff00"),
    BLUE("#0000ff");
}
```

This pattern is incredibly useful for FRC subsystems with **setpoints**. Instead
of writing scattered code like:

```kotlin
val target = when (state) {
    ElevatorState.STOWED -> 0.0
    ElevatorState.LOW -> 4.0
    // ...
}
```

…you put the setpoint *on the state itself*. Then the subsystem just reads
`state.targetRotations`. Less code, fewer places to mismatch.

## Your task

In `src/ElevatorState.kt`, declare an enum where each value carries its own
target position (in motor rotations):

| State    | targetRotations |
|----------|-----------------|
| `STOWED` |  0.0            |
| `LOW`    |  4.0            |
| `MID`    |  9.5            |
| `HIGH`   | 14.5            |

The constructor is already wired up — you just need to fill in the values.

## Hints

- Syntax for an enum value with constructor args:
  `STOWED(targetRotations = 0.0)`. The named argument is optional but reads well.
- The starter file already ends the entry list with a semicolon (`;`) — write
  your four values *before* it. Kotlin only requires that trailing semicolon
  when the enum body goes on to declare members (functions or properties)
  after the entries; here it's optional, but harmless.
