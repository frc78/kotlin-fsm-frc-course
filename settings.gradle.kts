plugins {
    // Auto-downloads the JDK 17 compile toolchain (see jvmToolchain in
    // build.gradle.kts) so the course builds no matter which JDK the student
    // has installed.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "kotlin-wpilib-fsm-course"
