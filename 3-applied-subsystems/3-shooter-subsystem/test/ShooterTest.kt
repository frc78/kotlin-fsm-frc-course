package course.l3t3

import frc.stubs.NeutralOut
import frc.stubs.VelocityVoltage
import frc.stubs.VoltageOut
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ShooterTest {
    @BeforeTest fun setUp() {
        Shooter.reset()
    }

    @Test fun starts_idle_with_motors_neutral() {
        Shooter.periodic()
        assertEquals(FsmState.Idle, Shooter.state)
        assertEquals(NeutralOut, Shooter.flywheel.lastRequest)
        assertEquals(NeutralOut, Shooter.feeder.lastRequest)
    }

    @Test fun targeting_rpm_starts_spinning_up_with_velocity_request() {
        Shooter.commandedTargetRpm = 4500.0
        Shooter.periodic()
        assertEquals(FsmState.SpinningUp(4500.0), Shooter.state)
        assertEquals(VelocityVoltage(75.0), Shooter.flywheel.lastRequest)
        assertEquals(NeutralOut, Shooter.feeder.lastRequest)
    }

    @Test fun reaches_ready_when_velocity_close_to_target() {
        Shooter.commandedTargetRpm = 4500.0
        Shooter.periodic()
        Shooter.periodic()
        assertEquals(FsmState.Ready(4500.0), Shooter.state)
    }

    @Test fun stays_spinning_up_below_95_percent_of_target() {
        Shooter.commandedTargetRpm = 4500.0
        Shooter.periodic()
        // The stub snapped velocity to the 75.0 rps setpoint; drag it back below the band.
        Shooter.flywheel.simulateVelocity(70.0)
        Shooter.periodic()
        assertEquals(
            FsmState.SpinningUp(4500.0), Shooter.state,
            "70.0 rps is below 0.95 * 75.0 = 71.25 rps, so the shooter should still be SpinningUp, not Ready",
        )
    }

    @Test fun reaches_ready_at_exactly_95_percent_of_target() {
        Shooter.commandedTargetRpm = 4500.0
        Shooter.periodic()
        Shooter.flywheel.simulateVelocity(71.25)
        Shooter.periodic()
        assertEquals(
            FsmState.Ready(4500.0), Shooter.state,
            "71.25 rps is exactly 0.95 * 75.0 rps — the threshold check is >=, so this should count as Ready",
        )
    }

    @Test fun fire_starts_feeding() {
        Shooter.commandedTargetRpm = 4500.0
        Shooter.periodic()
        Shooter.periodic()
        Shooter.commandedFire = true
        Shooter.periodic()
        assertEquals(FsmState.Feeding(4500.0), Shooter.state)
        assertEquals(VoltageOut(8.0), Shooter.feeder.lastRequest)
        assertEquals(VelocityVoltage(75.0), Shooter.flywheel.lastRequest)
    }

    @Test fun release_fire_returns_to_ready_with_same_target() {
        Shooter.commandedTargetRpm = 4500.0
        Shooter.periodic()
        Shooter.periodic()
        Shooter.commandedFire = true
        Shooter.periodic()
        Shooter.commandedFire = false
        Shooter.periodic()
        assertEquals(FsmState.Ready(4500.0), Shooter.state)
    }

    @Test fun clearing_target_in_spinningup_returns_to_idle() {
        Shooter.commandedTargetRpm = 4500.0
        Shooter.periodic()
        Shooter.commandedTargetRpm = null
        Shooter.periodic()
        assertEquals(FsmState.Idle, Shooter.state)
    }

    @Test fun clearing_target_in_ready_returns_to_idle() {
        Shooter.commandedTargetRpm = 4500.0
        Shooter.periodic()
        Shooter.periodic()
        Shooter.commandedTargetRpm = null
        Shooter.periodic()
        assertEquals(FsmState.Idle, Shooter.state)
    }

    @Test fun changed_target_while_spinning_up_is_ignored() {
        Shooter.commandedTargetRpm = 4500.0
        Shooter.periodic()
        Shooter.flywheel.simulateVelocity(0.0)
        Shooter.commandedTargetRpm = 5400.0
        Shooter.periodic()
        assertEquals(
            FsmState.SpinningUp(4500.0), Shooter.state,
            "only Idle reads a new target. SpinningUp keeps the 4500.0 it was created with",
        )
    }

    @Test fun different_target_in_idle_yields_that_target() {
        Shooter.commandedTargetRpm = 5400.0
        Shooter.periodic()
        assertEquals(FsmState.SpinningUp(5400.0), Shooter.state)
    }
}
