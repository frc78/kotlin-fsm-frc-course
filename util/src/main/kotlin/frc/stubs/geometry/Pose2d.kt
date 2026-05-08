package frc.stubs.geometry

data class Pose2d(
    val translation: Translation2d = Translation2d(),
    val rotation: Rotation2d = Rotation2d(),
) {
    constructor(x: Double, y: Double, rotation: Rotation2d) :
        this(Translation2d(x, y), rotation)

    val x: Double get() = translation.x
    val y: Double get() = translation.y

    fun relativeTo(other: Pose2d): Pose2d {
        val translationDelta = (translation - other.translation).rotateBy(-other.rotation)
        return Pose2d(translationDelta, rotation - other.rotation)
    }
}
