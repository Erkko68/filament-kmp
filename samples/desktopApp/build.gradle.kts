plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvm{}

    sourceSets {
        jvmMain.dependencies {
            implementation(project(":shared"))
            implementation(compose.desktop.currentOs)
        }
    }
}

// The Gradle daemon runs on JDK 25 (gradle/gradle-daemon-jvm.properties), so the compose `run`
// task and dependency resolution already use 25 — no toolchain or javaHome override needed for the
// filament FFM bindings (JVM 22 bytecode, java.lang.foreign).
compose.desktop {
    application {
        mainClass = "eric.bitria.samples.MainKt"

        // Silence "restricted method java.lang.System::load" warnings from Skiko and from our own
        // FFM loader. JDK 22+ requires native access to be explicitly granted; ALL-UNNAMED grants
        // it to every classpath jar. On the module path this can be narrowed to
        //   --enable-native-access=io.github.erkko68.filament.ffm
        jvmArgs += "--enable-native-access=ALL-UNNAMED"

        nativeDistributions {
            targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Pkg, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb)
            packageName = "eric.bitria.samples"
            packageVersion = "1.0.0"
        }
    }
}
