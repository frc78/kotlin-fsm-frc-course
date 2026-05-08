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
        assertEquals(Pivot.State.STOWED, Pivot.state)
    }

    @Test fun guard_blocks_move_when_elevator_not_clear() {
        Pivot.targetRotations = 5.0
        Pivot.commandedMove = true
        Pivot.elevatorClear = false
        Pivot.periodic()
        assertEquals(Pivot.State.STOWED, Pivot.state)
    }

    @Test fun guard_allows_move_when_elevator_clear() {
        Pivot.targetRotations = 5.0
        Pivot.commandedMove = true
        Pivot.elevatorClear = true
        Pivot.periodic()
        assertEquals(Pivot.State.MOVING, Pivot.state)
    }

    @Test fun guard_re_evaluated_on_each_tick() {
        Pivot.targetRotations = 5.0
        Pivot.commandedMove = true
        Pivot.elevatorClear = false
        Pivot.periodic()
        assertEquals(Pivot.State.STOWED, Pivot.state)
        Pivot.elevatorClear = true
        Pivot.periodic()
        assertEquals(Pivot.State.MOVING, Pivot.state)
    }

    @Test fun reaches_target_when_position_close() {
        Pivot.targetRotations = 5.0
        Pivot.commandedMove = true
        Pivot.periodic()
        Pivot.periodic()
        assertEquals(Pivot.State.AT_TARGET, Pivot.state)
    }

    @Test fun returns_to_stowed_when_command_released() {
        Pivot.targetRotations = 5.0
        Pivot.commandedMove = true
        Pivot.periodic()
        Pivot.periodic()
        assertEquals(Pivot.State.AT_TARGET, Pivot.state)
        Pivot.commandedMove = false
        Pivot.periodic()
        assertEquals(Pivot.State.STOWED, Pivot.state)
    }
}
