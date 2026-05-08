@file:Suppress("unused")

package frc.stubs

// Lightweight unit extension properties. Each returns a plain Double in a
// canonical unit (volts, rotations, rotations-per-second, degrees, meters).
// They exist for readability only — no compile-time unit checking.

inline val Double.volts: Double get() = this
inline val Int.volts: Double get() = this.toDouble()

inline val Double.rotations: Double get() = this
inline val Int.rotations: Double get() = this.toDouble()

inline val Double.degrees: Double get() = this
inline val Int.degrees: Double get() = this.toDouble()

inline val Double.rpm: Double get() = this / 60.0
inline val Int.rpm: Double get() = this.toDouble() / 60.0

inline val Double.rotationsPerSecond: Double get() = this
inline val Int.rotationsPerSecond: Double get() = this.toDouble()

inline val Double.meters: Double get() = this
inline val Int.meters: Double get() = this.toDouble()
