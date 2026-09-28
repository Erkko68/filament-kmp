import buildlogic.resources.registerEmbeddedResources

plugins {
    id("filament-kmp-module")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":kotlin:filament"))
        }
    }

    // ── Embed test .glb assets into a generated commonTest source ────────────────
    // The committed .glb files in src/commonTest/glb are the source of truth.
    val generateEmbeddedGlb = registerEmbeddedResources(
        taskName = "generateEmbeddedGlb",
        inputDir = "src/commonTest/glb",
        fileExtension = ".glb",
        packageName = "io.github.erkko68.filament.gltfio.testutils",
        objectName = "EmbeddedGlb",
    )
    sourceSets.named("commonTest") {
        kotlin.srcDir(generateEmbeddedGlb)
    }
}
