package course.l7t6

import frc.stubs.NeutralOut
import frc.stubs.OI
import frc.stubs.PositionVoltage
import frc.stubs.VoltageOut
import frc.stubs.superstructure.Arm
import frc.stubs.superstructure.Elevator
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RobotLoopTest {
    @BeforeTest fun setUp() {
        StateMachineManager.reset()
    }

    // Ticks until the condition holds. Returns the number of ticks used, or fails.
    private fun runUntil(maxTicks: Int, what: String, condition: () -> Boolean): Int {
        repeat(maxTicks) { i ->
            StateMachineManager.teleopPeriodic()
            if (condition()) return i + 1
        }
        throw AssertionError("$what did not happen within $maxTicks ticks (SuperStructure=${SuperStructure.state}, Intake=${Intake.state}, Climber=${Climber.state})")
    }

    private fun settledAt(pose: Pose) = SuperStructure.state == pose && SuperStructure.atPosition

    @Test fun full_match_through_the_manager() {
        // Intake a piece at the coral station.
        OI.intake = true
        runUntil(10, "reach CORAL_STATION") { settledAt(Pose.CORAL_STATION) }
        assertEquals(Intake.State.INTAKING, Intake.state, "OI.intake should start the intake rollers")
        Intake.motor.simulateStatorCurrent(15.0)
        runUntil(10, "hold the piece") { Intake.state == Intake.State.HOLDING }
        Intake.motor.simulateStatorCurrent(0.0)
        OI.intake = false

        // Home, then raise to L4.
        OI.home = true
        runUntil(10, "return HOME") { settledAt(Pose.HOME) }
        OI.home = false
        OI.scoreL4 = true
        runUntil(15, "reach L4") { settledAt(Pose.L4) }
        assertEquals(Intake.State.HOLDING, Intake.state, "the intake must keep holding while the superstructure moves")

        // Score.
        OI.score = true
        StateMachineManager.teleopPeriodic()
        assertEquals(Intake.State.EJECTING, Intake.state, "score at a settled L4 should eject")
        assertEquals(VoltageOut(-6.0), Intake.motor.lastRequest, "EJECTING should drive the rollers at -6 V")
        OI.score = false
        StateMachineManager.teleopPeriodic()
        assertEquals(Intake.State.IDLE, Intake.state, "releasing score should return the intake to IDLE")
        assertEquals(NeutralOut, Intake.motor.lastRequest, "IDLE should send NeutralOut")

        // Home, prepare to climb, climb.
        OI.scoreL4 = false
        OI.home = true
        runUntil(15, "return HOME from L4") { settledAt(Pose.HOME) }
        OI.home = false
        OI.prepareClimb = true
        runUntil(10, "reach READY_TO_CLIMB") { settledAt(Pose.READY_TO_CLIMB) }
        OI.prepareClimb = false
        assertEquals(Climber.State.RETRACTED, Climber.state, "the climber must stay retracted at READY_TO_CLIMB")
        OI.climb = true
        runUntil(10, "reach FULLY_CLIMBED") { settledAt(Pose.FULLY_CLIMBED) }
        assertEquals(Climber.State.EXTENDED, Climber.state, "the climber should extend once FULLY_CLIMBED is settled")
        assertEquals(PositionVoltage(72.0), Climber.motor.lastRequest, "EXTENDED should command 72 rotations")
    }

    @Test fun climber_extends_on_the_same_tick_the_superstructure_settles() {
        OI.prepareClimb = true
        runUntil(10, "reach READY_TO_CLIMB") { settledAt(Pose.READY_TO_CLIMB) }
        OI.prepareClimb = false
        OI.climb = true
        // Tick until the superstructure first reports settled at FULLY_CLIMBED,
        // then look at the climber in that same tick.
        runUntil(10, "reach FULLY_CLIMBED") { settledAt(Pose.FULLY_CLIMBED) }
        assertEquals(
            Climber.State.EXTENDED,
            Climber.state,
            "the climber reads SuperStructure, so it must run after SuperStructure; otherwise it extends one tick late",
        )
    }

    @Test fun reset_restores_every_machine() {
        OI.intake = true
        runUntil(10, "reach CORAL_STATION") { settledAt(Pose.CORAL_STATION) }
        Intake.motor.simulateStatorCurrent(15.0)
        runUntil(10, "hold the piece") { Intake.state == Intake.State.HOLDING }

        StateMachineManager.reset()

        assertFalse(OI.intake, "reset() must reset OI")
        assertEquals(Pose.HOME, SuperStructure.state, "reset() must reset SuperStructure to HOME")
        assertEquals(0.0, Elevator.position, "reset() must reset the elevator (SuperStructure.reset() does it)")
        assertEquals(90.0, Arm.angle, "reset() must reset the arm (SuperStructure.reset() does it)")
        assertEquals(Intake.State.IDLE, Intake.state, "reset() must reset Intake")
        assertEquals(NeutralOut, Intake.motor.lastRequest, "Intake.reset() stops the rollers")
        assertEquals(Climber.State.RETRACTED, Climber.state, "reset() must reset Climber")
        assertTrue(SuperStructure.atPosition, "a reset robot is settled at HOME")
    }
}
