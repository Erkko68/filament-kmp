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
        // Filament modules are declared as compileOnly so consumers can pick which
        // ones they actually need (filament-compose itself only references the core
        // module API surface). Apps must add the modules they consume directly:
        //   implementation("io.github.erkko68.filament:filament")
        //   // optional, when used:
        //   implementation("io.github.erkko68.filament:filament-utils")
        //   implementation("io.github.erkko68.filament:gltfio")
        //   implementation("io.github.erkko68.filament:filamat")
        commonMain.dependencies {
            compileOnly(project(":kotlin:filament"))
            compileOnly(project(":kotlin:filament-utils"))
            compileOnly(project(":kotlin:gltfio"))
            compileOnly(project(":kotlin:filamat"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}
