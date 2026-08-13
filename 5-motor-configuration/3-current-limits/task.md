# Current Limits

A Falcon 500 will happily pull 200+ amps if you ask it to — long enough to
trip a breaker, brown out the rio, or burn a motor. **Current limits are
not optional** on a real robot.

There are two limits worth knowing:

- **Supply current limit** — caps current drawn from the *battery*. This
  is what protects breakers and prevents brownouts. Most teams set this
  somewhere around 40 A on intakes, rollers, and indexers.
- **Stator current limit** — caps current through the *motor windings*.
  This is what protects the motor itself from cooking.

For most subsystems, a supply limit is enough. Set it, enable it, done.

## The CurrentLimits config block

Current limits live on `TalonFXConfiguration.CurrentLimits`. The fields
relevant to a supply limit are:

- `SupplyCurrentLimit` — the cap, in amps (`Double`).
- `SupplyCurrentLimitEnable` — a `Boolean` that turns the limit on.

The `Enable` flag is the gotcha: the limit value means nothing unless
the matching `Enable` flag is `true`. Older Phoenix6 versions (and this
course's stub) ship with limits *disabled* by default; since the 2025
release the real API defaults to *enabled* limits (70 A supply, 120 A
stator). Don't lean on either default — **set both fields explicitly in
the same configuration**, so your config says what it means.

You can layer current limits on top of motor-output settings in a single
`TalonFXConfiguration` — `MotorOutput`, `CurrentLimits`, and any other
config blocks all live on the same configuration object, mutated inside
one `.apply { ... }`.

## Your task

Open `src/IntakeMotor.kt`. The `motor` is already declared on CAN ID 22.
Implement `configure()` so the applied configuration has:

- Neutral mode set to **`Coast`** (intakes free-spin when disabled).
- Supply current limit set to **40 amps**.
- Supply current limit **enabled**.

## Hints

- One `TalonFXConfiguration().apply { ... }` block is enough — you
  don't need to apply multiple separate configurations.
- Setting the limit value but forgetting the `Enable` flag is the most
  common configuration bug in this whole API — real TalonFXs only
  started shipping with limits enabled in 2025, and this course's stub
  still defaults to disabled. The test will catch it.
