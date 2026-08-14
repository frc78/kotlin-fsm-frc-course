# Check: Hysteresis

Task 1's intake uses two thresholds, not one: a piece is **detected** when
`canRange.getDistance() < 0.05`, and **lost** only when it reads `> 0.10`.

The intake is in `HOLDING`, and this tick the sensor reads **0.07 m**.

After `stateTransitions()` runs, what state is the intake in?
