package frc.stubs.geometry

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

data class Translation2d(val x: Double = 0.0, val y: Double = 0.0) {
    operator fun plus(other: Translation2d) = Translation2d(x + other.x, y + other.y)
    operator fun minus(other: Translation2d) = Translation2d(x - other.x, y - other.y)
    operator fun unaryMinus() = Translation2d(-x, -y)
    operator fun times(scalar: Double) = Translation2d(x * scalar, y * scalar)
    operator fun div(scalar: Double) = Translation2d(x / scalar, y / scalar)

    fun getNorm(): Double = hypot(x, y)
    fun getDistance(other: Translation2d): Double = (other - this).getNorm()
    fun getAngle(): Rotation2d = Rotation2d.fromRadians(atan2(y, x))
    fun rotateBy(rotation: Rotation2d): Translation2d {
        val c = cos(rotation.radians)
        val s = sin(rotation.radians)
        return Translation2d(x * c - y * s, x * s + y * c)
    }
}
