plugins {
    id("filament-kmp-module")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":kotlin:filament"))
        }
        // A material to sample the loaders' textures with.
        getByName("pixelTest").dependencies {
            implementation(project(":kotlin:filamat"))
        }
    }
}
