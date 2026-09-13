package course.l5t4

import frc.stubs.VelocityVoltage
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FlywheelTest {
    @BeforeTest fun setUp() {
        Flywheel.reset()
    }

    @Test fun configure_applies_a_configuration() {
        Flywheel.configure()
        assertNotNull(Flywheel.motor.configurator.appliedConfig, "expected configurator.apply(...) to have been called")
    }

    @Test fun slot0_kv_is_set() {
        Flywheel.configure()
        val applied = Flywheel.motor.configurator.appliedConfig!!
        assertEquals(0.12, applied.Slot0.kV, "Slot0.kV must be 0.12")
    }

    @Test fun slot0_kp_is_set() {
        Flywheel.configure()
        val applied = Flywheel.motor.configurator.appliedConfig!!
        assertEquals(0.25, applied.Slot0.kP, "Slot0.kP must be 0.25")
    }

    @Test fun run_at_rps_sends_velocity_voltage() {
        Flywheel.runAtRps(80.0)
        assertEquals(VelocityVoltage(80.0), Flywheel.motor.lastRequest, "runAtRps(80.0) must send VelocityVoltage(80.0)")
    }

    @Test fun run_at_rps_passes_value_through() {
        Flywheel.runAtRps(42.5)
        assertEquals(VelocityVoltage(42.5), Flywheel.motor.lastRequest, "runAtRps(42.5) must send VelocityVoltage(42.5)")
    }
}
