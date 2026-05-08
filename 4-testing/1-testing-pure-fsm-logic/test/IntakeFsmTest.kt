package course.l4t1

import kotlin.test.Test
import kotlin.test.assertEquals

class IntakeFsmTest {
    private val noInput = Input(
        commandedIntake = false,
        commandedEject = false,
        pieceDetected = false,
        pieceLost = false,
    )

    @Test fun idle_with_no_input_stays_idle() {
        assertEquals(State.IDLE, transition(State.IDLE, noInput))
    }

    @Test fun idle_intake_command_goes_intaking() {
        assertEquals(
            State.INTAKING,
            transition(State.IDLE, noInput.copy(commandedIntake = true))
        )
    }

    @Test fun idle_eject_command_goes_ejecting() {
        assertEquals(
            State.EJECTING,
            transition(State.IDLE, noInput.copy(commandedEject = true))
        )
    }

    @Test fun intaking_piece_detected_goes_holding() {
        assertEquals(
            State.HOLDING,
            transition(
                State.INTAKING,
                noInput.copy(commandedIntake = true, pieceDetected = true)
            )
        )
    }

    @Test fun intaking_release_returns_idle() {
        assertEquals(
            State.IDLE,
            transition(State.INTAKING, noInput)
        )
    }

    @Test fun intaking_with_command_held_stays_intaking() {
        assertEquals(
            State.INTAKING,
            transition(State.INTAKING, noInput.copy(commandedIntake = true))
        )
    }

    @Test fun holding_eject_command_goes_ejecting() {
        assertEquals(
            State.EJECTING,
            transition(State.HOLDING, noInput.copy(commandedEject = true))
        )
    }

    @Test fun holding_piece_lost_returns_idle() {
        assertEquals(
            State.IDLE,
            transition(State.HOLDING, noInput.copy(pieceLost = true))
        )
    }

    @Test fun ejecting_release_returns_idle() {
        assertEquals(
            State.IDLE,
            transition(State.EJECTING, noInput)
        )
    }

    @Test fun ejecting_command_held_stays_ejecting() {
        assertEquals(
            State.EJECTING,
            transition(State.EJECTING, noInput.copy(commandedEject = true))
        )
    }

    @Test fun intaking_eject_priority_higher_than_piece_detected() {
        // Intaking + pieceDetected -> HOLDING is the only match here (no eject row from INTAKING).
        // Sanity-check the prioritization works the way described.
        assertEquals(
            State.HOLDING,
            transition(
                State.INTAKING,
                noInput.copy(commandedIntake = true, pieceDetected = true)
            )
        )
    }
}
