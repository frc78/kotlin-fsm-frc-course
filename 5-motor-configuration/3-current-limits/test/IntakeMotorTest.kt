package course.l5t3

import frc.stubs.NeutralModeValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class IntakeMotorTest {
    @Test fun configure_applies_a_configuration() {
        IntakeMotor.configure()
        assertNotNull(IntakeMotor.motor.configurator.appliedConfig)
    }

    @Test fun neutral_mode_is_coast() {
        IntakeMotor.configure()
        val applied = IntakeMotor.motor.configurator.appliedConfig!!
        assertEquals(NeutralModeValue.Coast, applied.MotorOutput.NeutralMode)
    }

    @Test fun supply_current_limit_is_forty_amps() {
        IntakeMotor.configure()
        val applied = IntakeMotor.motor.configurator.appliedConfig!!
        assertEquals(40.0, applied.CurrentLimits.SupplyCurrentLimit)
    }

    @Test fun supply_current_limit_is_enabled() {
        IntakeMotor.configure()
        val applied = IntakeMotor.motor.configurator.appliedConfig!!
        assertTrue(
            applied.CurrentLimits.SupplyCurrentLimitEnable,
            "SupplyCurrentLimitEnable must be true — setting the value alone does nothing"
        )
    }
}
