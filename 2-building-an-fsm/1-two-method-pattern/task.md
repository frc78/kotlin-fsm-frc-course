# The Two-Method Pattern

Every robot subsystem does two jobs on every periodic tick:

1. **Decide whether to change state**, from inputs and sensors.
2. **Drive the actuators**, from the current state.

The course splits these into two methods:

```kotlin
override fun periodic() {
    stateTransitions()  // 1. choose the next state
    stateActions()      // 2. drive the motors for that state
}
```

This split makes the code easy to read:

- `stateActions()` answers "what does the motor do in each state?"
- `stateTransitions()` answers "when do we leave each state?"

Each is a `when (state)` block. A new state adds one branch to each method.

## Read the skeleton

Open `src/Indexer.kt`. Three things in it are new since lesson 1.

- `object Indexer : Subsystem`. The colon means "Indexer implements
  Subsystem". `Subsystem` is an *interface* in `frc.stubs`. An interface is
  a list of functions that an object promises to have. `Subsystem` has one:
  `periodic()`. The robot loop calls `periodic()` on every subsystem, 50
  times a second.
- `override fun periodic()`. The word `override` marks the function that
  keeps the interface promise. Without it the code does not compile.
- `!` is the *not* operator. `!commandedIndex` is `true` when
  `commandedIndex` is `false`.

There is one indexer on the robot, so `Indexer` is an `object` (lesson 1,
task 6).

## The hardware

Two devices appear in this task:

- `TalonFX` is the motor controller. You command it by passing a *control
  request* to `motor.setControl(...)`. The simplest request is
  `VoltageOut(v)`, which applies `v` volts to the motor. Negative volts run
  it in reverse.
- The jam sensor is a `DigitalInput`, a beam-break sensor wired to a
  digital channel. `jamSensor.get()` returns `true` when the beam is
  tripped, which means something is stuck in the indexer.

## Your task

The state enum, the fields, and `periodic()` are written. Fill in the two
`when` blocks.

`stateActions()` drives the motor:

| State      | Motor                |
|------------|----------------------|
| `IDLE`     | `VoltageOut(0.0)`    |
| `INDEXING` | `VoltageOut(8.0)`    |
| `JAMMED`   | `VoltageOut(-3.0)`   |

`stateTransitions()` chooses the next state:

| Current      | Condition                          | Next       |
|--------------|------------------------------------|------------|
| `IDLE`       | `commandedIndex` is `true`         | `INDEXING` |
| `INDEXING`   | `jamSensor.get()` is `true`        | `JAMMED`   |
| `INDEXING`   | `commandedIndex` is `false`        | `IDLE`     |
| `JAMMED`     | `commandedIndex` is `false`        | `IDLE`     |

When a state has more than one row, check the conditions top to bottom. The
first match wins. If no condition matches, stay in the current state. A
cleared jam sensor alone does not leave `JAMMED`; only the driver's release
does.

## Hint

A `when` branch can return a value, so the whole assignment can be one
expression:

```kotlin
state = when (state) {
    State.SOMETHING -> if (someCondition) State.OTHER else State.SOMETHING
    // ...
}
```

The `if/else` inside each branch means "leave this state if the condition
is true; otherwise stay." In `stateActions()`, each branch is one
`motor.setControl(...)` call with no `if`.
