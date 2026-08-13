package course.l9t1

import frc.stubs.NeutralModeValue
import frc.stubs.PositionVoltage
import frc.stubs.VoltageOut
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class IntakeTest {
    @BeforeTest fun setUp() {
        Intake.reset()
    }

    @Test fun can_ids_match_wiring_table() {
        assertEquals(20, Intake.rollerMotor.canId,
            "rollerMotor is on the wrong CAN ID — the wiring table in task.md puts the roller Kraken X60 on CAN 20")
        assertEquals(21, Intake.pivotMotor.canId,
            "pivotMotor is on the wrong CAN ID — the wiring table in task.md puts the pivot Kraken X44 on CAN 21")
        assertEquals(0, Intake.beamBreak.channel,
            "beamBreak is on the wrong channel — the wiring table in task.md puts the beam break on DIO 0")
    }

    @Test fun motors_are_configured() {
        val rollerConfig = assertNotNull(Intake.rollerMotor.configurator.appliedConfig,
            "rollerMotor has no applied configuration — configureMotors() should build a TalonFXConfiguration and hand it to rollerMotor.configurator.apply(...)")
        assertEquals(NeutralModeValue.Coast, rollerConfig.MotorOutput.NeutralMode,
            "rollerMotor MotorOutput.NeutralMode should be Coast so a grabbed coral can slip instead of stalling — re-read the MotorOutput block in the config table, and remember the configurator keeps only the most recently applied configuration")
        assertEquals(30.0, rollerConfig.CurrentLimits.SupplyCurrentLimit,
            "rollerMotor CurrentLimits.SupplyCurrentLimit should be 30.0 A — re-read the CurrentLimits column of the config table")
        assertTrue(rollerConfig.CurrentLimits.SupplyCurrentLimitEnable,
            "rollerMotor CurrentLimits.SupplyCurrentLimitEnable must be true — setting the limit value alone does nothing (Lesson 5's classic gotcha)")

        val pivotConfig = assertNotNull(Intake.pivotMotor.configurator.appliedConfig,
            "pivotMotor has no applied configuration — configureMotors() should build a TalonFXConfiguration and hand it to pivotMotor.configurator.apply(...)")
        assertEquals(NeutralModeValue.Brake, pivotConfig.MotorOutput.NeutralMode,
            "pivotMotor MotorOutput.NeutralMode should be Brake so the intake doesn't flop down when the robot disables — re-read the MotorOutput block in the config table, and remember the configurator keeps only the most recently applied configuration")
        assertEquals(20.0, pivotConfig.CurrentLimits.SupplyCurrentLimit,
            "pivotMotor CurrentLimits.SupplyCurrentLimit should be 20.0 A — re-read the CurrentLimits column of the config table")
        assertTrue(pivotConfig.CurrentLimits.SupplyCurrentLimitEnable,
            "pivotMotor CurrentLimits.SupplyCurrentLimitEnable must be true — setting the limit value alone does nothing (Lesson 5's classic gotcha)")
        assertEquals(8.0, pivotConfig.Slot0.kP,
            "pivotMotor Slot0.kP should be 8.0 — PositionVoltage is closed-loop and computes zero volts without gains; re-read the Slot0 column of the config table")
    }

    @Test fun starts_stowed_with_pivot_up_and_rollers_off() {
        Intake.periodic()
        assertEquals(Intake.State.STOWED, Intake.state,
            "With no buttons pressed the intake should stay STOWED")
        assertEquals(PositionVoltage(0.0), Intake.pivotMotor.lastRequest,
            "STOWED should hold the pivot at 0.0 rotations with PositionVoltage(0.0)")
        assertEquals(VoltageOut(0.0), Intake.rollerMotor.lastRequest,
            "STOWED should command the rollers to VoltageOut(0.0)")
    }

    @Test fun deploy_slaps_down_and_spins_rollers() {
        Intake.commandedDeploy = true
        Intake.periodic()
        assertEquals(Intake.State.INTAKING, Intake.state,
            "Holding deploy from STOWED should transition to INTAKING")
        assertEquals(PositionVoltage(12.0), Intake.pivotMotor.lastRequest,
            "INTAKING should drive the pivot down to 12.0 rotations")
        assertEquals(VoltageOut(9.0), Intake.rollerMotor.lastRequest,
            "INTAKING should run the rollers at 9.0 V")
    }

    @Test fun stays_intaking_while_deploy_held_and_no_coral() {
        Intake.commandedDeploy = true
        Intake.periodic()
        Intake.periodic()
        assertEquals(Intake.State.INTAKING, Intake.state,
            "With deploy still held and no beam break, the intake should stay INTAKING")
    }

    @Test fun beam_break_auto_retracts_even_while_deploy_is_held() {
        Intake.commandedDeploy = true
        Intake.periodic()
        Intake.beamBreak.simulateValue(true)
        Intake.periodic()
        assertEquals(Intake.State.STOWED, Intake.state,
            "Coral acquired: the beam break should auto-retract the intake to STOWED even though the driver is still holding deploy")
        assertEquals(PositionVoltage(0.0), Intake.pivotMotor.lastRequest,
            "The auto-retract should bring the pivot back up to 0.0 rotations")
    }

    @Test fun releasing_deploy_without_coral_returns_to_stowed() {
        Intake.commandedDeploy = true
        Intake.periodic()
        Intake.commandedDeploy = false
        Intake.periodic()
        assertEquals(Intake.State.STOWED, Intake.state,
            "Releasing deploy with no coral detected should return the intake to STOWED")
    }

    @Test fun purge_from_stowed_puts_pivot_down_and_reverses_rollers() {
        Intake.commandedPurge = true
        Intake.periodic()
        assertEquals(Intake.State.PURGING, Intake.state,
            "Purge pressed in STOWED should transition to PURGING")
        assertEquals(PositionVoltage(12.0), Intake.pivotMotor.lastRequest,
            "PURGING should put the pivot down at 12.0 rotations")
        assertEquals(VoltageOut(-6.0), Intake.rollerMotor.lastRequest,
            "PURGING should reverse the rollers at -6.0 V")
    }

    @Test fun purge_from_intaking_reverses_rollers() {
        Intake.commandedDeploy = true
        Intake.periodic()
        Intake.commandedPurge = true
        Intake.periodic()
        assertEquals(Intake.State.PURGING, Intake.state,
            "Purge pressed while INTAKING should transition to PURGING")
        assertEquals(PositionVoltage(12.0), Intake.pivotMotor.lastRequest,
            "PURGING should keep the pivot down at 12.0 rotations")
        assertEquals(VoltageOut(-6.0), Intake.rollerMotor.lastRequest,
            "PURGING should reverse the rollers at -6.0 V")
    }

    @Test fun releasing_purge_returns_to_stowed() {
        Intake.commandedPurge = true
        Intake.periodic()
        Intake.commandedPurge = false
        Intake.periodic()
        assertEquals(Intake.State.STOWED, Intake.state,
            "Releasing purge should return the intake to STOWED")
    }

    @Test fun purge_wins_over_beam_break() {
        Intake.commandedDeploy = true
        Intake.periodic()
        Intake.beamBreak.simulateValue(true)
        Intake.commandedPurge = true
        Intake.periodic()
        assertEquals(Intake.State.PURGING, Intake.state,
            "With purge held AND the beam broken, the purge row is checked first: INTAKING should go to PURGING, not STOWED")
    }
}
