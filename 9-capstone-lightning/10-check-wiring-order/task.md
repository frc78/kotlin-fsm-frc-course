# Check: One Loop Stale

Task 8's `Lightning.periodic()` runs five steps, and step 3 —
`MiniSuperstructure.coralInGripper = MiniGripper.hasCoral()` — is wedged in
tight: after `MiniGripper.periodic()` (step 2), before
`MiniSuperstructure.periodic()` (step 4). The hidden `full_coral_cycle` test
fails if you slide it anywhere else.

Why must that assignment sit exactly between those two `periodic()` calls?
