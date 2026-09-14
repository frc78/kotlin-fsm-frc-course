package course.l7t5

import frc.stubs.OI
import frc.stubs.PositionVoltage
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ClimberTest {
    @BeforeTest fun setUp() {
        OI.reset()
        Intake.reset()
        SuperStructure.reset()
        Climber.reset()
    }

    // One robot tick: the superstructure runs before the climber that reads it.
    private fun tick() {
        SuperStructure.periodic()
        Climber.periodic()
    }

    private fun prepareToClimb() {
        OI.prepareClimb = true
        tick()
        OI.prepareClimb = false
        assertEquals(Pose.READY_TO_CLIMB, SuperStructure.state, "setup: superstructure should be READY_TO_CLIMB")
    }

    private fun tickUntilClimbedAndSettled(maxTicks: Int = 20) {
        repeat(maxTicks) {
            if (SuperStructure.state == Pose.FULLY_CLIMBED && SuperStructure.atPosition) return
            tick()
        }
    }

    @Test fun starts_retracted_and_holds_zero() {
        tick()
        assertEquals(Climber.State.RETRACTED, Climber.state, "climber should start RETRACTED")
        assertEquals(PositionVoltage(0.0), Climber.motor.lastRequest, "RETRACTED must send PositionVoltage(0.0) every tick")
    }

    @Test fun stays_retracted_at_ready_to_climb() {
        prepareToClimb()
        repeat(5) { tick() }
        assertTrue(SuperStructure.atPosition, "setup: superstructure should be settled at READY_TO_CLIMB")
        assertEquals(Climber.State.RETRACTED, Climber.state, "READY_TO_CLIMB is not FULLY_CLIMBED; the climber must stay RETRACTED")
    }

    @Test fun stays_retracted_while_fully_climbed_pose_is_still_moving() {
        prepareToClimb()
        repeat(3) { tick() }
        OI.climb = true
        tick()
        assertEquals(Pose.FULLY_CLIMBED, SuperStructure.state, "setup: superstructure should be FULLY_CLIMBED")
        assertFalse(SuperStructure.atPosition, "setup: the arm should still be swinging down")
        assertEquals(Climber.State.RETRACTED, Climber.state, "the pose alone is not enough; wait for SuperStructure.atPosition")
    }

    @Test fun extends_once_superstructure_is_climbed_and_settled() {
        prepareToClimb()
        repeat(3) { tick() }
        OI.climb = true
        tickUntilClimbedAndSettled()
        assertTrue(SuperStructure.atPosition, "setup: superstructure should be settled at FULLY_CLIMBED")
        tick()
        assertEquals(Climber.State.EXTENDED, Climber.state, "FULLY_CLIMBED and atPosition must move the climber to EXTENDED")
        assertEquals(PositionVoltage(72.0), Climber.motor.lastRequest, "EXTENDED must send PositionVoltage(72.0)")
    }

    @Test fun never_retracts_again() {
        prepareToClimb()
        repeat(3) { tick() }
        OI.climb = true
        tickUntilClimbedAndSettled()
        tick()
        assertEquals(Climber.State.EXTENDED, Climber.state, "setup: climber should be EXTENDED")
        SuperStructure.reset()   // the superstructure leaves FULLY_CLIMBED
        repeat(3) { tick() }
        assertEquals(Climber.State.EXTENDED, Climber.state, "EXTENDED has no exit; the climber must stay EXTENDED")
        assertEquals(PositionVoltage(72.0), Climber.motor.lastRequest, "EXTENDED must keep sending PositionVoltage(72.0)")
    }
}
