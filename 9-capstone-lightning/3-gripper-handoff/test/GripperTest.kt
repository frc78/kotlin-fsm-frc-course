package course.l9t3

import frc.stubs.NeutralModeValue
import frc.stubs.VoltageOut
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GripperTest {
    @BeforeTest fun setUp() {
        Gripper.reset()
    }

    /** Runs the coral handoff to completion: cradle beam, then gripper beam. */
    private fun acquireCoral() {
        Gripper.cradleBeamBreak.simulateValue(true)
        Gripper.periodic() // EMPTY -> HANDOFF
        Gripper.gripperBeamBreak.simulateValue(true)
        Gripper.periodic() // HANDOFF -> HOLDING_CORAL
        Gripper.cradleBeamBreak.simulateValue(false) // the coral has left the cradle
    }

    /** Stalls the wheels on a ball: ALGAE_INTAKE until current spikes. */
    private fun acquireAlgae() {
        Gripper.commandedAlgaeIntake = true
        Gripper.periodic() // EMPTY -> ALGAE_INTAKE
        Gripper.motor.simulateStatorCurrent(55.0)
        Gripper.periodic() // ALGAE_INTAKE -> HOLDING_ALGAE
        Gripper.commandedAlgaeIntake = false
        Gripper.motor.simulateStatorCurrent(0.0)
    }

    @Test fun can_ids_match_wiring_table() {
        assertEquals(30, Gripper.motor.canId,
            "motor should be the Kraken X44 on CAN 30 - check the wiring table in task.md")
        assertEquals(2, Gripper.cradleBeamBreak.channel,
            "cradleBeamBreak should be on DIO channel 2 - check the wiring table in task.md")
        assertEquals(3, Gripper.gripperBeamBreak.channel,
            "gripperBeamBreak should be on DIO channel 3 - check the wiring table in task.md")
    }

    @Test fun motors_are_configured() {
        val applied = assertNotNull(Gripper.motor.configurator.appliedConfig,
            "configureMotors() should build a TalonFXConfiguration and hand it to the motor's configurator via apply(...)")
        assertEquals(NeutralModeValue.Brake, applied.MotorOutput.NeutralMode,
            "MotorOutput.NeutralMode should be Brake so the wheels do not free-wheel and drop a held game piece when disabled - re-read the MotorOutput block, and remember the configurator keeps only the most recently applied configuration: set every field on one TalonFXConfiguration")
        assertEquals(55.0, applied.CurrentLimits.StatorCurrentLimit,
            "CurrentLimits.StatorCurrentLimit should be 55.0 - that clamped current IS the algae grip strength; re-read the CurrentLimits block")
        assertTrue(applied.CurrentLimits.StatorCurrentLimitEnable,
            "CurrentLimits.StatorCurrentLimitEnable must be true - the 55 A hold does not exist unless the limit is enabled; re-read the CurrentLimits block")
    }

    @Test fun starts_empty_with_motor_off() {
        Gripper.periodic()
        assertEquals(Gripper.State.EMPTY, Gripper.state,
            "No beams broken, no buttons pressed: the gripper should sit in EMPTY")
        assertEquals(VoltageOut(0.0), Gripper.motor.lastRequest,
            "EMPTY should command VoltageOut(0.0)")
    }

    @Test fun coral_in_cradle_starts_handoff_with_no_driver_input() {
        Gripper.cradleBeamBreak.simulateValue(true)
        Gripper.periodic()
        assertEquals(Gripper.State.HANDOFF, Gripper.state,
            "The cradle beam break alone should trigger the handoff - no driver input involved")
        assertEquals(VoltageOut(6.0), Gripper.motor.lastRequest,
            "HANDOFF should pull the coral in at VoltageOut(6.0)")
    }

    @Test fun gripper_beam_confirms_coral_and_idle_spins_to_hold() {
        Gripper.cradleBeamBreak.simulateValue(true)
        Gripper.periodic()
        Gripper.gripperBeamBreak.simulateValue(true)
        Gripper.periodic()
        assertEquals(Gripper.State.HOLDING_CORAL, Gripper.state,
            "The gripper beam break confirms possession: HANDOFF should end in HOLDING_CORAL")
        assertEquals(VoltageOut(0.5), Gripper.motor.lastRequest,
            "HOLDING_CORAL idle-spins the wheels inward at VoltageOut(0.5) to re-seat a bumped coral")
    }

    @Test fun algae_button_starts_algae_intake() {
        Gripper.commandedAlgaeIntake = true
        Gripper.periodic()
        assertEquals(Gripper.State.ALGAE_INTAKE, Gripper.state,
            "commandedAlgaeIntake from EMPTY should enter ALGAE_INTAKE")
        assertEquals(VoltageOut(10.0), Gripper.motor.lastRequest,
            "ALGAE_INTAKE should run the wheels at VoltageOut(10.0)")
    }

    @Test fun stall_current_means_algae_acquired() {
        Gripper.commandedAlgaeIntake = true
        Gripper.periodic()
        Gripper.motor.simulateStatorCurrent(55.0)
        Gripper.periodic()
        assertEquals(Gripper.State.HOLDING_ALGAE, Gripper.state,
            "55 A is past the 50 A stall threshold: the ball is seated, so ALGAE_INTAKE becomes HOLDING_ALGAE")
        assertEquals(VoltageOut(2.0), Gripper.motor.lastRequest,
            "HOLDING_ALGAE should keep pressing against the ball at VoltageOut(2.0)")
    }

    @Test fun below_stall_current_stays_in_algae_intake() {
        Gripper.commandedAlgaeIntake = true
        Gripper.periodic()
        Gripper.motor.simulateStatorCurrent(40.0)
        Gripper.periodic()
        assertEquals(Gripper.State.ALGAE_INTAKE, Gripper.state,
            "40 A is below the 50 A stall threshold - the wheels are still turning, so keep intaking")
        assertEquals(VoltageOut(10.0), Gripper.motor.lastRequest,
            "Still ALGAE_INTAKE, so the wheels should still run at VoltageOut(10.0)")
    }

    @Test fun releasing_algae_button_without_a_ball_returns_to_empty() {
        Gripper.commandedAlgaeIntake = true
        Gripper.periodic()
        Gripper.commandedAlgaeIntake = false
        Gripper.periodic()
        assertEquals(Gripper.State.EMPTY, Gripper.state,
            "Releasing the algae button with no stall detected means giving up: back to EMPTY")
        assertEquals(VoltageOut(0.0), Gripper.motor.lastRequest,
            "Back in EMPTY the motor should command VoltageOut(0.0)")
    }

    @Test fun release_button_ejects_coral() {
        acquireCoral()
        Gripper.commandedRelease = true
        Gripper.periodic()
        assertEquals(Gripper.State.RELEASING, Gripper.state,
            "commandedRelease from HOLDING_CORAL should enter RELEASING")
        assertEquals(VoltageOut(-6.0), Gripper.motor.lastRequest,
            "RELEASING should spit at VoltageOut(-6.0)")
    }

    @Test fun release_button_ejects_algae() {
        acquireAlgae()
        Gripper.commandedRelease = true
        Gripper.periodic()
        assertEquals(Gripper.State.RELEASING, Gripper.state,
            "commandedRelease from HOLDING_ALGAE should enter RELEASING")
        assertEquals(VoltageOut(-6.0), Gripper.motor.lastRequest,
            "RELEASING should spit at VoltageOut(-6.0)")
    }

    @Test fun releasing_continues_while_coral_still_blocks_the_beam() {
        acquireCoral()
        Gripper.commandedRelease = true
        Gripper.periodic()
        Gripper.commandedRelease = false // button released, but the coral has not cleared yet
        Gripper.periodic()
        assertEquals(Gripper.State.RELEASING, Gripper.state,
            "The gripper beam is still blocked: the coral is not out yet, so keep RELEASING")
        assertEquals(VoltageOut(-6.0), Gripper.motor.lastRequest,
            "Still RELEASING, so keep spitting at VoltageOut(-6.0)")
    }

    @Test fun releasing_continues_while_button_still_held() {
        acquireCoral()
        Gripper.commandedRelease = true
        Gripper.periodic()
        Gripper.gripperBeamBreak.simulateValue(false) // coral is out, but the driver still holds release
        Gripper.periodic()
        assertEquals(Gripper.State.RELEASING, Gripper.state,
            "commandedRelease is still held: RELEASING should wait for the button to be released too")
        assertEquals(VoltageOut(-6.0), Gripper.motor.lastRequest,
            "Still RELEASING, so keep spitting at VoltageOut(-6.0)")
    }

    @Test fun releasing_exits_to_empty_once_clear_and_released() {
        acquireCoral()
        Gripper.commandedRelease = true
        Gripper.periodic()
        Gripper.commandedRelease = false
        Gripper.gripperBeamBreak.simulateValue(false)
        Gripper.periodic()
        assertEquals(Gripper.State.EMPTY, Gripper.state,
            "Button released AND beam clear: RELEASING should return to EMPTY")
        assertEquals(VoltageOut(0.0), Gripper.motor.lastRequest,
            "Back in EMPTY the motor should command VoltageOut(0.0)")
    }

    @Test fun coral_handoff_beats_algae_button() {
        Gripper.cradleBeamBreak.simulateValue(true)
        Gripper.commandedAlgaeIntake = true
        Gripper.periodic()
        assertEquals(Gripper.State.HANDOFF, Gripper.state,
            "A coral already in the cradle outranks the algae button: EMPTY goes to HANDOFF, not ALGAE_INTAKE")
        assertEquals(VoltageOut(6.0), Gripper.motor.lastRequest,
            "HANDOFF should pull the coral in at VoltageOut(6.0)")
    }
}
