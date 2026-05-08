package course.l5t1

import frc.stubs.NeutralOut
import frc.stubs.VoltageOut
import kotlin.test.Test
import kotlin.test.assertEquals

class RollerMotorTest {
    @Test fun motor_uses_can_id_17() {
        assertEquals(17, RollerMotor.motor.canId)
    }

    @Test fun run_forward_sends_six_volts() {
        RollerMotor.runForward()
        assertEquals(VoltageOut(6.0), RollerMotor.motor.lastRequest)
    }

    @Test fun run_reverse_sends_negative_six_volts() {
        RollerMotor.runReverse()
        assertEquals(VoltageOut(-6.0), RollerMotor.motor.lastRequest)
    }

    @Test fun stop_sends_neutral_out() {
        RollerMotor.runForward()
        RollerMotor.stop()
        assertEquals(NeutralOut, RollerMotor.motor.lastRequest)
    }
}
