package course.l5t7

import frc.stubs.*

/**
 * Runnable recap of Lesson 5: one TalonFXConfiguration carrying all three
 * config blocks, applied in a single call.
 *
 * Run main(), read the printout, then change a value (disable the supply
 * limit, raise kV) and run it again.
 */
fun main() {
    val motor = TalonFX(canId = 30)

    val config = TalonFXConfiguration().apply {
        // Task 2: how the motor behaves at zero output, and which way is "forward".
        MotorOutput.NeutralMode = NeutralModeValue.Brake
        MotorOutput.Inverted = InvertedValue.Clockwise_Positive

        // Task 3: limit AND enable, explicitly, in the same configuration.
        CurrentLimits.SupplyCurrentLimit = 40.0
        CurrentLimits.SupplyCurrentLimitEnable = true
        CurrentLimits.StatorCurrentLimit = 80.0
        CurrentLimits.StatorCurrentLimitEnable = true

        // Task 4: closed-loop gains live in Slot0.
        Slot0.kV = 0.12
        Slot0.kP = 0.25
    }

    motor.configurator.apply(config) // ONE apply: the whole configuration at once

    println("TalonFX ${motor.canId} configuration:")
    println("  MotorOutput:   ${config.MotorOutput.NeutralMode}, ${config.MotorOutput.Inverted}")
    println(
        "  CurrentLimits: supply ${config.CurrentLimits.SupplyCurrentLimit} A " +
            "(enabled=${config.CurrentLimits.SupplyCurrentLimitEnable}), " +
            "stator ${config.CurrentLimits.StatorCurrentLimit} A " +
            "(enabled=${config.CurrentLimits.StatorCurrentLimitEnable})"
    )
    println("  Slot0:         kP=${config.Slot0.kP}, kV=${config.Slot0.kV}")

    // Requests work exactly as in tasks 1 and 4. Closed loop runs against Slot0:
    motor.setControl(VelocityVoltage(50.0))
    println("  Last request:  ${motor.lastRequest}")
}
