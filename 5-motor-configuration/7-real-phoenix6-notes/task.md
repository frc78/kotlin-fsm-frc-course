# On a Real Robot: Phoenix6 Notes

Everything you configured in this lesson transfers straight to real
Phoenix6 — same config blocks, same field names, same
build-one-configuration-apply-it-once discipline. But this course's stubs
deliberately flatten a few things so tasks compile without the FRC
toolchain. Those differences have been scattered through the course as
per-task notes; here they are in one place.

## StatusSignal reads

The stub's `motor.getVelocity()` returns a plain `Double`. Real Phoenix6
wraps every sensor read in a `StatusSignal` object carrying the value
plus timestamp and error info — you write
`motor.velocity.valueAsDouble` (or `motor.statorCurrent.valueAsDouble`,
and so on). Same number, one extra hop, and it applies to *every* read:
position, velocity, current, temperature.

## Mutable request objects

The stub's `VelocityVoltage` is a Kotlin `data class`, so the course
makes a fresh one per call. The real class is *mutable*: teams construct
it once, keep it in a field, and update the target each loop with
`.withVelocity(rps)`. Real requests also pick their gain slot with
`.withSlot(n)` — slot 0 is the default, which is why configuring `Slot0`
was all the flywheel in task 4 needed.

## Current limit defaults flipped in 2025

The stub ships with current limits disabled and zeroed, matching classic
Phoenix6. Since the 2025 release, real TalonFXs default to *enabled*
limits: 70 A supply, 120 A stator. Either way, task 3's rule stands —
never lean on a default. Set the limit *and* its enable flag explicitly,
in the same configuration.

## Deadbands on drive requests

When you reach swerve in Lesson 6: the real `FieldCentric` and
`RobotCentric` requests also take `withDeadband(...)` and
`withRotationalDeadband(...)` to ignore joystick noise near zero. The
stubs omit them; real drive code should not.

## What to learn next: MotionMagic

Real mechanisms rarely run bare `PositionVoltage`. Phoenix6's
**MotionMagic** is position control with a built-in motion profile: it
ramps velocity up and down smoothly instead of slamming full effort at
the error. Team 2056's
[technical binder](https://2056.ca/wp-content/uploads/2025/05/OPR25-2056-Technical-Binder.pdf)
shows them running Motion Magic position control on the mechanisms of
LIGHTNING — the robot you'll rebuild in Lesson 9. The configuration is
the same shape you already know: a `MotionMagic` config block (cruise
velocity, acceleration), then a `MotionMagicVoltage` request. It's the
first thing to reach for on a real elevator.

## Try it

`src/RealPhoenix6.kt` rebuilds the whole lesson: motor output, current
limits, and Slot0 gains applied in one `configurator.apply(...)`. Run
`main()`, read the printout, then tweak something (disable the supply
limit, bump `kV`) and run it again.
