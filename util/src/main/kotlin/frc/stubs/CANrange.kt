package frc.stubs

class CANrange(val canId: Int) {
    private var distanceMeters = Double.POSITIVE_INFINITY
    private var detected = false

    fun getDistance(): Double = distanceMeters
    fun getIsDetected(): Boolean = detected

    fun simulateDistance(meters: Double) {
        distanceMeters = meters
        detected = meters.isFinite() && meters < 0.10
    }

    fun simulateDetected(value: Boolean) {
        detected = value
        if (value && !distanceMeters.isFinite()) distanceMeters = 0.05
    }
}
