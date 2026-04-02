pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()

        flatDir {
            dirs(file("filament-prebuilts/android/lib"))
        }
    }
}

rootProject.name = "filament-kmp"

include(":filament-kmp-api")

project(":filament-kmp-api").projectDir = file("filament-kmp-api")