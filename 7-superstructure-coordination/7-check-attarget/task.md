# Check: `atTarget()` Rolls Up

Your `Superstructure` is partway through a `STOWED → SCORE_L4` move. On this
tick, `elevator.atTarget()` returns `true` (it has settled at `HIGH`) and
`intake.requestReached()` returns `true` (the rollers were not asked to change) — but the
arm is still one tick away from `SCORE`, so `arm.atTarget()` returns `false`.

What must `Superstructure.atTarget()` return, and why?
