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

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation("io.github.erkko68.filament:filament")
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
