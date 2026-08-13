package course.l9t8

import frc.stubs.DigitalInput
import frc.stubs.Subsystem
import frc.stubs.geometry.Pose2d
import frc.stubs.geometry.Rotation2d
import frc.stubs.lightning.Elevator
import frc.stubs.lightning.Wrist

// ===========================================================================
// Pre-written mini subsystems — trimmed-down versions of the FSMs you built
// in tasks 1, 3, 4, and 6 (fewer states, motor actions omitted where the
// coordination logic doesn't need them). Read them; don't edit them.
// Your work is `object Lightning` at the bottom of the file.
// ===========================================================================

// Task 1's slap-down intake: deploy on the button, auto-retract on the beam
// break (the sensor row outranks the driver's button).
object MiniIntake : Subsystem {
    enum class State { STOWED, INTAKING }

    internal val beamBreak = DigitalInput(channel = 0)

    var state: State = State.STOWED
        private set

    var commandedDeploy = false

    override fun periodic() = stateTransitions()

    private fun stateTransitions() {
        when (state) {
            State.STOWED -> if (commandedDeploy) state = State.INTAKING
            State.INTAKING -> if (beamBreak.get() || !commandedDeploy) state = State.STOWED
        }
    }

    fun reset() {
        state = State.STOWED
        commandedDeploy = false
        beamBreak.simulateValue(false)
    }
}

// Task 3's gripper, coral path only: the cradle beam break starts the
// handoff with no driver input; the gripper's own beam break confirms
// possession.
object MiniGripper : Subsystem {
    enum class State { EMPTY, HANDOFF, HOLDING_CORAL }

    internal val cradleBeam = DigitalInput(channel = 2)
    internal val gripperBeam = DigitalInput(channel = 3)

    var state: State = State.EMPTY
        private set

    fun hasCoral(): Boolean = state == State.HOLDING_CORAL

    override fun periodic() = stateTransitions()

    private fun stateTransitions() {
        when (state) {
            State.EMPTY -> if (cradleBeam.get()) state = State.HANDOFF
            State.HANDOFF -> if (gripperBeam.get()) state = State.HOLDING_CORAL
            State.HOLDING_CORAL -> {}
        }
    }

    fun reset() {
        state = State.EMPTY
        cradleBeam.simulateValue(false)
        gripperBeam.simulateValue(false)
    }
}

// Task 4's coral superstructure, L4 branch only. `coralInGripper` is the
// guard on raising — in this task it is fed by Lightning.periodic(), not by
// a test setting it directly.
object MiniSuperstructure : Subsystem {
    enum class State(val elevator: Elevator.Setpoint, val wrist: Wrist.Setpoint) {
        HOME(Elevator.Setpoint.HOME, Wrist.Setpoint.STOWED),
        L4_SCORE(Elevator.Setpoint.L4, Wrist.Setpoint.SCORE),
        L4_DUNK(Elevator.Setpoint.L4_DUNK, Wrist.Setpoint.DUNK),
    }

    internal val elevator = Elevator()
    internal val wrist = Wrist()

    var state: State = State.HOME
        private set

    var commandedL4 = false
    var commandedScore = false
    var commandedHome = false
    var coralInGripper = false

    fun atTarget(): Boolean = elevator.atTarget() && wrist.atTarget()

    override fun periodic() {
        stateTransitions()
        stateActions()
        elevator.tick()
        wrist.tick()
    }

    private fun stateTransitions() {
        // Per-state row first, then the global rows — first match wins.
        if (state == State.L4_SCORE && commandedScore) { state = State.L4_DUNK; return }
        if (commandedL4 && coralInGripper) { state = State.L4_SCORE; return }
        if (commandedHome) state = State.HOME
    }

    private fun stateActions() {
        elevator.commandedTarget = state.elevator
        wrist.commandedTarget = state.wrist
    }

    fun reset() {
        state = State.HOME
        commandedL4 = false
        commandedScore = false
        commandedHome = false
        coralInGripper = false
        elevator.reset()
        wrist.reset()
    }
}

// Task 6's drivebase: FIELD_DRIVE and AUTO_ALIGN only (tag-visible guard),
// request-building omitted. atPole() is the drive-to-point "close enough"
// check: within 0.05 m of the target pole.
object MiniDriveBase : Subsystem {
    enum class State { FIELD_DRIVE, AUTO_ALIGN }

    var state: State = State.FIELD_DRIVE
        private set

    var commandedAutoAlign = false
    var tagVisible = false
    var robotPose = Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0))
    var targetPole = Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0))

    fun atPole(): Boolean =
        (targetPole.translation - robotPose.translation).getNorm() < 0.05

    override fun periodic() = stateTransitions()

    private fun stateTransitions() {
        when (state) {
            State.FIELD_DRIVE -> if (commandedAutoAlign && tagVisible) state = State.AUTO_ALIGN
            State.AUTO_ALIGN -> if (!commandedAutoAlign) state = State.FIELD_DRIVE
        }
    }

    fun reset() {
        state = State.FIELD_DRIVE
        commandedAutoAlign = false
        tagVisible = false
        robotPose = Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0))
        targetPole = Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0))
    }
}

// ===========================================================================
// Your work starts here.
// ===========================================================================

enum class LedColor { OFF, WHITE, GREEN, BLUE, RED }

object Lightning {
    // One full robot loop. Order matters — see task.md.
    fun periodic() {
        // TODO: see task.md.
        TODO()
    }

    // What the LED strip shows the driver right now (priority table in task.md).
    fun ledColor(): LedColor {
        // TODO: see task.md.
        TODO()
    }

    // Binder p.25: blue + rumble only when EVERYTHING is lined up.
    fun readyToScore(): Boolean {
        // TODO: see task.md.
        TODO()
    }

    fun reset() {
        MiniIntake.reset()
        MiniGripper.reset()
        MiniSuperstructure.reset()
        MiniDriveBase.reset()
    }
}
