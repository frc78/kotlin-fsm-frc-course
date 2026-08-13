# The Two-Method Pattern

You've got the FSM ingredients (states, transitions, exhaustive `when`). Now
let's see how the team actually structures a subsystem.

Almost every robot subsystem has the same two responsibilities every periodic
tick:

1. **Drive the actuators** based on the *current* state.
2. **Decide whether to change state** based on inputs and sensors.

The team splits these into two methods:

```kotlin
override fun periodic() {
    stateTransitions()  // 1. choose next state
    stateActions()      // 2. drive motors based on state
}
```

This split makes the code easy to skim:

- Reading `stateActions()` answers *"what does the motor do in each state?"*
- Reading `stateTransitions()` answers *"when do we leave each state?"*

Each is a `when (state)` block, so adding a new state means adding one branch in
each method.

## Subsystems as `object`s

There's only one indexer on the robot. We model that as a Kotlin `object` — a
singleton:

```kotlin
object Indexer : Subsystem {
    // ...
}
```

`Subsystem` (in the `frc.stubs` package) is just `interface Subsystem { fun periodic() }`.

## The hardware

Two devices show up in this task:

- `TalonFX` is the motor controller. You command it by passing a *control
  request* to `motor.setControl(...)`. The simplest request is
  `VoltageOut(v)`, which applies `v` volts to the motor — negative volts
  run it in reverse.
- The jam sensor is a `DigitalInput` — a beam-break sensor wired to a
  digital channel. `jamSensor.get()` returns `true` when the beam is
  tripped (something is stuck in the indexer).

## Your task

Open `src/Indexer.kt`. The state enum, fields, and `periodic()` are wired up.
You need to fill in the two `when` blocks:

`stateActions()` — drive the motor:

| State      | Motor                |
|------------|----------------------|
| `IDLE`     | `VoltageOut(0.0)`    |
| `INDEXING` | `VoltageOut(8.0)`    |
| `JAMMED`   | `VoltageOut(-3.0)`   |

`stateTransitions()` — choose the next state:

| Current      | Condition                          | Next       |
|--------------|------------------------------------|------------|
| `IDLE`       | `commandedIndex` is `true`         | `INDEXING` |
| `INDEXING`   | `jamSensor.get()` is `true`        | `JAMMED`   |
| `INDEXING`   | `commandedIndex` is `false`        | `IDLE`     |
| `JAMMED`     | `commandedIndex` is `false`        | `IDLE`     |

When a state has more than one row, check the conditions top to bottom — the
first match wins. If no condition matches, stay in the current state.

## Hint

A `when` branch can return a value, so the whole assignment can be one
expression:

```kotlin
state = when (state) {
    State.SOMETHING -> if (someCondition) State.OTHER else State.SOMETHING
    // ...
}
```

The `if/else` inside each branch encodes "leave this state if the
condition is true; otherwise stay." For `stateActions()`, each branch is
a `motor.setControl(...)` call — no `if`, just one `setControl` per
state.
