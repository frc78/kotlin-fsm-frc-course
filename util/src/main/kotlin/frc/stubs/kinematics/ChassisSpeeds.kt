package frc.stubs.kinematics

import frc.stubs.geometry.Rotation2d
import kotlin.math.cos
import kotlin.math.sin

data class ChassisSpeeds(
    val vxMetersPerSecond: Double = 0.0,
    val vyMetersPerSecond: Double = 0.0,
    val omegaRadiansPerSecond: Double = 0.0,
) {
    companion object {
        fun fromFieldRelativeSpeeds(
            vxField: Double,
            vyField: Double,
            omega: Double,
            robotAngle: Rotation2d,
        ): ChassisSpeeds {
            val c = cos(robotAngle.radians)
            val s = sin(robotAngle.radians)
            return ChassisSpeeds(
                vxMetersPerSecond = vxField * c + vyField * s,
                vyMetersPerSecond = -vxField * s + vyField * c,
                omegaRadiansPerSecond = omega,
            )
        }
    }
}
