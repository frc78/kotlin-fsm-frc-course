package course.l2t3

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ClimberTest {
    @BeforeTest fun setUp() {
        Climber.reset()
    }

    @Test fun stowed_logged_on_first_tick() {
        Climber.periodic()
        assertEquals(
            listOf("stowed"), Climber.deploymentLogs,
            "On the first tick previousState is null, so the initial STOWED state should be logged once"
        )
    }

    @Test fun stowed_not_logged_repeatedly() {
        Climber.periodic()
        Climber.periodic()
        Climber.periodic()
        assertEquals(
            listOf("stowed"), Climber.deploymentLogs,
            "Ticks that stay in the same state must not add a log line"
        )
    }

    @Test fun deploying_logged_on_transition_then_not_again() {
        Climber.periodic()
        Climber.commandedDeploy = true
        Climber.periodic()
        Climber.periodic()
        assertEquals(
            listOf("stowed", "deploying"), Climber.deploymentLogs,
            "Entering DEPLOYING should log \"deploying\" once, and a quiet tick in DEPLOYING should add nothing"
        )
    }

    @Test fun full_sequence_logs_each_state_once() {
        Climber.periodic()
        Climber.commandedDeploy = true
        Climber.periodic()
        Climber.motor.simulatePosition(6.0)
        Climber.periodic()
        Climber.commandedClimb = true
        Climber.periodic()
        Climber.periodic()
        assertEquals(
            listOf("stowed", "deploying", "deployed", "climbing"),
            Climber.deploymentLogs,
            "Each state entered should appear exactly once, in order"
        )
    }
}
