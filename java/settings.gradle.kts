rootProject.name = "filament-java"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include(":filament")
include(":filamat")
include(":gltfio")
include(":filament-utils")
