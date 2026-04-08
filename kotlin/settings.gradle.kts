rootProject.name = "filament-kmp-core"

include(":filament")
include(":filamat")
include(":filament-utils")

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
