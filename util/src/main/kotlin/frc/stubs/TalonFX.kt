package frc.stubs

sealed interface ControlRequest

data class VoltageOut(val volts: Double) : ControlRequest
data class DutyCycleOut(val output: Double) : ControlRequest
data class PositionVoltage(val rotations: Double) : ControlRequest
data class VelocityVoltage(val rotationsPerSecond: Double) : ControlRequest
data object NeutralOut : ControlRequest

enum class NeutralModeValue { Coast, Brake }
enum class InvertedValue { CounterClockwise_Positive, Clockwise_Positive }

class MotorOutputConfigs {
    var NeutralMode: NeutralModeValue = NeutralModeValue.Coast
    var Inverted: InvertedValue = InvertedValue.CounterClockwise_Positive
}

class CurrentLimitsConfigs {
    var SupplyCurrentLimit: Double = 0.0
    var SupplyCurrentLimitEnable: Boolean = false
    var StatorCurrentLimit: Double = 0.0
    var StatorCurrentLimitEnable: Boolean = false
}

class Slot0Configs {
    var kP: Double = 0.0
    var kI: Double = 0.0
    var kD: Double = 0.0
    var kS: Double = 0.0
    var kV: Double = 0.0
    var kA: Double = 0.0
    var kG: Double = 0.0
}

class TalonFXConfiguration {
    val MotorOutput: MotorOutputConfigs = MotorOutputConfigs()
    val CurrentLimits: CurrentLimitsConfigs = CurrentLimitsConfigs()
    val Slot0: Slot0Configs = Slot0Configs()
}

class TalonFXConfigurator {
    var appliedConfig: TalonFXConfiguration? = null
        private set

    fun apply(config: TalonFXConfiguration) {
        appliedConfig = config
    }
}

class TalonFX(val canId: Int) {
    val configurator: TalonFXConfigurator = TalonFXConfigurator()

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
