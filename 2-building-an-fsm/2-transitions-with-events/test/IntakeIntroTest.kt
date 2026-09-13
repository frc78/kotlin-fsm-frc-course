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
        assertEquals(
            IntakeIntro.State.IDLE, IntakeIntro.state,
            "After reset() and one tick with no command the intake should be IDLE"
        )
        assertEquals(
            VoltageOut(0.0), IntakeIntro.motor.lastRequest,
            "IDLE should command VoltageOut(0.0)"
        )
    }

    @Test fun reset_clears_command_and_sensor() {
        IntakeIntro.commandedIntake = true
        IntakeIntro.canRange.simulateDistance(0.03)
        IntakeIntro.reset()
        assertEquals(
            false, IntakeIntro.commandedIntake,
            "reset() should set commandedIntake to false"
        )
        assertEquals(
            true, IntakeIntro.canRange.getDistance() > 0.05,
            "reset() should simulate a far distance so no piece is detected"
        )
        IntakeIntro.periodic()
        assertEquals(
            IntakeIntro.State.IDLE, IntakeIntro.state,
            "After reset() the intake should tick in IDLE"
        )
    }

    @Test fun command_starts_intaking() {
        IntakeIntro.commandedIntake = true
        IntakeIntro.periodic()
        assertEquals(
            IntakeIntro.State.INTAKING, IntakeIntro.state,
            "commandedIntake from IDLE should transition to INTAKING"
        )
        assertEquals(
            VoltageOut(6.0), IntakeIntro.motor.lastRequest,
            "INTAKING should command VoltageOut(6.0)"
        )
    }

    @Test fun close_canrange_transitions_to_holding() {
        IntakeIntro.commandedIntake = true
        IntakeIntro.periodic()
        IntakeIntro.canRange.simulateDistance(0.03)
        IntakeIntro.periodic()
        assertEquals(
            IntakeIntro.State.HOLDING, IntakeIntro.state,
            "A distance under 0.05 m while INTAKING should transition to HOLDING"
        )
        assertEquals(
            VoltageOut(1.0), IntakeIntro.motor.lastRequest,
            "HOLDING should command VoltageOut(1.0)"
        )
    }

    @Test fun far_canrange_does_not_transition_to_holding() {
        IntakeIntro.commandedIntake = true
        IntakeIntro.periodic()
        IntakeIntro.canRange.simulateDistance(0.20)
        IntakeIntro.periodic()
        assertEquals(
            IntakeIntro.State.INTAKING, IntakeIntro.state,
            "A distance of 0.20 m is not a piece; the intake should stay INTAKING"
        )
    }

    @Test fun piece_detected_and_button_released_on_same_tick_gives_holding() {
        IntakeIntro.commandedIntake = true
        IntakeIntro.periodic()
        IntakeIntro.canRange.simulateDistance(0.03)
        IntakeIntro.commandedIntake = false
        IntakeIntro.periodic()
        assertEquals(
            IntakeIntro.State.HOLDING, IntakeIntro.state,
            "The sensor row is listed first for INTAKING, so it wins over the release row on the same tick"
        )
    }

    @Test fun release_returns_to_idle_from_intaking() {
        IntakeIntro.commandedIntake = true
        IntakeIntro.periodic()
        IntakeIntro.commandedIntake = false
        IntakeIntro.periodic()
        assertEquals(
            IntakeIntro.State.IDLE, IntakeIntro.state,
            "Releasing commandedIntake while INTAKING should return to IDLE"
        )
    }

    @Test fun release_returns_to_idle_from_holding() {
        IntakeIntro.commandedIntake = true
        IntakeIntro.periodic()
        IntakeIntro.canRange.simulateDistance(0.03)
        IntakeIntro.periodic()
        IntakeIntro.commandedIntake = false
        IntakeIntro.periodic()
        assertEquals(
            IntakeIntro.State.IDLE, IntakeIntro.state,
            "Releasing commandedIntake while HOLDING should return to IDLE"
        )
    }
}
