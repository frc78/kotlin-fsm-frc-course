# On a Real Robot: Phoenix6 Notes

Everything you configured in this lesson transfers to real Phoenix6: same
config blocks, same field names, same build-one-configuration-apply-it-once
rule. This course's stubs flatten a few things so tasks compile without the
FRC toolchain. Here are the differences in one place.

## Constructor

The stub takes `TalonFX(canId = 3)`. The real constructor is `TalonFX(3)`
or `TalonFX(3, "canivore")`. The second argument names the CAN bus. There is
no `canId` parameter name in the real class.

## StatusSignal reads

The stub's `motor.getVelocity()` returns a plain `Double`. Real Phoenix6
wraps every sensor read in a `StatusSignal` object that carries the value,
a timestamp, and error info. You write `motor.velocity.valueAsDouble` (or
`motor.statorCurrent.valueAsDouble`, and so on). Same number, one extra hop.
This applies to *every* read: position, velocity, current, temperature.

## apply() returns a StatusCode

The stub's `configurator.apply(config)` returns nothing. The real one
returns a `StatusCode`. Teams check it, and many retry the apply a few times
at startup, because a CAN bus can be busy when the robot boots.

The stub's `configurator.appliedConfig` exists only so tests can inspect
what you applied. The real API has no such property. To read a
configuration back from a real motor, call `configurator.refresh(config)`.

## Mutable request objects

The stub's `VelocityVoltage` is a Kotlin `data class`, so the course makes
a fresh one per call. The real class is *mutable*. Teams construct it once,
keep it in a field, and update the target each loop with
`.withVelocity(rps)`. Real requests also pick their gain slot with
`.withSlot(n)`. Slot 0 is the default, so configuring `Slot0` was all the
flywheel in task 4 needed.

## Current limit defaults flipped in 2025

The stub ships with current limits disabled and zeroed, like classic
Phoenix6. Since the 2025 release, real TalonFXs default to *enabled* limits:
70 A supply, 120 A stator. Either way, task 3's rule stands. Never depend on
a default. Set the limit *and* its enable flag explicitly, in the same
configuration.

## Simulation

Tests in this course call `simulateVelocity(...)` and `simulatePosition(...)`
on the stub. On a real robot project, you get the same effect from
`motor.simState`, which has `setRotorVelocity(...)` and
`setRawRotorPosition(...)`.

## What to learn next: MotionMagic

Real mechanisms rarely run bare `PositionVoltage`. Phoenix6's
**MotionMagic** is position control with a built-in motion profile. It ramps
velocity up and down smoothly instead of applying full effort at the error.
The configuration has the shape you already know: a `MotionMagic` config
block (cruise velocity, acceleration), then a `MotionMagicVoltage` request.
It is the first thing to reach for on a real elevator.

## Try it

`src/RealPhoenix6.kt` rebuilds the whole lesson: motor output, current
limits, and Slot0 gains applied in one `configurator.apply(...)`. Run
`main()`, read the printout, then change something (disable the supply
limit, raise `kV`) and run it again.
