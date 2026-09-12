plugins {
    kotlin("jvm") version "2.4.10"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(17)
}

// EduTools convention: each task is its own source root. This block wires the
// shared `util/` stubs plus every task's `src/` and `test/` into a single
// project so the course can be opened, compiled, and tested as one Gradle build.
sourceSets {
    main {
        kotlin.srcDirs(
            "util/src/main/kotlin",
            // Lesson 1
            "1-kotlin-for-fsms/1-your-first-program/src",
            "1-kotlin-for-fsms/2-values-and-types/src",
            "1-kotlin-for-fsms/3-functions/src",
            "1-kotlin-for-fsms/4-decisions/src",
            "1-kotlin-for-fsms/6-objects/src",
            "1-kotlin-for-fsms/7-loops-and-lists/src",
            "1-kotlin-for-fsms/9-enums-for-states/src",
            "1-kotlin-for-fsms/10-when-expression/src",
            "1-kotlin-for-fsms/11-enums-with-properties/src",
            "1-kotlin-for-fsms/12-sealed-classes-alternative/src",
            "1-kotlin-for-fsms/15-what-is-an-fsm/src",
            // Lesson 2
            "2-building-an-fsm/1-two-method-pattern/src",
            "2-building-an-fsm/2-transitions-with-events/src",
            "2-building-an-fsm/3-entry-side-effects/src",
            "2-building-an-fsm/4-guards/src",
            "2-building-an-fsm/5-timers-and-timeouts/src",
            // Lesson 3
            "3-applied-subsystems/1-intake-subsystem/src",
            "3-applied-subsystems/2-elevator-subsystem/src",
            "3-applied-subsystems/3-shooter-subsystem/src",
            // Lesson 4
            "4-testing/1-testing-pure-fsm-logic/src",
            "4-testing/2-fake-hardware/src",
            // Lesson 5
            "5-motor-configuration/1-creating-a-motor/src",
            "5-motor-configuration/2-motor-output-config/src",
            "5-motor-configuration/3-current-limits/src",
            "5-motor-configuration/4-pid-slot/src",
            "5-motor-configuration/7-real-phoenix6-notes/src",
            // Lesson 6
            "6-swerve-requests/1-field-centric/src",
            "6-swerve-requests/2-robot-centric/src",
            "6-swerve-requests/3-swerve-brake/src",
            "6-swerve-requests/4-point-wheels-at/src",
            "6-swerve-requests/5-facing-angle/src",
            "6-swerve-requests/6-drive-mode-fsm/src",
            // Lesson 7
            "7-superstructure-coordination/1-robot-state-enum/src",
            "7-superstructure-coordination/2-commanding-subsystems/src",
            "7-superstructure-coordination/3-at-target/src",
            "7-superstructure-coordination/4-sequencing-guards/src",
            "7-superstructure-coordination/5-bidirectional-sequencing/src",
            // Lesson 8
            "8-field-awareness/1-translation-rotation/src",
            "8-field-awareness/2-pose2d/src",
            "8-field-awareness/3-targeting/src",
            "8-field-awareness/4-chassis-speeds/src",
            "8-field-awareness/5-pose-estimator/src",
            "8-field-awareness/6-vision-gating/src",
            "8-field-awareness/9-fsm-architecture-at-2056/src",
        )
    }
    test {
        kotlin.srcDirs(
            "1-kotlin-for-fsms/2-values-and-types/test",
            "1-kotlin-for-fsms/3-functions/test",
            "1-kotlin-for-fsms/4-decisions/test",
            "1-kotlin-for-fsms/6-objects/test",
            "1-kotlin-for-fsms/7-loops-and-lists/test",
            "1-kotlin-for-fsms/9-enums-for-states/test",
            "1-kotlin-for-fsms/10-when-expression/test",
            "1-kotlin-for-fsms/11-enums-with-properties/test",
            "1-kotlin-for-fsms/12-sealed-classes-alternative/test",
            "2-building-an-fsm/1-two-method-pattern/test",
            "2-building-an-fsm/2-transitions-with-events/test",
            "2-building-an-fsm/3-entry-side-effects/test",
            "2-building-an-fsm/4-guards/test",
            "2-building-an-fsm/5-timers-and-timeouts/test",
            "3-applied-subsystems/1-intake-subsystem/test",
            "3-applied-subsystems/2-elevator-subsystem/test",
            "3-applied-subsystems/3-shooter-subsystem/test",
            "4-testing/1-testing-pure-fsm-logic/test",
            "4-testing/2-fake-hardware/test",
            "5-motor-configuration/1-creating-a-motor/test",
            "5-motor-configuration/2-motor-output-config/test",
            "5-motor-configuration/3-current-limits/test",
            "5-motor-configuration/4-pid-slot/test",
            "6-swerve-requests/1-field-centric/test",
            "6-swerve-requests/2-robot-centric/test",
            "6-swerve-requests/3-swerve-brake/test",
            "6-swerve-requests/4-point-wheels-at/test",
            "6-swerve-requests/5-facing-angle/test",
            "6-swerve-requests/6-drive-mode-fsm/test",
            "7-superstructure-coordination/1-robot-state-enum/test",
            "7-superstructure-coordination/2-commanding-subsystems/test",
            "7-superstructure-coordination/3-at-target/test",
            "7-superstructure-coordination/4-sequencing-guards/test",
            "7-superstructure-coordination/5-bidirectional-sequencing/test",
            "8-field-awareness/1-translation-rotation/test",
            "8-field-awareness/2-pose2d/test",
            "8-field-awareness/3-targeting/test",
            "8-field-awareness/4-chassis-speeds/test",
            "8-field-awareness/5-pose-estimator/test",
            "8-field-awareness/6-vision-gating/test",
        )
    }
}

tasks.test {
    useJUnitPlatform()

    // EduTools' "Check" button parses lines starting with `#educational_plugin`
    // out of test stdout to display per-test pass / fail in the UI. Without
    // this listener, a failing student gets a generic Gradle error instead of
    // the specific assertion message.
    outputs.upToDateWhen { false }

    addTestListener(object : TestListener {
        override fun beforeSuite(suite: TestDescriptor) {}
        override fun beforeTest(testDescriptor: TestDescriptor) {}
        override fun afterSuite(suite: TestDescriptor, result: TestResult) {}

        override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {
            if (result.resultType == TestResult.ResultType.FAILURE) {
                val message = result.exception?.message ?: "Wrong answer"
                val lines = message.split("\n")
                println("#educational_plugin FAILED + ${lines[0]}")
                lines.drop(1).forEach { line ->
                    println("#educational_plugin$line")
                }
                println()
            }
        }
    })
}
