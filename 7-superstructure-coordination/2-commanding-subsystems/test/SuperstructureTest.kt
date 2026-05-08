package course.l7t2

import frc.stubs.superstructure.Arm
import frc.stubs.superstructure.Elevator
import frc.stubs.superstructure.Intake
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SuperstructureTest {
    private lateinit var s: Superstructure

    @BeforeTest fun setUp() {
        s = Superstructure()
    }

    @Test fun stowed_at_start() {
        assertEquals(RobotState.STOWED, s.commandedRobotState)
    }

    @Test fun stowed_pushes_stowed_setpoints() {
        s.periodic()
        assertEquals(Elevator.State.STOWED, s.elevator.commandedTarget)
        assertEquals(Arm.State.STOWED, s.arm.commandedTarget)
        assertEquals(Intake.Mode.IDLE, s.intake.commandedMode)
    }

    @Test fun intake_ground_pushes_subsystem_targets() {
        s.commandedRobotState = RobotState.INTAKE_GROUND
        s.periodic()
        assertEquals(Elevator.State.LOW, s.elevator.commandedTarget)
        assertEquals(Arm.State.GROUND, s.arm.commandedTarget)
        assertEquals(Intake.Mode.INTAKING, s.intake.commandedMode)
    }

    @Test fun score_l4_pushes_subsystem_targets() {
        s.commandedRobotState = RobotState.SCORE_L4
        s.periodic()
        assertEquals(Elevator.State.HIGH, s.elevator.commandedTarget)
        assertEquals(Arm.State.SCORE, s.arm.commandedTarget)
        assertEquals(Intake.Mode.HOLDING, s.intake.commandedMode)
    }

    @Test fun climb_prep_pushes_subsystem_targets() {
        s.commandedRobotState = RobotState.CLIMB_PREP
        s.periodic()
        assertEquals(Elevator.State.STOWED, s.elevator.commandedTarget)
        assertEquals(Arm.State.CLIMB, s.arm.commandedTarget)
        assertEquals(Intake.Mode.IDLE, s.intake.commandedMode)
    }

    @Test fun changing_state_re_pushes_setpoints() {
        s.commandedRobotState = RobotState.SCORE_L4
        s.periodic()
        s.commandedRobotState = RobotState.INTAKE_GROUND
        s.periodic()
        assertEquals(Elevator.State.LOW, s.elevator.commandedTarget)
        assertEquals(Arm.State.GROUND, s.arm.commandedTarget)
        assertEquals(Intake.Mode.INTAKING, s.intake.commandedMode)
    }
}
