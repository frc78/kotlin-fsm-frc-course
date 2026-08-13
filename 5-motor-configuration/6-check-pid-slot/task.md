# Check: Which Request Runs Closed Loop?

Your flywheel is configured exactly as in task 4: `Slot0` gains of
`kV = 0.12` and `kP = 0.25`, applied to the motor. Now you want it
spinning at a target of 50 rotations per second — and *holding* that
speed as the battery sags and game pieces drag it down.

Which control request makes the TalonFX run its onboard closed loop
using the Slot0 gains you configured?
