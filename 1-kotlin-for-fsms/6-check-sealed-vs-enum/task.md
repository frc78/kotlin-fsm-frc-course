# Check: Sealed class or enum?

Task 3 gave every elevator state a fixed setpoint, chosen once at declaration:
`LOW(4.0)`, `MID(9.5)`. Now your shooter needs something different: it computes
a fresh flywheel target for every shot — 4500 rpm up close, 5200 rpm from the
wing — and the *state itself* should remember which rpm it is currently
spinning up to.

Which tool models this state, and why?
