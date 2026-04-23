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
                implementation(compose.html.core)
                implementation(compose.runtime)
            }
        }
    }
}
