plugins {
    id("filament-kmp-module")
}

// Web: filamat-kmp.wasm, built by :web; its exports are installed as globals like filament-kmp.wasm's.
val stageFilamatWasm = tasks.register<Sync>("stageFilamatWasm") {
    dependsOn(":web:stageFilamatWasm")
    from(rootProject.layout.projectDirectory.dir("web/build/filamatWasm"))
    into(layout.buildDirectory.dir("filamatWasm"))
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":kotlin:filament"))
        }
        webTest {
            resources.srcDir(stageFilamatWasm)
        }
    }
}
