# Check: Vision Gating

Your pose estimator believes the robot is at `(4.0, 3.0)`. One frame later
a vision measurement arrives claiming the robot is at `(9.2, 1.0)` — more
than 5 meters away. The measurement is fresh (well under 0.5 s old) and its
`translationStdDev` is low, so task 6's stale and imprecise gates both
pass.

Should `integrateVisionMeasurement` apply this measurement to the
estimator?
