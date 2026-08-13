package course.l5t7

import frc.stubs.*

/**
 * Runnable recap of Lesson 5: one TalonFXConfiguration carrying all three
 * config blocks, applied in a single call.
 *
 * Run main(), read the printout, then tweak a value (disable the supply
 * limit, bump kV) and run it again.
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

    motor.configurator.apply(config) // ONE apply — the whole configuration at once

    val applied = motor.configurator.appliedConfig!!
    println("TalonFX ${motor.canId} configuration:")
    println("  MotorOutput:   ${applied.MotorOutput.NeutralMode}, ${applied.MotorOutput.Inverted}")
    println(
        "  CurrentLimits: supply ${applied.CurrentLimits.SupplyCurrentLimit} A " +
            "(enabled=${applied.CurrentLimits.SupplyCurrentLimitEnable}), " +
            "stator ${applied.CurrentLimits.StatorCurrentLimit} A " +
            "(enabled=${applied.CurrentLimits.StatorCurrentLimitEnable})"
    )
    println("  Slot0:         kP=${applied.Slot0.kP}, kV=${applied.Slot0.kV}")

    // Requests work exactly as in tasks 1 and 4 — closed loop against Slot0:
    motor.setControl(VelocityVoltage(rotationsPerSecond = 50.0))
    println("  Last request:  ${motor.lastRequest}")
}
