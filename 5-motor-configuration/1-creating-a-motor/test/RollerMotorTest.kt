package course.l5t1

import frc.stubs.NeutralOut
import frc.stubs.VoltageOut
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RollerMotorTest {
    @BeforeTest fun setUp() {
        RollerMotor.reset()
    }

    @Test fun motor_uses_can_id_17() {
        assertEquals(17, RollerMotor.motor.canId, "motor must be declared on CAN ID 17")
    }

    @Test fun run_forward_sends_six_volts() {
        RollerMotor.runForward()
        assertEquals(VoltageOut(6.0), RollerMotor.motor.lastRequest, "runForward() must send VoltageOut(6.0)")
    }

    @Test fun run_reverse_sends_negative_six_volts() {
        RollerMotor.runReverse()
        assertEquals(VoltageOut(-6.0), RollerMotor.motor.lastRequest, "runReverse() must send VoltageOut(-6.0)")
    }

    @Test fun stop_sends_neutral_out() {
        RollerMotor.runForward()
        RollerMotor.stop()
        assertEquals(NeutralOut, RollerMotor.motor.lastRequest, "stop() must release the motor with stopMotor(), which sends NeutralOut")
    }
}
