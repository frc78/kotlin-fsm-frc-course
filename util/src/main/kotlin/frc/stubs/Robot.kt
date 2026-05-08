package frc.stubs

class Robot(private vararg val subsystems: Subsystem) {
    fun tick(times: Int = 1) {
        repeat(times) {
            for (s in subsystems) s.periodic()
        }
    }
}
