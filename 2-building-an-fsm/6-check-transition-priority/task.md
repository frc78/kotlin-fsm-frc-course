# Check: Transition Priority

Back to task 2's intake. It's `INTAKING`, and on a single tick two things
happen at once: `canRange.getDistance()` reads 0.03 m, a piece below the
0.05 m threshold, *and* the driver releases the button, so `commandedIntake`
is `false`. Both `INTAKING` rows in the transition table now match:

| Current    | Condition                       | Next      |
|------------|---------------------------------|-----------|
| `INTAKING` | `canRange.getDistance() < 0.05` | `HOLDING` |
| `INTAKING` | `!commandedIntake`              | `IDLE`    |

After `stateTransitions()` runs, what is the state?
