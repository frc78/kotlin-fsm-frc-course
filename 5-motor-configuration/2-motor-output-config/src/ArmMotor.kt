package course.l5t2

import frc.stubs.*

object ArmMotor {
    internal val motor = TalonFX(canId = 25)

    // TODO: build a TalonFXConfiguration that sets:
    //   - MotorOutput.NeutralMode = NeutralModeValue.Brake
    //   - MotorOutput.Inverted    = InvertedValue.Clockwise_Positive
    // and apply it via motor.configurator.apply(config).
    fun configure() {
        TODO()
    }
}
