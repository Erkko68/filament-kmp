plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    js {
        browser {
            binaries.executable()
            commonWebpackConfig {
                outputFileName = "webApp.js"
            }
        }
    }

    val stageFilamentWebAssets = tasks.register<Sync>("stageFilamentWebAssets") {
        dependsOn(gradle.includedBuild("filament-umbrella").task(":downloadPrebuilts_web"))
        from(project.file("../../prebuilts/web")) {
            include("filament.js", "filament.wasm")
        }
        into(layout.buildDirectory.dir("filamentWebAssets"))
    }

    sourceSets {
        val jsMain by getting {
            resources.srcDir(stageFilamentWebAssets)
            dependencies {
                implementation(project(":shared"))
                implementation(libs.filament)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.components.resources)
            }
        }
    }
}
