package course.l2t2

import frc.stubs.VoltageOut
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class IntakeIntroTest {
    @BeforeTest fun setUp() {
        IntakeIntro.reset()
    }

    @Test fun starts_idle() {
        IntakeIntro.periodic()
        assertEquals(IntakeIntro.State.IDLE, IntakeIntro.state)
        assertEquals(VoltageOut(0.0), IntakeIntro.motor.lastRequest)
    }

    @Test fun command_starts_intaking() {
        IntakeIntro.commandedIntake = true
        IntakeIntro.periodic()
        assertEquals(IntakeIntro.State.INTAKING, IntakeIntro.state)
        assertEquals(VoltageOut(6.0), IntakeIntro.motor.lastRequest)
    }

    @Test fun close_canrange_transitions_to_holding() {
        IntakeIntro.commandedIntake = true
        IntakeIntro.periodic()
        IntakeIntro.canRange.simulateDistance(0.03)
        IntakeIntro.periodic()
        assertEquals(IntakeIntro.State.HOLDING, IntakeIntro.state)
        assertEquals(VoltageOut(1.0), IntakeIntro.motor.lastRequest)
    }

    @Test fun far_canrange_does_not_transition_to_holding() {
        IntakeIntro.commandedIntake = true
        IntakeIntro.periodic()
        IntakeIntro.canRange.simulateDistance(0.20)
        IntakeIntro.periodic()
        assertEquals(IntakeIntro.State.INTAKING, IntakeIntro.state)
    }

    @Test fun release_returns_to_idle_from_intaking() {
        IntakeIntro.commandedIntake = true
        IntakeIntro.periodic()
        IntakeIntro.commandedIntake = false
        IntakeIntro.periodic()
        assertEquals(IntakeIntro.State.IDLE, IntakeIntro.state)
    }

    @Test fun release_returns_to_idle_from_holding() {
        IntakeIntro.commandedIntake = true
        IntakeIntro.periodic()
        IntakeIntro.canRange.simulateDistance(0.03)
        IntakeIntro.periodic()
        IntakeIntro.commandedIntake = false
        IntakeIntro.periodic()
        assertEquals(IntakeIntro.State.IDLE, IntakeIntro.state)
    }
}
