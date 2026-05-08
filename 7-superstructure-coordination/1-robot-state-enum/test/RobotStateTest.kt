package course.l7t1

import frc.stubs.superstructure.Arm
import frc.stubs.superstructure.Elevator
import frc.stubs.superstructure.Intake
import kotlin.test.Test
import kotlin.test.assertEquals

class RobotStateTest {
    @Test fun has_four_states() {
        assertEquals(4, RobotState.entries.size)
    }

    @Test fun stowed_setpoints() {
        val s = RobotState.STOWED
        assertEquals(Elevator.State.STOWED, s.elevator)
        assertEquals(Arm.State.STOWED, s.arm)
        assertEquals(Intake.Mode.IDLE, s.intake)
    }

    @Test fun intake_ground_setpoints() {
        val s = RobotState.INTAKE_GROUND
        assertEquals(Elevator.State.LOW, s.elevator)
        assertEquals(Arm.State.GROUND, s.arm)
        assertEquals(Intake.Mode.INTAKING, s.intake)
    }

    @Test fun score_l4_setpoints() {
        val s = RobotState.SCORE_L4
        assertEquals(Elevator.State.HIGH, s.elevator)
        assertEquals(Arm.State.SCORE, s.arm)
        assertEquals(Intake.Mode.HOLDING, s.intake)
    }

    @Test fun climb_prep_setpoints() {
        val s = RobotState.CLIMB_PREP
        assertEquals(Elevator.State.STOWED, s.elevator)
        assertEquals(Arm.State.CLIMB, s.arm)
        assertEquals(Intake.Mode.IDLE, s.intake)
    }
}
