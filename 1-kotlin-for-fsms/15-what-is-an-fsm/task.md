# What Is an FSM?

You now have every ingredient this lesson set out to give you: enums name a
fixed set of states (task 9), enum properties attach compile-time data to them
(task 11), and sealed classes carry runtime data (task 12). Before Lesson 2 puts
those pieces to work on a real subsystem, let's zoom out and name the pattern
they all serve.

## Three parts, one idea

A **finite state machine** (FSM) organizes code around one rule: *at any
moment, the mechanism is in exactly one of a small, named set of modes.*

1. **States** are the named modes. A washing machine is `FILLING`, `WASHING`,
   `DRAINING`, `SPINNING`, or `DONE` — never two at once, never something off
   the list. That "small, fixed, named" property is exactly what an
   `enum class` expresses.
2. **Transitions** are the rules for changing mode: "when the drum has spun
   long enough, go from `SPINNING` to `DONE`." Every rule is a triple —
   current state, condition, next state — and from Lesson 2 on you'll read
   and write them as tables.
3. **Actions** are what each mode *does*: `FILLING` opens the water valve,
   `SPINNING` runs the drum flat out. One state, one behavior.

## Why robot subsystems fit this shape

Think about any FRC mechanism: an intake, an elevator, a shooter. Each is one
mechanism with a handful of modes — an intake is idle, intaking, holding, or
ejecting (you declared exactly those four in task 9). And what flips it
between modes is always some input: the driver holds a button, a beam-break
sees the game piece, a timer expires. States, transitions, actions. An FSM
isn't something you impose on a subsystem — it's what a subsystem already
*is*. Writing it as one makes the code say so out loud.

## The two-method pattern (your Lesson 2 preview)

Every subsystem in this course runs the same loop, once per robot tick:

```kotlin
fun periodic() {
    stateTransitions()  // read inputs and sensors, decide the next state
    stateActions()      // command the hardware for whatever state we're in
}
```

`stateTransitions()` *decides*; `stateActions()` *acts*. Each is a
`when (state)` — and because `when` over an enum is exhaustive, adding a new
state means the compiler walks you to every branch that needs updating.

## Run the toy

Open `src/FsmPlayground.kt`: a complete washing-machine FSM in about twenty
lines — an enum of states, a `transition()` function, an `action()` function,
and a `main()` that ticks the machine through a full cycle, printing the
state each tick. Run `main` and watch the cycle scroll by.

Then change it: add a `RINSING` state between `DRAINING` and `SPINNING`. The
moment you add the enum value, the compiler flags both `when` blocks as
non-exhaustive — that's the safety net from task 9 working for you. Next
lesson you build this exact shape into a real subsystem.
