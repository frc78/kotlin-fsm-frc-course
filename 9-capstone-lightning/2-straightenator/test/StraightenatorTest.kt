package course.l9t2

import frc.stubs.InvertedValue
import frc.stubs.NeutralModeValue
import frc.stubs.VoltageOut
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class StraightenatorTest {
    @BeforeTest fun setUp() {
        Straightenator.reset()
    }

    @Test fun can_ids_match_wiring_table() {
        assertEquals(25, Straightenator.leftMotor.canId,
            "leftMotor should be on CAN ID 25 — check the wiring table in task.md")
        assertEquals(26, Straightenator.rightMotor.canId,
            "rightMotor should be on CAN ID 26 — check the wiring table in task.md")
    }

    @Test fun motors_are_configured() {
        val left = assertNotNull(
            Straightenator.leftMotor.configurator.appliedConfig,
            "leftMotor has no applied configuration — configureMotors() should build a TalonFXConfiguration and hand it to leftMotor.configurator.apply(...)",
        )
        val right = assertNotNull(
            Straightenator.rightMotor.configurator.appliedConfig,
            "rightMotor has no applied configuration — configureMotors() should build a TalonFXConfiguration and hand it to rightMotor.configurator.apply(...)",
        )

        assertEquals(NeutralModeValue.Coast, left.MotorOutput.NeutralMode,
            "leftMotor MotorOutput.NeutralMode should be Coast — re-read the config table in task.md")
        assertEquals(InvertedValue.CounterClockwise_Positive, left.MotorOutput.Inverted,
            "leftMotor MotorOutput.Inverted should stay CounterClockwise_Positive — only the mirror-mounted right side is inverted")
        assertEquals(NeutralModeValue.Coast, right.MotorOutput.NeutralMode,
            "rightMotor MotorOutput.NeutralMode should be Coast — re-read the config table in task.md")
        assertEquals(InvertedValue.Clockwise_Positive, right.MotorOutput.Inverted,
            "rightMotor MotorOutput.Inverted should be Clockwise_Positive — the sides face each other, so 'positive = feed' needs the right side inverted; the configurator keeps only the most recently applied configuration, so set every field on one TalonFXConfiguration")

        assertEquals(20.0, left.CurrentLimits.SupplyCurrentLimit,
            "leftMotor CurrentLimits.SupplyCurrentLimit should be 20.0 A — re-read the config table in task.md; the configurator keeps only the most recently applied configuration, so set every field on one TalonFXConfiguration")
        assertTrue(left.CurrentLimits.SupplyCurrentLimitEnable,
            "leftMotor CurrentLimits.SupplyCurrentLimitEnable must be true — the limit value alone does nothing")
        assertEquals(60.0, left.CurrentLimits.StatorCurrentLimit,
            "leftMotor CurrentLimits.StatorCurrentLimit should be 60.0 A — it has to sit above the 40 A jam detection threshold")
        assertTrue(left.CurrentLimits.StatorCurrentLimitEnable,
            "leftMotor CurrentLimits.StatorCurrentLimitEnable must be true — the limit value alone does nothing")

        assertEquals(20.0, right.CurrentLimits.SupplyCurrentLimit,
            "rightMotor CurrentLimits.SupplyCurrentLimit should be 20.0 A — re-read the config table in task.md; the configurator keeps only the most recently applied configuration, so set every field on one TalonFXConfiguration")
        assertTrue(right.CurrentLimits.SupplyCurrentLimitEnable,
            "rightMotor CurrentLimits.SupplyCurrentLimitEnable must be true — the limit value alone does nothing")
        assertEquals(60.0, right.CurrentLimits.StatorCurrentLimit,
            "rightMotor CurrentLimits.StatorCurrentLimit should be 60.0 A — it has to sit above the 40 A jam detection threshold")
        assertTrue(right.CurrentLimits.StatorCurrentLimitEnable,
            "rightMotor CurrentLimits.StatorCurrentLimitEnable must be true — the limit value alone does nothing")
    }

    @Test fun starts_idle_with_both_motors_off() {
        Straightenator.periodic()
        assertEquals(Straightenator.State.IDLE, Straightenator.state,
            "With no feed command the Straightenator should sit in IDLE")
        assertEquals(VoltageOut(0.0), Straightenator.leftMotor.lastRequest,
            "IDLE should command 0.0 V on the left motor")
        assertEquals(VoltageOut(0.0), Straightenator.rightMotor.lastRequest,
            "IDLE should command 0.0 V on the right motor")
    }

    @Test fun feed_command_runs_both_sides_forward() {
        Straightenator.commandedFeed = true
        Straightenator.periodic()
        assertEquals(Straightenator.State.FEEDING, Straightenator.state,
            "commandedFeed should take IDLE to FEEDING")
        assertEquals(VoltageOut(4.0), Straightenator.leftMotor.lastRequest,
            "FEEDING should command 4.0 V on the left motor")
        assertEquals(VoltageOut(4.0), Straightenator.rightMotor.lastRequest,
            "FEEDING should command 4.0 V on the right motor")
    }

    @Test fun left_current_spike_triggers_unjamming() {
        Straightenator.commandedFeed = true
        Straightenator.periodic()
        Straightenator.leftMotor.simulateStatorCurrent(45.0)
        Straightenator.periodic()
        assertEquals(Straightenator.State.UNJAMMING, Straightenator.state,
            "45 A on the left motor is over the 40 A jam threshold — FEEDING should become UNJAMMING")
        assertEquals(VoltageOut(-4.0), Straightenator.leftMotor.lastRequest,
            "UNJAMMING should reverse the left motor to -4.0 V so the sides counter-rotate")
        assertEquals(VoltageOut(4.0), Straightenator.rightMotor.lastRequest,
            "UNJAMMING should keep the right motor at 4.0 V so the sides counter-rotate")
    }

    @Test fun right_current_spike_also_triggers_unjamming() {
        Straightenator.commandedFeed = true
        Straightenator.periodic()
        Straightenator.rightMotor.simulateStatorCurrent(45.0)
        Straightenator.periodic()
        assertEquals(Straightenator.State.UNJAMMING, Straightenator.state,
            "A jam can show up on EITHER side — 45 A on the right motor alone should trigger UNJAMMING")
    }

    @Test fun normal_feeding_current_does_not_trigger_unjamming() {
        Straightenator.commandedFeed = true
        Straightenator.periodic()
        Straightenator.leftMotor.simulateStatorCurrent(35.0)
        Straightenator.rightMotor.simulateStatorCurrent(35.0)
        Straightenator.periodic()
        assertEquals(Straightenator.State.FEEDING, Straightenator.state,
            "35 A is below the 40 A jam threshold — the Straightenator should keep FEEDING")
    }

    @Test fun unjam_keeps_running_before_a_quarter_second() {
        Straightenator.commandedFeed = true
        Straightenator.periodic()
        Straightenator.leftMotor.simulateStatorCurrent(45.0)
        Straightenator.periodic()
        Straightenator.leftMotor.simulateStatorCurrent(10.0)
        Straightenator.unjamTimer.simulateAdvance(0.2)
        Straightenator.periodic()
        assertEquals(Straightenator.State.UNJAMMING, Straightenator.state,
            "0.2 s is short of the 0.25 s unjam window — UNJAMMING should continue even though current is back to normal")
        assertEquals(VoltageOut(-4.0), Straightenator.leftMotor.lastRequest,
            "The left motor should still be counter-rotating at -4.0 V while the unjam window runs")
    }

    @Test fun unjam_returns_to_feeding_after_a_quarter_second() {
        Straightenator.commandedFeed = true
        Straightenator.periodic()
        Straightenator.leftMotor.simulateStatorCurrent(45.0)
        Straightenator.periodic()
        Straightenator.leftMotor.simulateStatorCurrent(10.0)
        Straightenator.unjamTimer.simulateAdvance(0.3)
        Straightenator.periodic()
        assertEquals(Straightenator.State.FEEDING, Straightenator.state,
            "0.3 s is past the 0.25 s unjam window — UNJAMMING should return to FEEDING")
        assertEquals(VoltageOut(4.0), Straightenator.leftMotor.lastRequest,
            "Back in FEEDING the left motor should run forward at 4.0 V again")
        assertEquals(VoltageOut(4.0), Straightenator.rightMotor.lastRequest,
            "Back in FEEDING the right motor should run forward at 4.0 V again")
    }

    @Test fun still_jammed_coral_triggers_a_second_unjam() {
        Straightenator.commandedFeed = true
        Straightenator.periodic()
        Straightenator.leftMotor.simulateStatorCurrent(45.0)
        Straightenator.periodic()
        Straightenator.unjamTimer.simulateAdvance(0.3)
        Straightenator.periodic()
        assertEquals(Straightenator.State.FEEDING, Straightenator.state,
            "When the unjam window ends the FSM always returns to FEEDING first, even if the coral is still stuck")
        Straightenator.periodic()
        assertEquals(Straightenator.State.UNJAMMING, Straightenator.state,
            "Current is still over 40 A, so FEEDING should detect the jam again and start another unjam")
    }

    @Test fun second_jam_needs_the_full_unjam_time_again() {
        Straightenator.commandedFeed = true
        Straightenator.periodic()
        Straightenator.leftMotor.simulateStatorCurrent(45.0)
        Straightenator.periodic()
        Straightenator.leftMotor.simulateStatorCurrent(10.0)
        Straightenator.unjamTimer.simulateAdvance(0.3)
        Straightenator.periodic()
        assertEquals(Straightenator.State.FEEDING, Straightenator.state,
            "The first unjam should be over after 0.3 s, putting the Straightenator back in FEEDING")
        Straightenator.leftMotor.simulateStatorCurrent(45.0)
        Straightenator.periodic()
        Straightenator.leftMotor.simulateStatorCurrent(10.0)
        Straightenator.unjamTimer.simulateAdvance(0.2)
        Straightenator.periodic()
        assertEquals(Straightenator.State.UNJAMMING, Straightenator.state,
            "Only 0.2 s into the SECOND unjam the window must not be over — did onEnter() restart the timer, not just start it?")
    }

    @Test fun releasing_feed_during_an_unjam_exits_through_feeding() {
        Straightenator.commandedFeed = true
        Straightenator.periodic()
        Straightenator.leftMotor.simulateStatorCurrent(45.0)
        Straightenator.periodic()
        Straightenator.commandedFeed = false
        Straightenator.leftMotor.simulateStatorCurrent(10.0)
        Straightenator.unjamTimer.simulateAdvance(0.1)
        Straightenator.periodic()
        assertEquals(Straightenator.State.UNJAMMING, Straightenator.state,
            "Releasing the feed button must not cut an unjam short — UNJAMMING only exits on the timer")
        Straightenator.unjamTimer.simulateAdvance(0.2)
        Straightenator.periodic()
        assertEquals(Straightenator.State.FEEDING, Straightenator.state,
            "UNJAMMING always returns to FEEDING first, even after the driver lets go — the two-hop exit")
        Straightenator.periodic()
        assertEquals(Straightenator.State.IDLE, Straightenator.state,
            "With feed released, FEEDING should fall through to IDLE on the next tick")
        assertEquals(VoltageOut(0.0), Straightenator.leftMotor.lastRequest,
            "Back in IDLE the left motor should be commanded 0.0 V")
        assertEquals(VoltageOut(0.0), Straightenator.rightMotor.lastRequest,
            "Back in IDLE the right motor should be commanded 0.0 V")
    }

    @Test fun idle_ignores_current_spikes() {
        Straightenator.leftMotor.simulateStatorCurrent(50.0)
        Straightenator.rightMotor.simulateStatorCurrent(50.0)
        Straightenator.periodic()
        assertEquals(Straightenator.State.IDLE, Straightenator.state,
            "Jam detection only applies while FEEDING — IDLE has no current transition")
        assertEquals(VoltageOut(0.0), Straightenator.leftMotor.lastRequest,
            "IDLE should keep the left motor at 0.0 V no matter what the current reads")
    }
}
