# Your First Program

Welcome. This course teaches you to program FRC robot subsystems in Kotlin.
It starts from zero. You do not need to know any programming.

## What a program is

A program is a list of instructions. The computer does them in order, from
top to bottom. Before the program runs, a tool called the **compiler** reads
the whole list and checks it. If a line does not make sense, the compiler
stops and reports a **compiler error**. Nothing runs until every error is
fixed.

When the check passes, the program **runs**. In Kotlin, running starts at
a function named `main`.

## The robot loop

A robot program does not run once and stop. It runs one block of code, then
runs it again, about **50 times a second**, for the whole match. Each run of
that block is one **tick**. Almost all robot code you will write is a
function that the loop calls on every tick. This loop is why the code in
later lessons looks the way it does.

## Run it

1. Open `src/FirstProgram.kt`.
2. Click the green arrow next to `fun main()`.
3. Read the output panel at the bottom of the window.

You see one line with the battery voltage, then three `tick` lines.

- `println(...)` prints one line of text.
- `val batteryVolts = 12.6` stores a value under a name. The `$batteryVolts`
  inside the quotes puts that value into the text.
- `repeat(3) { ... }` runs the block three times. The name `tick` counts
  0, 1, 2.

## Change it

Change the text inside the quotes. Change `3` to `5`. Run it again after
each change. If you make a typo, the compiler underlines it in red and
tells you what is wrong. Read the message, fix the line, and run again.
This fix-and-run loop is how all programming works.

This task has no check. Click **Next** when you are ready.
