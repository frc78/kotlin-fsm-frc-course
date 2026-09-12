package course.l1t12

import kotlin.test.Test
import kotlin.test.assertEquals

class ShooterFsmStateTest {
    @Test fun idle_describes() {
        assertEquals("Idle", describe(ShooterFsmState.Idle))
    }

    @Test fun spinningUp_includesRpm() {
        assertEquals(
            "Spinning up to 4500.0 rpm",
            describe(ShooterFsmState.SpinningUp(4500.0))
        )
    }

    @Test fun spinningUp_withDifferentRpm() {
        assertEquals(
            "Spinning up to 5200.0 rpm",
            describe(ShooterFsmState.SpinningUp(5200.0))
        )
    }

    @Test fun ready_describes() {
        assertEquals("Ready to fire", describe(ShooterFsmState.Ready))
    }

    @Test fun feeding_includesRpm() {
        assertEquals(
            "Feeding at 4500.0 rpm",
            describe(ShooterFsmState.Feeding(4500.0))
        )
    }

    @Test fun feeding_withDifferentRpm() {
        assertEquals(
            "Feeding at 6000.0 rpm",
            describe(ShooterFsmState.Feeding(6000.0)),
            "describe(Feeding(6000.0)) must interpolate that state's own targetRpm, not a fixed number"
        )
    }

    @Test fun spinningUp_holdsItsRpm() {
        val s = ShooterFsmState.SpinningUp(3300.0)
        assertEquals(3300.0, s.targetRpm)
    }
}
