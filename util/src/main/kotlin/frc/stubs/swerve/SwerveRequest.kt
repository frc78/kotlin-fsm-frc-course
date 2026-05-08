package frc.stubs.swerve

// Lightweight stand-ins for Phoenix6's CTRE swerve request types. Each has the
// same `with*`-method API surface students will see in real CTRE code, so
// muscle memory transfers.
//
// Implementation note: these stubs use immutable data classes — each `with*`
// call returns a COPY with the field updated. The real Phoenix6 classes
// mutate-in-place and return `this`. The chained call shape is identical
// either way:
//
//   FieldCentric()
//       .withVelocityX(1.0)
//       .withVelocityY(0.5)
//       .withRotationalRate(0.2)
//
// Velocities are in meters/second (vx, vy) and radians/second (rotationalRate).
// Directions are in degrees in this course; real Phoenix6 takes Rotation2d.

sealed class SwerveRequest

data class FieldCentric(
    val velocityX: Double = 0.0,
    val velocityY: Double = 0.0,
    val rotationalRate: Double = 0.0,
) : SwerveRequest() {
    fun withVelocityX(velocityX: Double) = copy(velocityX = velocityX)
    fun withVelocityY(velocityY: Double) = copy(velocityY = velocityY)
    fun withRotationalRate(rotationalRate: Double) = copy(rotationalRate = rotationalRate)
}

data class RobotCentric(
    val velocityX: Double = 0.0,
    val velocityY: Double = 0.0,
    val rotationalRate: Double = 0.0,
) : SwerveRequest() {
    fun withVelocityX(velocityX: Double) = copy(velocityX = velocityX)
    fun withVelocityY(velocityY: Double) = copy(velocityY = velocityY)
    fun withRotationalRate(rotationalRate: Double) = copy(rotationalRate = rotationalRate)
}

data object SwerveDriveBrake : SwerveRequest()

data class PointWheelsAt(
    val moduleDirection: Double = 0.0,
) : SwerveRequest() {
    fun withModuleDirection(moduleDirection: Double) = copy(moduleDirection = moduleDirection)
}

data class FieldCentricFacingAngle(
    val velocityX: Double = 0.0,
    val velocityY: Double = 0.0,
    val targetDirection: Double = 0.0,
) : SwerveRequest() {
    fun withVelocityX(velocityX: Double) = copy(velocityX = velocityX)
    fun withVelocityY(velocityY: Double) = copy(velocityY = velocityY)
    fun withTargetDirection(targetDirection: Double) = copy(targetDirection = targetDirection)
}

data object Idle : SwerveRequest()
