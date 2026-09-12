package course.l1t1

// Your first program. Run main() and read the output.
// Then change the text inside the quotes and run it again.

fun main() {
    val batteryVolts = 12.6
    println("Battery: $batteryVolts V")

    // A robot program runs its loop about 50 times a second.
    // repeat(3) runs the block three times, with tick = 0, 1, 2.
    repeat(3) { tick ->
        println("tick $tick")
    }
}
