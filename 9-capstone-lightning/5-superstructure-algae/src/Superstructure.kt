package course.l9t5

import frc.stubs.Subsystem
import frc.stubs.lightning.Elevator
import frc.stubs.lightning.Wrist

// The full LIGHTNING superstructure (binder p. 26): the coral half from the
// previous task plus the algae branch.
enum class SuperState(val elevator: Elevator.Setpoint, val wrist: Wrist.Setpoint) {
    // Coral half (previous task).
    HOME(Elevator.Setpoint.HOME, Wrist.Setpoint.STOWED),
    CORAL_PICKUP(Elevator.Setpoint.CORAL_PICKUP, Wrist.Setpoint.CORAL_PICKUP),
    L1_SCORE(Elevator.Setpoint.L1, Wrist.Setpoint.SCORE),
    L2_SCORE(Elevator.Setpoint.L2, Wrist.Setpoint.SCORE),
    L3_SCORE(Elevator.Setpoint.L3, Wrist.Setpoint.SCORE),
    L4_SCORE(Elevator.Setpoint.L4, Wrist.Setpoint.SCORE),
    L1_SPIT(Elevator.Setpoint.L1, Wrist.Setpoint.SPIT),
    L2_DUNK(Elevator.Setpoint.L2_DUNK, Wrist.Setpoint.DUNK),
    L3_DUNK(Elevator.Setpoint.L3_DUNK, Wrist.Setpoint.DUNK),
    L4_DUNK(Elevator.Setpoint.L4_DUNK, Wrist.Setpoint.DUNK),

    // Algae branch (new in this task).
    // TODO: fill in each algae state's real pose pair - see the pose table in task.md.
    ALGAE_FLOOR_PICKUP(Elevator.Setpoint.HOME, Wrist.Setpoint.STOWED),
    L2_ALGAE_PICKUP(Elevator.Setpoint.HOME, Wrist.Setpoint.STOWED),
    L3_ALGAE_PICKUP(Elevator.Setpoint.HOME, Wrist.Setpoint.STOWED),
    ALGAE_HOME(Elevator.Setpoint.HOME, Wrist.Setpoint.STOWED),
    NET_SCORE(Elevator.Setpoint.HOME, Wrist.Setpoint.STOWED),
    PROCESSOR_SCORE(Elevator.Setpoint.HOME, Wrist.Setpoint.STOWED),
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
    var commandedAlgaeFloor: Boolean = false

    // On the real robot these read the Gripper FSM (task 8 wires that up);
    // the tests set them directly.
    var coralInGripper: Boolean = false
    var algaeInGripper: Boolean = false

    override fun periodic() {
        stateTransitions()
        stateActions()
        elevator.tick()
        wrist.tick()
    }

    /** True once both mechanisms have settled at the current state's setpoints. */
    fun atTarget(): Boolean = elevator.atTarget() && wrist.atTarget()

    private fun stateTransitions() {
        val stateAtTickStart = state

        // 1. Per-state rows, coral half - as built in the previous task.
        when (state) {
            SuperState.HOME -> if (commandedCoralPickup) state = SuperState.CORAL_PICKUP
            SuperState.L1_SCORE -> if (commandedScore) state = SuperState.L1_SPIT
            SuperState.L2_SCORE -> if (commandedScore) state = SuperState.L2_DUNK
            SuperState.L3_SCORE -> if (commandedScore) state = SuperState.L3_DUNK
            SuperState.L4_SCORE -> if (commandedScore) state = SuperState.L4_DUNK
            else -> {}
        }

        // 2. Per-state rows, algae branch - your work.
        algaeTransitions()

        // 3. One transition per tick: if a per-state row already fired, skip
        //    the global rows so a lower-priority row cannot overwrite it.
        if (state != stateAtTickStart) return

        // 4. Global rows, coral half - legal from any state, lowest priority.
        if (commandedL1 && coralInGripper) state = SuperState.L1_SCORE
        else if (commandedL2 && coralInGripper) state = SuperState.L2_SCORE
        else if (commandedL3 && coralInGripper) state = SuperState.L3_SCORE
        else if (commandedL4 && coralInGripper) state = SuperState.L4_SCORE
        else if (commandedHome) state = SuperState.HOME
    }

    private fun algaeTransitions() {
        // TODO: see task.md.
        TODO()
    }

    private fun stateActions() {
        elevator.commandedTarget = state.elevator
        wrist.commandedTarget = state.wrist
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
        commandedAlgaeFloor = false
        coralInGripper = false
        algaeInGripper = false
        elevator.reset()
        wrist.reset()
    }
}
