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
        assertEquals(
            State.IDLE,
            transition(State.IDLE, noInput),
            "IDLE with no commands and no sensor input should stay IDLE"
        )
    }

    @Test fun idle_intake_command_goes_intaking() {
        assertEquals(
            State.INTAKING,
            transition(State.IDLE, noInput.copy(commandedIntake = true)),
            "IDLE with the intake command should go to INTAKING"
        )
    }

    @Test fun idle_eject_command_goes_ejecting() {
        assertEquals(
            State.EJECTING,
            transition(State.IDLE, noInput.copy(commandedEject = true)),
            "IDLE with the eject command should go to EJECTING"
        )
    }

    @Test fun intaking_piece_detected_goes_holding() {
        assertEquals(
            State.HOLDING,
            transition(
                State.INTAKING,
                noInput.copy(commandedIntake = true, pieceDetected = true)
            ),
            "INTAKING with a piece detected should go to HOLDING"
        )
    }

    @Test fun intaking_release_returns_idle() {
        assertEquals(
            State.IDLE,
            transition(State.INTAKING, noInput),
            "INTAKING with the intake command released should return to IDLE"
        )
    }

    @Test fun intaking_with_command_held_stays_intaking() {
        assertEquals(
            State.INTAKING,
            transition(State.INTAKING, noInput.copy(commandedIntake = true)),
            "INTAKING with the command held and no piece yet should stay INTAKING"
        )
    }

    @Test fun holding_eject_command_goes_ejecting() {
        assertEquals(
            State.EJECTING,
            transition(State.HOLDING, noInput.copy(commandedEject = true)),
            "HOLDING with the eject command should go to EJECTING"
        )
    }

    @Test fun holding_piece_lost_returns_idle() {
        assertEquals(
            State.IDLE,
            transition(State.HOLDING, noInput.copy(pieceLost = true)),
            "HOLDING with the piece lost should return to IDLE"
        )
    }

    @Test fun ejecting_release_returns_idle() {
        assertEquals(
            State.IDLE,
            transition(State.EJECTING, noInput),
            "EJECTING with the eject command released should return to IDLE"
        )
    }

    @Test fun ejecting_command_held_stays_ejecting() {
        assertEquals(
            State.EJECTING,
            transition(State.EJECTING, noInput.copy(commandedEject = true)),
            "EJECTING with the eject command held should stay EJECTING"
        )
    }

    @Test fun intaking_piece_detected_beats_command_release() {
        // Both INTAKING rows match here; pieceDetected -> HOLDING is listed higher.
        assertEquals(
            State.HOLDING,
            transition(State.INTAKING, noInput.copy(pieceDetected = true)),
            "INTAKING with a piece detected AND the command released should prefer the higher row: HOLDING, not IDLE"
        )
    }

    @Test fun holding_eject_command_beats_piece_lost() {
        // Both HOLDING rows match here; commandedEject -> EJECTING is listed higher.
        assertEquals(
            State.EJECTING,
            transition(
                State.HOLDING,
                noInput.copy(commandedEject = true, pieceLost = true)
            ),
            "HOLDING with the eject command AND the piece lost should prefer the higher row: EJECTING, not IDLE"
        )
    }

    @Test fun idle_intake_command_beats_eject_command() {
        // Both IDLE rows match here; commandedIntake -> INTAKING is listed higher.
        assertEquals(
            State.INTAKING,
            transition(
                State.IDLE,
                noInput.copy(commandedIntake = true, commandedEject = true)
            ),
            "IDLE with both intake and eject commanded should prefer the higher row: INTAKING"
        )
    }
}
