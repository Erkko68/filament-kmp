rootProject.name = "filament-umbrella"

// Kotlin modules
include(":kotlin:filament")
include(":kotlin:filamat")
include(":kotlin:filament-utils")
include(":kotlin:gltfio")

include(":kotlin:filament-compose")

// Java modules
include(":java:filament")
include(":java:filamat")
include(":java:gltfio")
include(":java:filament-utils")

// JS modules
include(":js")


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
