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

compose.desktop {
    application {
        mainClass = "eric.bitria.samples.MainKt"

        // Silence "restricted method java.lang.System::load" warnings from Skiko and
        // from our own NativeLoader. JDK 22+ requires native access to be explicitly
        // granted; ALL-UNNAMED grants it to every classpath jar.
        // If consumers run on the module path they can narrow this to
        //   --enable-native-access=io.github.erkko68.filament.jni
        jvmArgs += "--enable-native-access=ALL-UNNAMED"

        nativeDistributions {
            targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Pkg, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb)
            packageName = "eric.bitria.samples"
            packageVersion = "1.0.0"
        }
    }
}
