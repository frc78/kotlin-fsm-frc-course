package course.l5t2

import frc.stubs.InvertedValue
import frc.stubs.NeutralModeValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ArmMotorTest {
    @Test fun configure_applies_a_configuration() {
        ArmMotor.configure()
        val applied = ArmMotor.motor.configurator.appliedConfig
        assertNotNull(applied, "expected configurator.apply(...) to have been called")
    }

    @Test fun neutral_mode_is_brake() {
        ArmMotor.configure()
        val applied = ArmMotor.motor.configurator.appliedConfig!!
        assertEquals(NeutralModeValue.Brake, applied.MotorOutput.NeutralMode)
    }

    @Test fun direction_is_clockwise_positive() {
        ArmMotor.configure()
        val applied = ArmMotor.motor.configurator.appliedConfig!!
        assertEquals(InvertedValue.Clockwise_Positive, applied.MotorOutput.Inverted)
    }
}
