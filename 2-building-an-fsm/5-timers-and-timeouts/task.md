# Timers and Timed States

Your transitions so far have fired on two kinds of input: driver intent
(`commandedIntake`) and sensor reads (`canRange.getDistance()`). This task adds
the third classic trigger: **time**.

Real robots use timed states. Team 2056's 2025
[technical binder](https://2056.ca/wp-content/uploads/2025/05/OPR25-2056-Technical-Binder.pdf)
lists timers next to buttons and sensors as transition triggers.

Here we build a timed **eject**. The driver *taps* the eject button once; the
intake spits the piece backward for exactly half a second, then returns to
`IDLE` on its own. The FSM **latches** the request — the driver doesn't hold
the button, and releasing it early doesn't cut the eject short.

## The Timer API

`frc.stubs.Timer` mirrors WPILib's `edu.wpi.first.wpilibj.Timer`:

```kotlin
val t = Timer()
t.start()                   // begin accumulating time
t.stop()                    // pause accumulation
t.reset()                   // zero the clock (doesn't start or stop it)
t.restart()                 // reset() then start() — the usual one-liner
t.get(): Double             // seconds accumulated so far
t.hasElapsed(s): Boolean    // true once get() >= s
```

> **Real WPILib detail:** the real `Timer` reads the robot's FPGA clock, so it
> advances on its own. The stub only advances when a test calls
> `simulateAdvance(seconds)`. Same API, but the tests control time, which
> makes timing logic checkable tick by tick.

> **Real Phoenix6 detail:** this course writes `VoltageOut(-8.0)` on every
> tick, which creates a new request object each time. Real robot code keeps
> one request object per motor and updates it:
> `motor.setControl(ejectRequest.withOutput(-8.0))`. Creating objects 50
> times a second on the roboRIO wastes memory and time.

## Never block `periodic()`

Why not just `Thread.sleep(500)` in the eject state? Because `periodic()` runs
every 20 ms for *every* subsystem on the robot. A sleep anywhere freezes the
whole loop — no driving, no other transitions, no safety checks. `periodic()`
must always return immediately.

So instead of *waiting* for time to pass, the FSM *polls*: start a timer when
the state begins, and each tick `stateTransitions()` asks
`ejectTimer.hasElapsed(...)` — "is it done yet?" — and moves on either way.

## Starting the timer exactly once

`restart()` must run **once, on entering `EJECTING`** — not every tick, or the
clock would be zeroed 50 times a second and never elapse. That is exactly the
entry-side-effect pattern from task 3: compare `state` to `previousState`.

`periodic()` is already wired as `stateTransitions()` → `onEnter()` →
`stateActions()`, and the `previousState = state` bookkeeping at the end is
pre-written. Your `onEnter()` only has to notice the edge.

## Your task

Open `src/EjectingIntake.kt`. Implement all three methods.

**`stateActions()`:**

| State      | Motor              |
|------------|--------------------|
| `IDLE`     | `VoltageOut(0.0)`  |
| `INTAKING` | `VoltageOut(6.0)`  |
| `EJECTING` | `VoltageOut(-8.0)` |

**`stateTransitions()`** (priority order — the first matching row wins):

| Current    | Condition                    | Next       |
|------------|------------------------------|------------|
| `IDLE`     | `commandedEject`             | `EJECTING` |
| `IDLE`     | `commandedIntake`            | `INTAKING` |
| `INTAKING` | `commandedEject`             | `EJECTING` |
| `INTAKING` | `!commandedIntake`           | `IDLE`     |
| `EJECTING` | `ejectTimer.hasElapsed(0.5)` | `IDLE`     |

If no row matches, stay in the current state. Notice what's *not* in the
`EJECTING` row: any button. Once ejecting, the only way out is the timer —
that's the latch.

**`onEnter()`:** on the tick the FSM *enters* `EJECTING` (it wasn't `EJECTING`
on the previous tick), `restart()` the `ejectTimer`. Every other tick — a
different transition, or a quiet tick inside a state — does nothing.

`restart()` matters on *re-entry* too: the second eject of a match must get a
fresh 0.5 seconds, not whatever was left on the clock from the first one.
