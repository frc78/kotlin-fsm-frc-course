package course.l5t4

import frc.stubs.*

object Flywheel {
    internal val motor = TalonFX(canId = 60)

    // TODO: build a TalonFXConfiguration that sets:
    //   - Slot0.kV = 0.12
    //   - Slot0.kP = 0.25
    // and apply it via motor.configurator.apply(config).
    fun configure() {
        TODO()
    }

    // TODO: motor.setControl(VelocityVoltage(rps))
    fun runAtRps(rps: Double) {
        TODO()
    }
}
