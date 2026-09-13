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
        assertEquals(Elevator.State.STOWED, Elevator.state, "reset() leaves the elevator STOWED")
        assertEquals(PositionVoltage(0.0), Elevator.motor.lastRequest, "STOWED holds position 0.0")
    }

    @Test fun targets_high_when_commanded() {
        Elevator.commandedTarget = Elevator.State.HIGH
        Elevator.periodic()
        assertEquals(Elevator.State.HIGH, Elevator.state, "at target, so the new command is accepted")
        assertEquals(PositionVoltage(14.5), Elevator.motor.lastRequest, "HIGH commands 14.5 rotations")
    }

    @Test fun all_setpoints_match_enum_property() {
        for (target in Elevator.State.entries) {
            Elevator.commandedTarget = target
            Elevator.periodic()
            assertEquals(
                PositionVoltage(target.targetRotations), Elevator.motor.lastRequest,
                "$target must command its own targetRotations",
            )
        }
    }

    @Test fun new_target_waits_until_current_move_finishes() {
        Elevator.commandedTarget = Elevator.State.HIGH
        Elevator.periodic()
        // Place the elevator part way along its travel.
        Elevator.motor.setPosition(7.0)
        Elevator.commandedTarget = Elevator.State.LOW
        Elevator.periodic()
        assertEquals(
            Elevator.State.HIGH, Elevator.state,
            "position 7.0 is not within 0.1 of 14.5, so the new LOW command must wait",
        )
        assertEquals(PositionVoltage(14.5), Elevator.motor.lastRequest, "keep driving to HIGH while waiting")
        // The stub snapped the position to 14.5. Now the move is finished.
        Elevator.periodic()
        assertEquals(Elevator.State.LOW, Elevator.state, "once at HIGH, the LOW command is accepted")
    }

    @Test fun atTarget_true_after_periodic_in_stub() {
        Elevator.commandedTarget = Elevator.State.LOW
        Elevator.periodic()
        // Stub: setControl(PositionVoltage(4.0)) sets simulated position to 4.0.
        assertTrue(Elevator.atTarget(), "position 4.0 equals the LOW target")
    }

    @Test fun atTarget_false_when_position_far_from_target() {
        Elevator.commandedTarget = Elevator.State.HIGH
        Elevator.periodic()
        Elevator.motor.setPosition(0.0)
        assertFalse(Elevator.atTarget(), "position 0.0 is 14.5 rotations below the HIGH target")
    }

    @Test fun atTarget_false_when_position_above_target() {
        Elevator.commandedTarget = Elevator.State.HIGH
        Elevator.periodic()
        Elevator.motor.setPosition(20.0)
        assertFalse(
            Elevator.atTarget(),
            "Position 20.0 is 5.5 rotations past the 14.5 target. atTarget() must be false on both sides of the target",
        )
    }
}
