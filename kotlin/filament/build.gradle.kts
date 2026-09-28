import buildlogic.resources.registerEmbeddedResources

plugins {
    id("filament-kmp-module")
}

kotlin {
    sourceSets {
        getByName("jniMain").dependencies {
            api(project(":jni"))
        }
        androidMain.dependencies {
            api(project(":android"))
        }
        jvmMain.dependencies {
            api(project(":desktop"))
        }
        webMain.dependencies {
            api(project(":web"))
        }
    }

    // Test .filamat materials (compiled with `matc -p all -a all` from the .mat sources alongside).
    val generateEmbeddedMaterials = registerEmbeddedResources(
        taskName = "generateEmbeddedMaterials",
        inputDir = "src/commonTest/materials",
        fileExtension = ".filamat",
        packageName = "io.github.erkko68.filament.testutils",
        objectName = "EmbeddedMaterials",
    )
    sourceSets.named("commonTest") { kotlin.srcDir(generateEmbeddedMaterials) }
}
