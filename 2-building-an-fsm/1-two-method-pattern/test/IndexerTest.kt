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
        assertEquals(Indexer.State.IDLE, Indexer.state)
        assertEquals(VoltageOut(0.0), Indexer.motor.lastRequest)
    }

    @Test fun command_starts_indexing_and_drives_forward() {
        Indexer.commandedIndex = true
        Indexer.periodic()
        assertEquals(Indexer.State.INDEXING, Indexer.state)
        assertEquals(VoltageOut(8.0), Indexer.motor.lastRequest)
    }

    @Test fun jam_sensor_transitions_to_jammed_and_runs_reverse() {
        Indexer.commandedIndex = true
        Indexer.periodic()
        Indexer.jamSensor.simulateValue(true)
        Indexer.periodic()
        assertEquals(Indexer.State.JAMMED, Indexer.state)
        assertEquals(VoltageOut(-3.0), Indexer.motor.lastRequest)
    }

    @Test fun release_returns_to_idle_from_indexing() {
        Indexer.commandedIndex = true
        Indexer.periodic()
        Indexer.commandedIndex = false
        Indexer.periodic()
        assertEquals(Indexer.State.IDLE, Indexer.state)
    }

    @Test fun release_returns_to_idle_from_jammed() {
        Indexer.commandedIndex = true
        Indexer.periodic()
        Indexer.jamSensor.simulateValue(true)
        Indexer.periodic()
        Indexer.commandedIndex = false
        Indexer.periodic()
        assertEquals(Indexer.State.IDLE, Indexer.state)
    }
}
