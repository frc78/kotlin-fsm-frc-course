# Check: The Timer Latch

From task 5's `EjectingIntake`: the robot is `IDLE` and the driver *taps* the
eject button — `commandedEject` is `true` for exactly one tick, then back to
`false`. The FSM enters `EJECTING`, and `onEnter()` restarts `ejectTimer`.

It is now 0.3 seconds since that tap. No button is pressed.

What state is the intake in?
