package frc.stubs.swerve

import frc.stubs.geometry.Rotation2d

// Lightweight stand-ins for Phoenix6's CTRE swerve request types. Each has the
// same `with*`-method API surface students will see in real CTRE code, so
// muscle memory transfers.
//
// Implementation note: these stubs use immutable data classes. Each `with*`
// call returns a COPY with the field updated. The real Phoenix6 classes
// mutate in place and return `this`. The chained call shape is identical
// either way.
//
// Velocities are in meters/second (vx, vy) and radians/second (rotationalRate).
// Directions are `Rotation2d`, as in real Phoenix6. Deadbands are stored but
// not applied here; the real drivetrain applies them.

sealed class SwerveRequest

data class FieldCentric(
    val velocityX: Double = 0.0,
    val velocityY: Double = 0.0,
    val rotationalRate: Double = 0.0,
    val deadband: Double = 0.0,
    val rotationalDeadband: Double = 0.0,
) : SwerveRequest() {
    fun withVelocityX(velocityX: Double) = copy(velocityX = velocityX)
    fun withVelocityY(velocityY: Double) = copy(velocityY = velocityY)
    fun withRotationalRate(rotationalRate: Double) = copy(rotationalRate = rotationalRate)
    fun withDeadband(metersPerSecond: Double) = copy(deadband = metersPerSecond)
    fun withRotationalDeadband(radiansPerSecond: Double) = copy(rotationalDeadband = radiansPerSecond)
}

data class RobotCentric(
    val velocityX: Double = 0.0,
    val velocityY: Double = 0.0,
    val rotationalRate: Double = 0.0,
    val deadband: Double = 0.0,
    val rotationalDeadband: Double = 0.0,
) : SwerveRequest() {
    fun withVelocityX(velocityX: Double) = copy(velocityX = velocityX)
    fun withVelocityY(velocityY: Double) = copy(velocityY = velocityY)
    fun withRotationalRate(rotationalRate: Double) = copy(rotationalRate = rotationalRate)
    fun withDeadband(metersPerSecond: Double) = copy(deadband = metersPerSecond)
    fun withRotationalDeadband(radiansPerSecond: Double) = copy(rotationalDeadband = radiansPerSecond)
}

data object SwerveDriveBrake : SwerveRequest()

data class PointWheelsAt(
    val moduleDirection: Rotation2d = Rotation2d(),
) : SwerveRequest() {
    fun withModuleDirection(direction: Rotation2d) = copy(moduleDirection = direction)
}

data class FieldCentricFacingAngle(
    val velocityX: Double = 0.0,
    val velocityY: Double = 0.0,
    val targetDirection: Rotation2d = Rotation2d(),
) : SwerveRequest() {
    fun withVelocityX(velocityX: Double) = copy(velocityX = velocityX)
    fun withVelocityY(velocityY: Double) = copy(velocityY = velocityY)
    fun withTargetDirection(direction: Rotation2d) = copy(targetDirection = direction)
}

data object Idle : SwerveRequest()
