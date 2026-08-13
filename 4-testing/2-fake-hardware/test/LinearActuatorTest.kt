package course.l4t2

import frc.stubs.DigitalInput
import frc.stubs.TalonFX
import frc.stubs.VoltageOut
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LinearActuatorTest {

    private lateinit var motor: TalonFX
    private lateinit var limit: DigitalInput
    private lateinit var actuator: LinearActuator

    @BeforeTest fun setUp() {
        motor = TalonFX(canId = 99)
        limit = DigitalInput(channel = 9)
        actuator = LinearActuator(motor, limit)
    }

    @Test fun starts_retracted_with_motor_off() {
        actuator.periodic()
        assertEquals(
            LinearActuator.State.RETRACTED, actuator.state,
            "With no extend command, the actuator should stay RETRACTED"
        )
        assertEquals(
            VoltageOut(0.0), motor.lastRequest,
            "RETRACTED should command VoltageOut(0.0)"
        )
    }

    @Test fun command_starts_extending() {
        actuator.commandedExtend = true
        actuator.periodic()
        assertEquals(
            LinearActuator.State.EXTENDING, actuator.state,
            "RETRACTED with commandedExtend should go to EXTENDING"
        )
        assertEquals(
            VoltageOut(6.0), motor.lastRequest,
            "EXTENDING should command VoltageOut(6.0)"
        )
    }

    @Test fun limit_hit_transitions_to_extended() {
        actuator.commandedExtend = true
        actuator.periodic()
        limit.simulateValue(true)
        actuator.periodic()
        assertEquals(
            LinearActuator.State.EXTENDED, actuator.state,
            "EXTENDING with the limit switch hit should go to EXTENDED"
        )
        assertEquals(
            VoltageOut(0.0), motor.lastRequest,
            "EXTENDED should command VoltageOut(0.0)"
        )
    }

    @Test fun release_returns_to_retracted() {
        actuator.commandedExtend = true
        actuator.periodic()
        limit.simulateValue(true)
        actuator.periodic()
        actuator.commandedExtend = false
        actuator.periodic()
        assertEquals(
            LinearActuator.State.RETRACTED, actuator.state,
            "EXTENDED with the extend command released should return to RETRACTED"
        )
    }

    @Test fun stays_extending_until_limit_hit() {
        actuator.commandedExtend = true
        actuator.periodic()
        actuator.periodic()
        assertEquals(
            LinearActuator.State.EXTENDING, actuator.state,
            "EXTENDING should hold until limit.get() is true — not advance to EXTENDED on its own"
        )
        assertEquals(
            VoltageOut(6.0), motor.lastRequest,
            "While still EXTENDING the motor should keep commanding VoltageOut(6.0)"
        )
    }

    @Test fun holds_extended_while_command_held() {
        actuator.commandedExtend = true
        actuator.periodic()
        limit.simulateValue(true)
        actuator.periodic()
        actuator.periodic()
        assertEquals(
            LinearActuator.State.EXTENDED, actuator.state,
            "EXTENDED should hold while commandedExtend is still true — not fall back to RETRACTED"
        )
    }

    @Test fun two_actuators_are_independent() {
        // The point of DI: two instances, no shared state.
        val motor2 = TalonFX(canId = 100)
        val limit2 = DigitalInput(channel = 10)
        val actuator2 = LinearActuator(motor2, limit2)

        actuator.commandedExtend = true
        actuator2.commandedExtend = false
        actuator.periodic()
        actuator2.periodic()

        assertEquals(
            LinearActuator.State.EXTENDING, actuator.state,
            "The commanded actuator should be EXTENDING, independent of the other instance"
        )
        assertEquals(
            LinearActuator.State.RETRACTED, actuator2.state,
            "The un-commanded actuator should stay RETRACTED, independent of the other instance"
        )
        assertEquals(
            VoltageOut(6.0), motor.lastRequest,
            "The commanded actuator's motor should get VoltageOut(6.0)"
        )
        assertEquals(
            VoltageOut(0.0), motor2.lastRequest,
            "The un-commanded actuator's motor should get VoltageOut(0.0)"
        )
    }
}
