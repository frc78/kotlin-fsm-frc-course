package course.l5t3

import frc.stubs.*

object IntakeMotor {
    internal val motor = TalonFX(canId = 22)

    // TODO: build a TalonFXConfiguration that:
    //   - sets MotorOutput.NeutralMode = NeutralModeValue.Coast
    //   - sets CurrentLimits.SupplyCurrentLimit = 40.0
    //   - enables CurrentLimits.SupplyCurrentLimitEnable = true
    // and apply it via motor.configurator.apply(config).
    fun configure() {
        TODO()
    }
}
