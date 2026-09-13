package course.l2t1

import frc.stubs.VoltageOut
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class IndexerTest {
    @BeforeTest fun setUp() {
        Indexer.reset()
    }

    @Test fun starts_in_idle_with_motor_off() {
        Indexer.periodic()
        assertEquals(
            Indexer.State.IDLE, Indexer.state,
            "With no command the indexer should stay in IDLE"
        )
        assertEquals(
            VoltageOut(0.0), Indexer.motor.lastRequest,
            "IDLE should command VoltageOut(0.0)"
        )
    }

    @Test fun command_starts_indexing_and_drives_forward() {
        Indexer.commandedIndex = true
        Indexer.periodic()
        assertEquals(
            Indexer.State.INDEXING, Indexer.state,
            "commandedIndex from IDLE should transition to INDEXING"
        )
        assertEquals(
            VoltageOut(8.0), Indexer.motor.lastRequest,
            "INDEXING should command VoltageOut(8.0)"
        )
    }

    @Test fun jam_sensor_transitions_to_jammed_and_runs_reverse() {
        Indexer.commandedIndex = true
        Indexer.periodic()
        Indexer.jamSensor.simulateValue(true)
        Indexer.periodic()
        assertEquals(
            Indexer.State.JAMMED, Indexer.state,
            "jamSensor true while INDEXING should transition to JAMMED"
        )
        assertEquals(
            VoltageOut(-3.0), Indexer.motor.lastRequest,
            "JAMMED should command VoltageOut(-3.0)"
        )
    }

    @Test fun jam_clearing_while_commanded_stays_jammed() {
        Indexer.commandedIndex = true
        Indexer.periodic()
        Indexer.jamSensor.simulateValue(true)
        Indexer.periodic()
        Indexer.jamSensor.simulateValue(false)
        Indexer.periodic()
        assertEquals(
            Indexer.State.JAMMED, Indexer.state,
            "The only exit from JAMMED is releasing commandedIndex; a cleared sensor alone must not leave JAMMED"
        )
    }

    @Test fun release_returns_to_idle_from_indexing() {
        Indexer.commandedIndex = true
        Indexer.periodic()
        Indexer.commandedIndex = false
        Indexer.periodic()
        assertEquals(
            Indexer.State.IDLE, Indexer.state,
            "Releasing commandedIndex while INDEXING should return to IDLE"
        )
    }

    @Test fun release_returns_to_idle_from_jammed() {
        Indexer.commandedIndex = true
        Indexer.periodic()
        Indexer.jamSensor.simulateValue(true)
        Indexer.periodic()
        Indexer.commandedIndex = false
        Indexer.periodic()
        assertEquals(
            Indexer.State.IDLE, Indexer.state,
            "Releasing commandedIndex while JAMMED should return to IDLE"
        )
    }
}
