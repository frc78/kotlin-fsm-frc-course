package course.l2t5

import frc.stubs.VoltageOut
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class EjectingIntakeTest {
    @BeforeTest fun setUp() {
        EjectingIntake.reset()
    }

    @Test fun starts_idle_with_motor_off() {
        EjectingIntake.periodic()
        assertEquals(
            EjectingIntake.State.IDLE, EjectingIntake.state,
            "With no buttons pressed the intake should stay in IDLE"
        )
        assertEquals(
            VoltageOut(0.0), EjectingIntake.motor.lastRequest,
            "IDLE should command VoltageOut(0.0)"
        )
    }

    @Test fun intake_command_runs_rollers_at_6_volts() {
        EjectingIntake.commandedIntake = true
        EjectingIntake.periodic()
        assertEquals(
            EjectingIntake.State.INTAKING, EjectingIntake.state,
            "commandedIntake from IDLE should transition to INTAKING"
        )
        assertEquals(
            VoltageOut(6.0), EjectingIntake.motor.lastRequest,
            "INTAKING should command VoltageOut(6.0)"
        )
    }

    @Test fun eject_command_runs_rollers_backward_at_8_volts() {
        EjectingIntake.commandedEject = true
        EjectingIntake.periodic()
        assertEquals(
            EjectingIntake.State.EJECTING, EjectingIntake.state,
            "commandedEject from IDLE should transition to EJECTING"
        )
        assertEquals(
            VoltageOut(-8.0), EjectingIntake.motor.lastRequest,
            "EJECTING should command VoltageOut(-8.0)"
        )
    }

    @Test fun eject_wins_when_both_buttons_are_pressed() {
        EjectingIntake.commandedIntake = true
        EjectingIntake.commandedEject = true
        EjectingIntake.periodic()
        assertEquals(
            EjectingIntake.State.EJECTING, EjectingIntake.state,
            "commandedEject is checked before commandedIntake — eject should win when both are held"
        )
    }

    @Test fun eject_interrupts_intaking() {
        EjectingIntake.commandedIntake = true
        EjectingIntake.periodic()
        EjectingIntake.commandedEject = true
        EjectingIntake.periodic()
        assertEquals(
            EjectingIntake.State.EJECTING, EjectingIntake.state,
            "commandedEject while INTAKING should transition to EJECTING"
        )
        assertEquals(
            VoltageOut(-8.0), EjectingIntake.motor.lastRequest,
            "EJECTING should command VoltageOut(-8.0)"
        )
    }

    @Test fun eject_latches_after_the_button_is_released() {
        EjectingIntake.commandedEject = true
        EjectingIntake.periodic()
        EjectingIntake.commandedEject = false
        EjectingIntake.ejectTimer.simulateAdvance(0.3)
        EjectingIntake.periodic()
        assertEquals(
            EjectingIntake.State.EJECTING, EjectingIntake.state,
            "EJECTING should latch: releasing the button at 0.3 s must not end the eject early"
        )
        assertEquals(
            VoltageOut(-8.0), EjectingIntake.motor.lastRequest,
            "EJECTING should keep commanding VoltageOut(-8.0) until the timer elapses"
        )
    }

    @Test fun still_ejecting_just_before_the_timeout() {
        EjectingIntake.commandedEject = true
        EjectingIntake.periodic()
        EjectingIntake.commandedEject = false
        EjectingIntake.ejectTimer.simulateAdvance(0.4)
        EjectingIntake.periodic()
        assertEquals(
            EjectingIntake.State.EJECTING, EjectingIntake.state,
            "At 0.4 s the 0.5 s eject timer has not elapsed — should still be EJECTING"
        )
    }

    @Test fun returns_to_idle_after_half_a_second() {
        EjectingIntake.commandedEject = true
        EjectingIntake.periodic()
        EjectingIntake.commandedEject = false
        EjectingIntake.ejectTimer.simulateAdvance(0.6)
        EjectingIntake.periodic()
        assertEquals(
            EjectingIntake.State.IDLE, EjectingIntake.state,
            "Once ejectTimer.hasElapsed(0.5) is true the FSM should return to IDLE on its own"
        )
        assertEquals(
            VoltageOut(0.0), EjectingIntake.motor.lastRequest,
            "Back in IDLE the motor should be commanded VoltageOut(0.0)"
        )
    }

    @Test fun second_eject_restarts_the_timer() {
        // First eject runs to completion.
        EjectingIntake.commandedEject = true
        EjectingIntake.periodic()
        EjectingIntake.commandedEject = false
        EjectingIntake.ejectTimer.simulateAdvance(0.6)
        EjectingIntake.periodic()
        assertEquals(
            EjectingIntake.State.IDLE, EjectingIntake.state,
            "The first eject should have finished and returned to IDLE"
        )

        // Second eject: the timer must start over from zero.
        EjectingIntake.commandedEject = true
        EjectingIntake.periodic()
        EjectingIntake.commandedEject = false
        EjectingIntake.ejectTimer.simulateAdvance(0.3)
        EjectingIntake.periodic()
        assertEquals(
            EjectingIntake.State.EJECTING, EjectingIntake.state,
            "Re-entering EJECTING must restart() the timer — 0.3 s into the second eject is too early to stop"
        )
    }

    @Test fun intake_button_during_eject_is_ignored() {
        EjectingIntake.commandedEject = true
        EjectingIntake.periodic()
        EjectingIntake.commandedEject = false
        EjectingIntake.commandedIntake = true
        EjectingIntake.ejectTimer.simulateAdvance(0.3)
        EjectingIntake.periodic()
        assertEquals(
            EjectingIntake.State.EJECTING, EjectingIntake.state,
            "The EJECTING row watches only the timer; pressing intake at 0.3 s must not interrupt the eject"
        )
        assertEquals(
            VoltageOut(-8.0), EjectingIntake.motor.lastRequest,
            "EJECTING should keep commanding VoltageOut(-8.0) while the intake button is held"
        )
    }

    @Test fun releasing_intake_returns_to_idle() {
        EjectingIntake.commandedIntake = true
        EjectingIntake.periodic()
        EjectingIntake.commandedIntake = false
        EjectingIntake.periodic()
        assertEquals(
            EjectingIntake.State.IDLE, EjectingIntake.state,
            "Releasing commandedIntake while INTAKING should return to IDLE"
        )
    }
}
