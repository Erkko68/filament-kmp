plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvm {
        withJava()
    }
    
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.coroutines.swing)
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.components.resources)
                implementation(compose.desktop.currentOs)
            }
        }
        
        val jvmRuntimeClasspath by configurations.getting {
            resolutionStrategy {
                force("org.jetbrains.compose.runtime:runtime:1.7.1")
                force("org.jetbrains.compose.foundation:foundation:1.7.1")
                force("org.jetbrains.compose.ui:ui:1.7.1")
                force("org.jetbrains.compose.material3:material3:1.7.1")
                force("org.jetbrains.skiko:skiko-awt-runtime-macos-arm64:0.8.18")
                force("org.jetbrains.skiko:skiko-awt:0.8.18")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "eric.bitria.samples.MainKt"
        
        nativeDistributions {
            targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Pkg, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb)
            packageName = "eric.bitria.samples"
            packageVersion = "1.0.0"
        }
    }
}
