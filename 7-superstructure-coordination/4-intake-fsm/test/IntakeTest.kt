package course.l7t4

import frc.stubs.NeutralOut
import frc.stubs.OI
import frc.stubs.VoltageOut
import frc.stubs.superstructure.Arm
import frc.stubs.superstructure.Elevator
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IntakeTest {
    @BeforeTest fun setUp() {
        OI.reset()
        Intake.reset()
        Intake.motor.simulateStatorCurrent(0.0)   // the singleton motor keeps sim values between tests
        SuperStructure.reset()
    }

    private fun stallFor(ticks: Int, amps: Double = 15.0) {
        Intake.motor.simulateStatorCurrent(amps)
        repeat(ticks) { Intake.periodic() }
    }

    // Press intake for one tick, then stall the rollers long enough to hold.
    private fun holdPiece() {
        OI.intake = true
        Intake.periodic()
        OI.intake = false
        stallFor(5)
        assertEquals(Intake.State.HOLDING, Intake.state, "setup: five stalled ticks should give HOLDING")
    }

    // Drive the superstructure to L4 and let it settle (arm 1 tick, elevator 5 more).
    private fun settleAtL4() {
        OI.scoreL4 = true
        repeat(8) { SuperStructure.periodic() }
        assertEquals(Pose.L4, SuperStructure.state, "setup: superstructure should be at L4")
        assertTrue(SuperStructure.atPosition, "setup: superstructure should be settled at L4")
    }

    @Test fun starts_idle_and_off() {
        assertEquals(Intake.State.IDLE, Intake.state, "reset() should leave the intake IDLE")
        Intake.periodic()
        assertEquals(NeutralOut, Intake.motor.lastRequest, "IDLE should send NeutralOut (IDLE row of the state table)")
    }

    @Test fun intake_button_starts_intaking() {
        OI.intake = true
        Intake.periodic()
        assertEquals(Intake.State.INTAKING, Intake.state, "OI.intake in IDLE should give INTAKING")
        assertEquals(VoltageOut(6.0), Intake.motor.lastRequest, "INTAKING should send VoltageOut(6.0)")
    }

    @Test fun four_stalled_ticks_stay_intaking() {
        OI.intake = true
        Intake.periodic()
        stallFor(4)
        assertEquals(Intake.State.INTAKING, Intake.state, "the debouncer needs 5 stalled ticks; after 4 the intake is still INTAKING")
    }

    @Test fun fifth_stalled_tick_holds() {
        OI.intake = true
        Intake.periodic()
        stallFor(5)
        assertEquals(Intake.State.HOLDING, Intake.state, "the fifth stalled tick in a row should give HOLDING")
        assertEquals(VoltageOut(1.0), Intake.motor.lastRequest, "HOLDING should send VoltageOut(1.0)")
    }

    @Test fun current_dropout_resets_the_count() {
        OI.intake = true
        Intake.periodic()
        stallFor(3)
        stallFor(1, amps = 0.0)
        stallFor(4)
        assertEquals(Intake.State.INTAKING, Intake.state, "one tick below 10 A resets the debouncer; 4 stalled ticks after it are not enough")
        stallFor(1)
        assertEquals(Intake.State.HOLDING, Intake.state, "the fifth stalled tick after the dropout should give HOLDING")
    }

    @Test fun home_while_intaking_returns_to_idle() {
        OI.intake = true
        Intake.periodic()
        OI.intake = false
        OI.home = true
        Intake.periodic()
        assertEquals(Intake.State.IDLE, Intake.state, "OI.home in INTAKING should give IDLE")
        assertEquals(NeutralOut, Intake.motor.lastRequest, "IDLE should send NeutralOut")
    }

    @Test fun score_at_home_does_nothing() {
        holdPiece()
        OI.score = true
        Intake.periodic()
        assertEquals(Intake.State.HOLDING, Intake.state, "HOME is not a scoring pose; the intake must keep HOLDING")
    }

    @Test fun score_at_l4_while_still_moving_does_nothing() {
        holdPiece()
        OI.scoreL4 = true
        repeat(2) { SuperStructure.periodic() }   // pose is L4, elevator is on its way
        assertEquals(Pose.L4, SuperStructure.state, "setup: superstructure should be at L4")
        assertFalse(SuperStructure.atPosition, "setup: elevator should still be moving")
        OI.score = true
        Intake.periodic()
        assertEquals(Intake.State.HOLDING, Intake.state, "SuperStructure.atPosition is false, so the intake must not eject yet")
    }

    @Test fun score_in_the_one_tick_window_does_nothing() {
        holdPiece()
        OI.scoreL4 = true
        SuperStructure.periodic()   // tick 1: the arm arrives at 45, the elevator is not commanded yet
        assertTrue(Arm.atPosition, "setup: the arm should have arrived in one tick")
        assertEquals(0.0, Elevator.target, "setup: the elevator should not be commanded until the arm arrives")
        OI.score = true
        Intake.periodic()
        assertEquals(
            Intake.State.HOLDING, Intake.state,
            "the elevator is still at HOME, so SuperStructure.atPosition must be false and the intake must not eject",
        )
    }

    @Test fun score_at_settled_l4_ejects() {
        holdPiece()
        settleAtL4()
        OI.score = true
        Intake.periodic()
        assertEquals(Intake.State.EJECTING, Intake.state, "OI.score at a settled scoring pose should give EJECTING")
        assertEquals(VoltageOut(-6.0), Intake.motor.lastRequest, "EJECTING should send VoltageOut(-6.0)")
    }

    @Test fun releasing_score_returns_to_idle() {
        holdPiece()
        settleAtL4()
        OI.score = true
        Intake.periodic()
        OI.score = false
        Intake.periodic()
        assertEquals(Intake.State.IDLE, Intake.state, "releasing score in EJECTING should give IDLE")
        assertEquals(NeutralOut, Intake.motor.lastRequest, "IDLE should send NeutralOut")
    }

    @Test fun state_actions_send_control_every_tick() {
        holdPiece()
        Intake.motor.stopMotor()   // something else touched the motor
        Intake.periodic()
        assertEquals(VoltageOut(1.0), Intake.motor.lastRequest, "stateActions() must send state.control on every tick, not only on a change")
    }
}
