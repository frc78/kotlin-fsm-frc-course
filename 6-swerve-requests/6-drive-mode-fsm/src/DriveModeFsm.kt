package course.l6t6

import frc.stubs.swerve.*

object DriveModeFsm {
    enum class State { TELEOP_FIELD, TELEOP_ROBOT, AIMING, BRAKED }

    val drivetrain = SwerveDrivetrain()

    var state: State = State.TELEOP_FIELD
        private set

    var requestedVx: Double = 0.0
    var requestedVy: Double = 0.0
    var requestedOmega: Double = 0.0
    var commandedRobotRelative: Boolean = false
    var commandedAim: Boolean = false
    var aimTargetDegrees: Double = 0.0
    var commandedBrake: Boolean = false

    fun periodic() {
        stateTransitions()
        stateActions()
    }

    private fun stateTransitions() {
        // TODO: priority order brake > aim > robot-relative > field.
        // Hint: a `when { ... }` with no subject lets you put boolean conditions
        // on each branch. The first true branch wins.
        TODO()
    }

    private fun stateActions() {
        // TODO: build the right SwerveRequest for the current state and apply it.
        //   TELEOP_FIELD -> FieldCentric             (with vx, vy, omega)
        //   TELEOP_ROBOT -> RobotCentric             (with vx, vy, omega)
        //   AIMING       -> FieldCentricFacingAngle  (with vx, vy, aimTargetDegrees)
        //   BRAKED       -> SwerveDriveBrake
        TODO()
    }

    fun reset() {
        state = State.TELEOP_FIELD
        requestedVx = 0.0
        requestedVy = 0.0
        requestedOmega = 0.0
        commandedRobotRelative = false
        commandedAim = false
        aimTargetDegrees = 0.0
        commandedBrake = false
        drivetrain.setControl(Idle)
    }
}
