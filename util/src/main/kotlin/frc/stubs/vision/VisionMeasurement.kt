package frc.stubs.vision

import frc.stubs.geometry.Pose2d

data class VisionMeasurement(
    val pose: Pose2d,
    val timestampSeconds: Double,
    val translationStdDev: Double = 0.5,
    val rotationStdDev: Double = 0.5,
)
