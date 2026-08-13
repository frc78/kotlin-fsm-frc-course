package course.l9t7

import frc.stubs.NeutralModeValue
import frc.stubs.VoltageOut
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ClimberTest {
    @BeforeTest fun setUp() {
        Climber.reset()
    }

    /** Drives the FSM STOWED -> DEPLOYING -> DEPLOYED. */
    private fun deployFully() {
        Climber.commandedDeploy = true
        Climber.periodic()
        Climber.winchMotor.simulatePosition(15.0)
        Climber.periodic()
    }

    /** Drives the FSM all the way into CLIMBING. */
    private fun startClimb() {
        deployFully()
        Climber.commandedClimb = true
        Climber.periodic()
    }

    @Test fun can_ids_match_wiring_table() {
        assertEquals(40, Climber.winchMotor.canId,
            "winchMotor must be on CAN 40 — check the wiring table in task.md")
    }

    @Test fun motors_are_configured() {
        val applied = assertNotNull(Climber.winchMotor.configurator.appliedConfig,
            "configureMotors() never handed winchMotor a configuration — build a TalonFXConfiguration and pass it to winchMotor.configurator.apply(...)")
        assertEquals(NeutralModeValue.Brake, applied.MotorOutput.NeutralMode,
            "winchMotor's MotorOutput.NeutralMode should be Brake (re-read the Configuration table in task.md) — and remember the configurator keeps only the most recently applied configuration, so set every field on one TalonFXConfiguration")
        assertEquals(40.0, applied.CurrentLimits.SupplyCurrentLimit,
            "winchMotor's CurrentLimits.SupplyCurrentLimit should be 40.0 A — re-read the Configuration table in task.md")
        assertTrue(applied.CurrentLimits.SupplyCurrentLimitEnable,
            "CurrentLimits.SupplyCurrentLimitEnable must be true — setting the limit value alone does nothing (the Lesson 5 gotcha)")
    }

    @Test fun starts_stowed_with_winch_unpowered() {
        Climber.periodic()
        assertEquals(Climber.State.STOWED, Climber.state,
            "The climber should start in STOWED")
        assertEquals(VoltageOut(0.0), Climber.winchMotor.lastRequest,
            "STOWED should command VoltageOut(0.0) on the winch")
    }

    @Test fun deploy_button_starts_deploying_at_4_volts() {
        Climber.commandedDeploy = true
        Climber.periodic()
        assertEquals(Climber.State.DEPLOYING, Climber.state,
            "commandedDeploy in STOWED should transition to DEPLOYING")
        assertEquals(VoltageOut(4.0), Climber.winchMotor.lastRequest,
            "DEPLOYING should pay the mechanism out at VoltageOut(4.0)")
    }

    @Test fun climb_button_alone_does_nothing_from_stowed() {
        Climber.commandedClimb = true
        Climber.periodic()
        assertEquals(Climber.State.STOWED, Climber.state,
            "commandedClimb must be ignored in STOWED — there is no row for it; the climber has to deploy first")
        assertEquals(VoltageOut(0.0), Climber.winchMotor.lastRequest,
            "Still STOWED, so the winch should stay at VoltageOut(0.0)")
    }

    @Test fun partway_through_deploy_stays_deploying() {
        Climber.commandedDeploy = true
        Climber.periodic()
        Climber.winchMotor.simulatePosition(14.9)
        Climber.periodic()
        assertEquals(Climber.State.DEPLOYING, Climber.state,
            "At 14.9 rotations (< 15.0) the hook is still travelling — stay DEPLOYING")
        assertEquals(VoltageOut(4.0), Climber.winchMotor.lastRequest,
            "DEPLOYING should keep commanding VoltageOut(4.0) until 15.0 rotations")
    }

    @Test fun reaching_15_rotations_finishes_the_deploy() {
        deployFully()
        assertEquals(Climber.State.DEPLOYED, Climber.state,
            "At 15.0 rotations the mechanism is out — DEPLOYING should transition to DEPLOYED")
        assertEquals(VoltageOut(0.0), Climber.winchMotor.lastRequest,
            "DEPLOYED waits with the winch unpowered: VoltageOut(0.0)")
    }

    @Test fun climb_button_from_deployed_starts_climbing_at_12_volts() {
        startClimb()
        assertEquals(Climber.State.CLIMBING, Climber.state,
            "commandedClimb in DEPLOYED should transition to CLIMBING")
        assertEquals(VoltageOut(12.0), Climber.winchMotor.lastRequest,
            "CLIMBING should reel in at VoltageOut(12.0)")
    }

    @Test fun reaching_40_rotations_latches_climbed_and_unpowers_the_winch() {
        startClimb()
        Climber.winchMotor.simulatePosition(40.0)
        Climber.periodic()
        assertEquals(Climber.State.CLIMBED, Climber.state,
            "At 40.0 rotations the robot is up — CLIMBING should transition to CLIMBED")
        assertEquals(VoltageOut(0.0), Climber.winchMotor.lastRequest,
            "The ratchet holds — CLIMBED leaves the motor unloaded at VoltageOut(0.0)")
    }

    @Test fun stalled_climb_faults_after_two_seconds() {
        startClimb()
        Climber.winchMotor.simulatePosition(20.0)
        Climber.climbTimer.simulateAdvance(2.0)
        Climber.periodic()
        assertEquals(Climber.State.FAULTED, Climber.state,
            "Stuck at 20 rotations with 2.0 s elapsed — the FSM must latch FAULTED instead of burning the winch (did onEnter() restart the timer when CLIMBING began?)")
        assertEquals(VoltageOut(0.0), Climber.winchMotor.lastRequest,
            "FAULTED must cut power: VoltageOut(0.0)")
    }

    @Test fun reaching_position_wins_over_the_timeout() {
        startClimb()
        Climber.winchMotor.simulatePosition(40.0)
        Climber.climbTimer.simulateAdvance(2.5)
        Climber.periodic()
        assertEquals(Climber.State.CLIMBED, Climber.state,
            "Position reached AND timer expired on the same tick: the success row is checked before the timeout row, so this is CLIMBED, not FAULTED")
    }

    @Test fun nothing_leaves_climbed() {
        startClimb()
        Climber.winchMotor.simulatePosition(40.0)
        Climber.periodic()
        // Press everything and let time pass — CLIMBED must be terminal.
        Climber.commandedDeploy = true
        Climber.commandedClimb = true
        Climber.climbTimer.simulateAdvance(10.0)
        Climber.periodic()
        assertEquals(Climber.State.CLIMBED, Climber.state,
            "No input may leave CLIMBED — the ratchet is one-way; 2056 reset theirs by cutting a zip tie after the match")
        Climber.periodic()
        assertEquals(Climber.State.CLIMBED, Climber.state,
            "CLIMBED must still hold on later ticks — no escape rows, even via another state")
    }

    @Test fun nothing_leaves_faulted() {
        startClimb()
        Climber.winchMotor.simulatePosition(20.0)
        Climber.climbTimer.simulateAdvance(2.0)
        Climber.periodic()
        // Even reaching the climb position later must not un-fault.
        Climber.winchMotor.simulatePosition(40.0)
        Climber.climbTimer.simulateAdvance(5.0)
        Climber.commandedDeploy = true
        Climber.commandedClimb = true
        Climber.periodic()
        assertEquals(Climber.State.FAULTED, Climber.state,
            "No input may leave FAULTED — not even the winch reaching 40 rotations afterwards")
        Climber.periodic()
        assertEquals(Climber.State.FAULTED, Climber.state,
            "FAULTED must still hold on later ticks — it is terminal")
    }
}
