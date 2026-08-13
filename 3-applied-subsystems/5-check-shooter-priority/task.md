# Check: Shooter Priority

Task 3's shooter is in `SpinningUp(4500.0)`. This tick, two rows of the
transition table match at once: the driver clears the target
(`commandedTargetRpm` is now `null`) *and* the flywheel's velocity finally
reaches the 95% ready band.

After `stateTransitions()` runs, what state is the shooter in?
