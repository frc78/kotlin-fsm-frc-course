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
        assertEquals(LinearActuator.State.RETRACTED, actuator.state)
        assertEquals(VoltageOut(0.0), motor.lastRequest)
    }

    @Test fun command_starts_extending() {
        actuator.commandedExtend = true
        actuator.periodic()
        assertEquals(LinearActuator.State.EXTENDING, actuator.state)
        assertEquals(VoltageOut(6.0), motor.lastRequest)
    }

    @Test fun limit_hit_transitions_to_extended() {
        actuator.commandedExtend = true
        actuator.periodic()
        limit.simulateValue(true)
        actuator.periodic()
        assertEquals(LinearActuator.State.EXTENDED, actuator.state)
        assertEquals(VoltageOut(0.0), motor.lastRequest)
    }

    @Test fun release_returns_to_retracted() {
        actuator.commandedExtend = true
        actuator.periodic()
        limit.simulateValue(true)
        actuator.periodic()
        actuator.commandedExtend = false
        actuator.periodic()
        assertEquals(LinearActuator.State.RETRACTED, actuator.state)
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

        assertEquals(LinearActuator.State.EXTENDING, actuator.state)
        assertEquals(LinearActuator.State.RETRACTED, actuator2.state)
        assertEquals(VoltageOut(6.0), motor.lastRequest)
        assertEquals(VoltageOut(0.0), motor2.lastRequest)
    }
}
