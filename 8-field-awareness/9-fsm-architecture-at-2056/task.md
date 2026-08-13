# FSM Architecture at 2056

You now have the whole toolbox: FSMs (Lessons 1–4), motor configuration
(5), swerve requests (6), coordination (7), and field awareness (this
lesson). Before Lesson 9 assembles a full robot — a recreation of FRC team
2056's 2025 robot LIGHTNING — look at how that team organizes all of these
pieces. Everything below comes from their own
[Chief Delphi Q&A](https://www.chiefdelphi.com/t/team-2056-op-robotics-2025-technical-binder-release/502550)
about their [technical binder](https://2056.ca/wp-content/uploads/2025/05/OPR25-2056-Technical-Binder.pdf).

## One pattern, everywhere

Every mechanism on LIGHTNING — intake, straightenator, gripper,
superstructure, climber, even the drivebase's mode selection — is a
**singleton subsystem running its state machine every robot loop**. That's
the shape you've written since Lesson 2: decide the next state, then act
on the current one. No command framework, no event bus; the loop *is* the
framework.

## Per-state rows, then global rows

Transitions that only make sense in one state live inside that state's
branch of the `when`; transitions that are legal from *anywhere* — a stow
button, a fault — are checked **after** it, as global rows. The capstone's
superstructure uses exactly this split.

## Coordination is just reading state

How do seven machines cooperate without a framework? Mentor Tyler
Holtzman, in the Q&A: *"If one subsystem is entirely dependent on
another, we just get an instance of the other subsystem so we can set or
get variables/states from it."* A consumer FSM simply treats another
machine's `state` like a sensor. The only rule: update the producer
before the consumer each loop, or the consumer reacts one tick late.

## When two machines become one

Reading someone else's state has a limit. LIGHTNING's elevator and gripper
wrist are so intertwined that neither has a meaningful state alone — every
scoring pose is a height *and* an angle pair. Holtzman again: *"If two
subsystems truly depend on each other... you really just have one big
subsystem and it should be a single state machine that controls both
mechanisms."* Co-dependent mechanisms don't coordinate; they merge.

## Anything can be a trigger

Across the Q&A and binder, LIGHTNING's transitions fire on driver
**buttons**, **sensors** (beam breaks ending a pickup), **timers** (a
fixed-time unjam), **motor current** (a stator spike means "holding an
algae"), **motor position and velocity** (mechanism at target), **motor
temperature** (protection faults), **other FSMs' states**, and **FMS/match
info**. If your code can read it, your FSM can transition on it.

## Run the demo

`src/ArchitectureDemo.kt` is the coordination pattern in miniature: two
toy FSMs where `Feeder`'s transition reads `Intake.state`. Run `main()`
and watch the feeder start feeding on the same tick the intake reports
`HOLDING`; then swap the two `periodic()` calls and run again — the feeder
goes one tick stale. Lesson 9 builds LIGHTNING exactly this way.
