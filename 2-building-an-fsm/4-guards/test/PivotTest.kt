package course.l2t4

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PivotTest {
    @BeforeTest fun setUp() {
        Pivot.reset()
    }

    @Test fun starts_stowed() {
        Pivot.periodic()
        assertEquals(
            Pivot.State.STOWED, Pivot.state,
            "With no command the pivot should stay STOWED"
        )
    }

    @Test fun guard_blocks_move_when_elevator_not_clear() {
        Pivot.targetRotations = 5.0
        Pivot.commandedMove = true
        Pivot.elevatorClear = false
        Pivot.periodic()
        assertEquals(
            Pivot.State.STOWED, Pivot.state,
            "commandedMove with elevatorClear false must not leave STOWED; the guard blocks it"
        )
    }

    @Test fun guard_allows_move_when_elevator_clear() {
        Pivot.targetRotations = 5.0
        Pivot.commandedMove = true
        Pivot.elevatorClear = true
        Pivot.periodic()
        assertEquals(
            Pivot.State.MOVING, Pivot.state,
            "commandedMove with elevatorClear true should transition to MOVING"
        )
    }

    @Test fun guard_re_evaluated_on_each_tick() {
        Pivot.targetRotations = 5.0
        Pivot.commandedMove = true
        Pivot.elevatorClear = false
        Pivot.periodic()
        assertEquals(
            Pivot.State.STOWED, Pivot.state,
            "While the guard is false the pivot should stay STOWED"
        )
        Pivot.elevatorClear = true
        Pivot.periodic()
        assertEquals(
            Pivot.State.MOVING, Pivot.state,
            "Once the guard becomes true the transition should fire on the next tick"
        )
    }

    @Test fun reaches_target_when_position_close() {
        Pivot.targetRotations = 5.0
        Pivot.commandedMove = true
        Pivot.periodic()
        Pivot.periodic()
        assertEquals(
            Pivot.State.AT_TARGET, Pivot.state,
            "Once the position is within 0.1 rotations of the target, MOVING should transition to AT_TARGET"
        )
    }

    @Test fun new_target_while_moving_stays_moving() {
        Pivot.targetRotations = 5.0
        Pivot.commandedMove = true
        Pivot.periodic()
        Pivot.targetRotations = 8.0
        Pivot.periodic()
        assertEquals(
            Pivot.State.MOVING, Pivot.state,
            "The position is 5.0 but the target is now 8.0; MOVING must stay MOVING until the position is within 0.1 of the target"
        )
        Pivot.periodic()
        assertEquals(
            Pivot.State.AT_TARGET, Pivot.state,
            "After the motor reaches 8.0, MOVING should transition to AT_TARGET"
        )
    }

    @Test fun returns_to_stowed_when_command_released() {
        Pivot.targetRotations = 5.0
        Pivot.commandedMove = true
        Pivot.periodic()
        Pivot.periodic()
        assertEquals(
            Pivot.State.AT_TARGET, Pivot.state,
            "Two ticks after the move command the pivot should be AT_TARGET"
        )
        Pivot.commandedMove = false
        Pivot.periodic()
        assertEquals(
            Pivot.State.STOWED, Pivot.state,
            "Releasing commandedMove while AT_TARGET should return to STOWED"
        )
    }
}
