rootProject.name = "Samples"
includeBuild("../") {
    dependencySubstitution {
        substitute(module("io.github.erkko68.filament:filament")).using(project(":kotlin:filament"))
        substitute(module("io.github.erkko68.filament:filamat")).using(project(":kotlin:filamat"))
        substitute(module("io.github.erkko68.filament:gltfio")).using(project(":kotlin:gltfio"))
        substitute(module("io.github.erkko68.filament:filament-utils")).using(project(":kotlin:filament-utils"))
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":shared")
include(":androidApp")
include(":desktopApp")
include(":webApp")
