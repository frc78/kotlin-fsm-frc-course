package frc.stubs.geometry

import kotlin.math.PI

data class Rotation2d(val radians: Double = 0.0) {
    val degrees: Double get() = radians * 180.0 / PI

    operator fun plus(other: Rotation2d) = Rotation2d(radians + other.radians)
    operator fun minus(other: Rotation2d) = Rotation2d(radians - other.radians)
    operator fun unaryMinus() = Rotation2d(-radians)

    companion object {
        fun fromDegrees(degrees: Double) = Rotation2d(degrees * PI / 180.0)
        fun fromRadians(radians: Double) = Rotation2d(radians)
    }
}
