plugins {
    id("filament-kmp-module")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":kotlin:filament"))
        }
    }
}
