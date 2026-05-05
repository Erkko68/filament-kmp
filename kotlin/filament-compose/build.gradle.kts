plugins {
    id("filament-kmp-module")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

filamentModule {
    xcframeworkName.set("FilamentCompose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":kotlin:filament"))
            api(project(":kotlin:filament-utils"))
            api(project(":kotlin:gltfio"))
            api(project(":kotlin:filamat"))
            api(libs.compose.runtime)
            api(libs.compose.foundation)
            api(libs.compose.ui)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}
