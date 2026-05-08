package course.l3t2

import frc.stubs.PositionVoltage
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ElevatorTest {
    @BeforeTest fun setUp() {
        Elevator.reset()
    }

    @Test fun starts_stowed_with_zero_target() {
        Elevator.periodic()
        assertEquals(Elevator.State.STOWED, Elevator.state)
        assertEquals(PositionVoltage(0.0), Elevator.motor.lastRequest)
    }

    @Test fun targets_high_when_commanded() {
        Elevator.commandedTarget = Elevator.State.HIGH
        Elevator.periodic()
        assertEquals(Elevator.State.HIGH, Elevator.state)
        assertEquals(PositionVoltage(14.5), Elevator.motor.lastRequest)
    }

    @Test fun all_setpoints_match_enum_property() {
        for (target in Elevator.State.entries) {
            Elevator.commandedTarget = target
            Elevator.periodic()
            assertEquals(PositionVoltage(target.targetRotations), Elevator.motor.lastRequest)
        }
    }

    @Test fun atTarget_true_after_periodic_in_stub() {
        Elevator.commandedTarget = Elevator.State.LOW
        Elevator.periodic()
        // Stub: setControl(PositionVoltage(4.0)) sets simulated position to 4.0.
        assertTrue(Elevator.atTarget())
    }

    @Test fun atTarget_false_when_position_far_from_target() {
        Elevator.commandedTarget = Elevator.State.HIGH
        // Force position to 0 explicitly; don't run periodic so motor stays put.
        Elevator.motor.setPosition(0.0)
        // We still need the state to reflect HIGH for atTarget to compare correctly.
        // Trigger the transition without re-issuing the position command:
        Elevator.periodic()
        Elevator.motor.setPosition(0.0)
        assertFalse(Elevator.atTarget())
    }
}
