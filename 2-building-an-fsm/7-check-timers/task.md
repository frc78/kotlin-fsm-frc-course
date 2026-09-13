# Check: Timers

A student writes `onEnter()` for task 5's `EjectingIntake` with one change:
on entry to `EJECTING` it calls `ejectTimer.start()` instead of
`ejectTimer.restart()`. Nothing in the code ever calls `stop()` or
`reset()` on the timer.

The driver taps eject. The intake ejects for 0.5 seconds and returns to
`IDLE`. Two seconds later the driver taps eject again.

What happens on the second eject?
