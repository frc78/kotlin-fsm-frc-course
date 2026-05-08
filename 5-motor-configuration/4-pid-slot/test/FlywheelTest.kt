package course.l5t4

import frc.stubs.VelocityVoltage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FlywheelTest {
    @Test fun configure_applies_a_configuration() {
        Flywheel.configure()
        assertNotNull(Flywheel.motor.configurator.appliedConfig)
    }

    @Test fun slot0_kv_is_set() {
        Flywheel.configure()
        val applied = Flywheel.motor.configurator.appliedConfig!!
        assertEquals(0.12, applied.Slot0.kV)
    }

    @Test fun slot0_kp_is_set() {
        Flywheel.configure()
        val applied = Flywheel.motor.configurator.appliedConfig!!
        assertEquals(0.25, applied.Slot0.kP)
    }

    @Test fun run_at_rps_sends_velocity_voltage() {
        Flywheel.runAtRps(80.0)
        assertEquals(VelocityVoltage(80.0), Flywheel.motor.lastRequest)
    }

    @Test fun run_at_rps_passes_value_through() {
        Flywheel.runAtRps(42.5)
        assertEquals(VelocityVoltage(42.5), Flywheel.motor.lastRequest)
    }
}
