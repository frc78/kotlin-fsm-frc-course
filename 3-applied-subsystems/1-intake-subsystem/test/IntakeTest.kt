package course.l3t1

import frc.stubs.VoltageOut
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class IntakeTest {
    @BeforeTest fun setUp() {
        Intake.reset()
    }

    @Test fun idle_at_start() {
        Intake.periodic()
        assertEquals(Intake.State.IDLE, Intake.state)
        assertEquals(VoltageOut(0.0), Intake.motor.lastRequest)
    }

    @Test fun command_starts_intaking() {
        Intake.commandedIntake = true
        Intake.periodic()
        assertEquals(Intake.State.INTAKING, Intake.state)
        assertEquals(VoltageOut(8.0), Intake.motor.lastRequest)
    }

    @Test fun piece_detected_transitions_to_holding() {
        Intake.commandedIntake = true
        Intake.periodic()
        Intake.canRange.simulateDistance(0.03)
        Intake.periodic()
        assertEquals(Intake.State.HOLDING, Intake.state)
        assertEquals(VoltageOut(0.5), Intake.motor.lastRequest)
    }

    @Test fun piece_lost_returns_to_idle() {
        Intake.commandedIntake = true
        Intake.periodic()
        Intake.canRange.simulateDistance(0.03)
        Intake.periodic()
        Intake.canRange.simulateDistance(0.20)
        Intake.periodic()
        assertEquals(Intake.State.IDLE, Intake.state)
    }

    @Test fun release_intake_command_in_intaking_returns_to_idle() {
        Intake.commandedIntake = true
        Intake.periodic()
        Intake.commandedIntake = false
        Intake.periodic()
        assertEquals(Intake.State.IDLE, Intake.state)
    }

    @Test fun eject_from_holding() {
        Intake.commandedIntake = true
        Intake.periodic()
        Intake.canRange.simulateDistance(0.03)
        Intake.periodic()
        Intake.commandedIntake = false
        Intake.commandedEject = true
        Intake.periodic()
        assertEquals(Intake.State.EJECTING, Intake.state)
        assertEquals(VoltageOut(-8.0), Intake.motor.lastRequest)
    }

    @Test fun eject_from_idle() {
        Intake.commandedEject = true
        Intake.periodic()
        assertEquals(Intake.State.EJECTING, Intake.state)
    }

    @Test fun eject_release_returns_to_idle() {
        Intake.commandedEject = true
        Intake.periodic()
        Intake.commandedEject = false
        Intake.periodic()
        assertEquals(Intake.State.IDLE, Intake.state)
    }
}
