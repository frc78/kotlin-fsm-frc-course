plugins {
    kotlin("jvm") version "2.0.21"
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
            "1-kotlin-for-fsms/1-hello-kotlin/src",
            "1-kotlin-for-fsms/2-enums-for-states/src",
            "1-kotlin-for-fsms/3-enums-with-properties/src",
            "1-kotlin-for-fsms/4-sealed-classes-alternative/src",
            // Lesson 2
            "2-building-an-fsm/1-two-method-pattern/src",
            "2-building-an-fsm/2-transitions-with-events/src",
            "2-building-an-fsm/3-entry-side-effects/src",
            "2-building-an-fsm/4-guards/src",
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
            "7-superstructure-coordination/5-capstone/src",
            // Lesson 8
            "8-field-awareness/1-translation-rotation/src",
            "8-field-awareness/2-pose2d/src",
            "8-field-awareness/3-targeting/src",
            "8-field-awareness/4-chassis-speeds/src",
            "8-field-awareness/5-pose-estimator/src",
            "8-field-awareness/6-vision-gating/src",
        )
    }
    test {
        kotlin.srcDirs(
            "1-kotlin-for-fsms/1-hello-kotlin/test",
            "1-kotlin-for-fsms/2-enums-for-states/test",
            "1-kotlin-for-fsms/3-enums-with-properties/test",
            "1-kotlin-for-fsms/4-sealed-classes-alternative/test",
            "2-building-an-fsm/1-two-method-pattern/test",
            "2-building-an-fsm/2-transitions-with-events/test",
            "2-building-an-fsm/3-entry-side-effects/test",
            "2-building-an-fsm/4-guards/test",
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
            "7-superstructure-coordination/5-capstone/test",
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
}
