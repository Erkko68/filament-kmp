rootProject.name = "filament-umbrella"

// Kotlin modules
include(":kotlin:filament")
include(":kotlin:filamat")
include(":kotlin:filament-utils")
include(":kotlin:gltfio")


pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
