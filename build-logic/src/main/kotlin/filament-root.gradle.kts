// Root-project tasks every module shares:
//   prebuilts_<id>     Filament's static libraries for one FilamentTarget, downloaded or built from source
//   prebuilts          every downloadable target + headers (source-built ones stay on demand: they're slow)
//   downloadIncludes   Filament's public headers (include/)
//   setupEmsdk         the Emscripten SDK (.emsdk/) for the wasm builds
//   generateBindings   JNI forwarders + wasm export tables from the common externals
//   cmakeBuild_<id>    the C API's static libraries for an iOS target (packed into the klibs)
//   apiGaps           the Filament API nothing binds yet (build/reports/api-gaps.txt)

import buildlogic.apicheck.registerApiGapTasks
import buildlogic.bindings.GenerateBindingsTask
import buildlogic.cmake.registerCApiBuild
import buildlogic.platform.FilamentTarget
import buildlogic.platform.hostPlatform
import buildlogic.prebuilts.DownloadIncludesTask
import buildlogic.prebuilts.DownloadPrebuiltsTask
import buildlogic.prebuilts.source.BuildFromSourceTask
import buildlogic.prebuilts.source.SetupEmsdkTask

val filaVersion = providers.gradleProperty("filaVersion")
val archiveCache = layout.projectDirectory.dir(".gradle/filament-prebuilts-cache")

val setupEmsdk = tasks.register<SetupEmsdkTask>("setupEmsdk") {
    group = "filament"
    description = "Installs the pinned Emscripten SDK into .emsdk/."
    emsdkVersion.set(providers.gradleProperty("emsdkVersion"))
    installDir.set(layout.projectDirectory.dir(".emsdk"))
    cacheDir.set(archiveCache)
}

FilamentTarget.entries.forEach { target ->
    val outDir = layout.projectDirectory.dir("prebuilts/${target.id}")
    if (target.fromSource) {
        tasks.register<BuildFromSourceTask>("prebuilts_${target.id}") {
            group = "filament"
            description = "Builds Filament's static libraries for ${target.id} from source (upstream ships none)."
            filamentVersion.set(filaVersion)
            this.target.set(target)
            outputDir.set(outDir)
            workDir.set(layout.projectDirectory.dir(".gradle/filament-src"))
            cacheDir.set(archiveCache)
            emsdkDir.set(layout.projectDirectory.dir(".emsdk"))
            if (target == FilamentTarget.WASM) dependsOn(setupEmsdk)
        }
    } else {
        tasks.register<DownloadPrebuiltsTask>("prebuilts_${target.id}") {
            group = "filament"
            description = "Downloads Filament's static libraries for ${target.id}."
            filamentVersion.set(filaVersion)
            this.target.set(target)
            outputDir.set(outDir.dir("lib"))
            cacheDir.set(archiveCache)
        }
    }
}

tasks.register<DownloadIncludesTask>("downloadIncludes") {
    group = "filament"
    description = "Downloads Filament's public headers into include/."
    filamentVersion.set(filaVersion)
    includeDir.set(layout.projectDirectory.dir("include"))
    cacheDir.set(archiveCache)
    stampFile.set(layout.projectDirectory.file("include/.filament-version"))
}

tasks.register("prebuilts") {
    group = "filament"
    description = "Downloads Filament's static libraries for every release target, plus its headers."
    dependsOn(FilamentTarget.entries.filterNot { it.fromSource }.map { "prebuilts_${it.id}" }, "downloadIncludes")
}

tasks.register<GenerateBindingsTask>("generateBindings") {
    group = "filament"
    description = "Generates the JNI forwarders and wasm export tables from the common externals."
    sources.from(fileTree("kotlin") { include("*/src/commonMain/**/*.kt") })
    headers.from(fileTree("c") { include("*/c/*.h") })
    wasmRuntimes.put("filamat", "filamat-kmp")
    outputDir.set(layout.buildDirectory.dir("generated/bindings"))
}

if (hostPlatform() == "macos") {
    FilamentTarget.ios.forEach { target ->
        registerCApiBuild("cmakeBuild_${target.id}", target) {
            targets.addAll("filament-c", "filamat-c", "filament-utils-c", "gltfio-c")
        }
    }
}

registerApiGapTasks()
