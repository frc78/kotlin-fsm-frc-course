package frc.stubs.swerve

class SwerveDrivetrain {
    var lastRequest: SwerveRequest = Idle
        private set

    fun setControl(request: SwerveRequest) {
        lastRequest = request
    }
}
