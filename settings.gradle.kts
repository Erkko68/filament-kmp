rootProject.name = "filament-umbrella"

// Kotlin modules
include(":kotlin:filament")
include(":kotlin:filamat")
include(":kotlin:filament-utils")

// Java modules
include(":java:filament")
include(":java:filamat")
include(":java:filament-utils")

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
