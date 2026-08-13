package course.l9t4

import frc.stubs.Subsystem
import frc.stubs.lightning.Elevator
import frc.stubs.lightning.Wrist

// LIGHTNING's coral superstructure (binder p. 26): the two-stage elevator and
// the gripper wrist, driven as ONE state machine. Each SuperState names a
// full robot pose - an elevator setpoint plus a wrist setpoint. This enum IS
// this subsystem's constants block.
//
// TODO: fill in each state's real pose pair - see the pose table in task.md.
enum class SuperState(val elevator: Elevator.Setpoint, val wrist: Wrist.Setpoint) {
    HOME(Elevator.Setpoint.HOME, Wrist.Setpoint.STOWED),
    CORAL_PICKUP(Elevator.Setpoint.HOME, Wrist.Setpoint.STOWED),
    L1_SCORE(Elevator.Setpoint.HOME, Wrist.Setpoint.STOWED),
    L2_SCORE(Elevator.Setpoint.HOME, Wrist.Setpoint.STOWED),
    L3_SCORE(Elevator.Setpoint.HOME, Wrist.Setpoint.STOWED),
    L4_SCORE(Elevator.Setpoint.HOME, Wrist.Setpoint.STOWED),
    L1_SPIT(Elevator.Setpoint.HOME, Wrist.Setpoint.STOWED),
    L2_DUNK(Elevator.Setpoint.HOME, Wrist.Setpoint.STOWED),
    L3_DUNK(Elevator.Setpoint.HOME, Wrist.Setpoint.STOWED),
    L4_DUNK(Elevator.Setpoint.HOME, Wrist.Setpoint.STOWED),
}

object Superstructure : Subsystem {
    internal val elevator = Elevator()
    internal val wrist = Wrist()

    var state: SuperState = SuperState.HOME
        private set

    // Driver buttons.
    var commandedHome: Boolean = false
    var commandedCoralPickup: Boolean = false
    var commandedL1: Boolean = false
    var commandedL2: Boolean = false
    var commandedL3: Boolean = false
    var commandedL4: Boolean = false
    var commandedScore: Boolean = false

    // On the real robot this reads the Gripper FSM (task 8 wires that up);
    // the tests set it directly.
    var coralInGripper: Boolean = false

    override fun periodic() {
        stateTransitions()
        stateActions()
        elevator.tick()
        wrist.tick()
    }

    /** True once both mechanisms have settled at the current state's setpoints. */
    fun atTarget(): Boolean = elevator.atTarget() && wrist.atTarget()

    private fun stateTransitions() {
        // TODO: see task.md.
        TODO()
    }

    private fun stateActions() {
        // TODO: see task.md.
        TODO()
    }

    fun reset() {
        state = SuperState.HOME
        commandedHome = false
        commandedCoralPickup = false
        commandedL1 = false
        commandedL2 = false
        commandedL3 = false
        commandedL4 = false
        commandedScore = false
        coralInGripper = false
        elevator.reset()
        wrist.reset()
    }
}
