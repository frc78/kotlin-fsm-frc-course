package frc.stubs

sealed interface ControlRequest

data class VoltageOut(val volts: Double) : ControlRequest
data class DutyCycleOut(val output: Double) : ControlRequest
data class PositionVoltage(val rotations: Double) : ControlRequest
data class VelocityVoltage(val rotationsPerSecond: Double) : ControlRequest
data object NeutralOut : ControlRequest

class TalonFX(val canId: Int) {
    var lastRequest: ControlRequest = NeutralOut
        private set

    private var simulatedPosition = 0.0
    private var simulatedVelocity = 0.0

    fun setControl(request: ControlRequest) {
        lastRequest = request
        when (request) {
            is PositionVoltage -> simulatedPosition = request.rotations
            is VelocityVoltage -> simulatedVelocity = request.rotationsPerSecond
            NeutralOut -> simulatedVelocity = 0.0
            else -> { /* VoltageOut / DutyCycleOut don't auto-update sim state */ }
        }
    }

    fun stopMotor() = setControl(NeutralOut)

    fun getPosition(): Double = simulatedPosition
    fun getVelocity(): Double = simulatedVelocity
    fun setPosition(rotations: Double) { simulatedPosition = rotations }

    fun simulateVelocity(rps: Double) { simulatedVelocity = rps }
    fun simulatePosition(rotations: Double) { simulatedPosition = rotations }
}
