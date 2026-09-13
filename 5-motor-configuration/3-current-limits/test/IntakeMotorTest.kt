package course.l5t3

import frc.stubs.NeutralModeValue
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class IntakeMotorTest {
    @BeforeTest fun setUp() {
        IntakeMotor.reset()
    }

    @Test fun configure_applies_a_configuration() {
        IntakeMotor.configure()
        assertNotNull(IntakeMotor.motor.configurator.appliedConfig, "expected configurator.apply(...) to have been called")
    }

    @Test fun neutral_mode_is_coast() {
        IntakeMotor.configure()
        val applied = IntakeMotor.motor.configurator.appliedConfig!!
        assertEquals(NeutralModeValue.Coast, applied.MotorOutput.NeutralMode, "MotorOutput.NeutralMode must be Coast")
    }

    @Test fun supply_current_limit_is_forty_amps() {
        IntakeMotor.configure()
        val applied = IntakeMotor.motor.configurator.appliedConfig!!
        assertEquals(40.0, applied.CurrentLimits.SupplyCurrentLimit, "CurrentLimits.SupplyCurrentLimit must be 40.0 A")
    }

    @Test fun supply_current_limit_is_enabled() {
        IntakeMotor.configure()
        val applied = IntakeMotor.motor.configurator.appliedConfig!!
        assertTrue(
            applied.CurrentLimits.SupplyCurrentLimitEnable,
            "SupplyCurrentLimitEnable must be true. Setting the value alone does nothing"
        )
    }
}
