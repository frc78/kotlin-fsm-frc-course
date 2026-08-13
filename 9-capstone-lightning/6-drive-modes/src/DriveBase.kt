package course.l9t6

import frc.stubs.Subsystem
import frc.stubs.geometry.Pose2d
import frc.stubs.geometry.Rotation2d
import frc.stubs.swerve.*

object DriveBase : Subsystem {
    enum class State { FIELD_DRIVE, ROBOT_DRIVE, ASSISTED_DRIVE, AUTO_ALIGN }

    internal val drivetrain = SwerveDrivetrain()

    // TODO: declare your constants here (see "Your task" in task.md).

    var state: State = State.FIELD_DRIVE
        private set

    // Driver sticks (m/s, rad/s).
    var stickVx: Double = 0.0
    var stickVy: Double = 0.0
    var stickOmega: Double = 0.0

    // Buttons.
    var commandedRobotDrive: Boolean = false    // R3 (right stick click)
    var commandedFieldDrive: Boolean = false    // L3 (left stick click)
    var commandedIntakeAssist: Boolean = false  // held while the intake is deployed
    var commandedAutoAlign: Boolean = false     // held while any level button (L1-L4) is pressed

    // Vision and pose inputs (set by vision/odometry code; tests set them directly).
    var tagVisible: Boolean = false
    var coralOffsetDegrees: Double = 0.0
    var robotPose: Pose2d = Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0))
    var targetPole: Pose2d = Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0))

    override fun periodic() {
        stateTransitions()
        stateActions()
    }

    private fun stateTransitions() {
        // TODO: see task.md.
        TODO()
    }

    private fun stateActions() {
        // TODO: see task.md.
        TODO()
    }

    fun reset() {
        state = State.FIELD_DRIVE
        stickVx = 0.0
        stickVy = 0.0
        stickOmega = 0.0
        commandedRobotDrive = false
        commandedFieldDrive = false
        commandedIntakeAssist = false
        commandedAutoAlign = false
        tagVisible = false
        coralOffsetDegrees = 0.0
        robotPose = Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0))
        targetPole = Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0))
        drivetrain.setControl(Idle)
    }
}
