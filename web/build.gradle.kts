plugins {
    kotlin("multiplatform")
    id("filament-publish")
}

version = findProperty("libVersion") as? String ?: "0.1.0-SNAPSHOT"

val filaVersion = project.property("filaVersion") as String

// ──────────────────────────────────────────────────────────────────────────────
// Karakum-generated Kotlin/JS externals
//
// Filament.js ships a TypeScript definition (`filament.d.ts`) that lags the real
// embind surface registered in upstream `web/filament-js/jsbindings.cpp` (the
// source of truth). We therefore:
//   1. download the upstream d.ts alongside filament.js/.wasm (downloadPrebuilts_web),
//   2. concatenate it with a curated overlay (patches/filament.patch.d.ts) that
//      adds the under-reported declarations, into a local `filament` npm package,
//   3. run Karakum over the patched d.ts to (re)generate the external declarations.
//
// Nothing generated is committed — the externals are produced on every build and
// land in build/generated/karakum, wired below as a jsMain source directory.
// See scripts/dev/check-js-bindings.sh to audit the overlay against jsbindings.cpp.
// ──────────────────────────────────────────────────────────────────────────────

// Resolve the Node.js toolchain (node/npm/npx). An explicit NODE/NPM/NPX env var wins
// (mirrors the PYTHON pattern used by the root prebuilt-download tasks); otherwise we
// search PATH plus the usual install locations. IDE-launched Gradle daemons (IntelliJ
// on macOS) inherit a stripped-down PATH that typically omits Homebrew/nvm, so a bare
// `npm` fails to even start — the fallback search makes `./gradlew` and the IDE both
// work with no env setup.
fun findNodeTool(name: String, override: String?): String {
    if (override != null) return override
    val pathDirs = (System.getenv("PATH") ?: "").split(File.pathSeparator)
    val commonDirs = listOf("/opt/homebrew/bin", "/usr/local/bin", "/usr/bin")
    val nvmDirs = System.getenv("HOME")?.let { home ->
        File("$home/.nvm/versions/node").listFiles()?.sortedDescending()?.map { "${it.path}/bin" }
    }.orEmpty()
    return (pathDirs + commonDirs + nvmDirs)
        .map { File(it, name) }
        .firstOrNull { it.canExecute() }
        ?.absolutePath
        ?: name
}

val npmExe = findNodeTool("npm", providers.environmentVariable("NPM").orNull)
val npxExe = findNodeTool("npx", providers.environmentVariable("NPX").orNull)
val nodeExe = findNodeTool("node", providers.environmentVariable("NODE").orNull)

// npm/npx are node scripts that re-exec `node` off PATH; prepend node's own directory so
// they resolve it even when the daemon's PATH doesn't include the Node install (the IDE case).
val nodeBinDir = File(nodeExe).parentFile?.absolutePath
fun Exec.withNodeOnPath() {
    if (nodeBinDir != null) {
        environment("PATH", nodeBinDir + File.pathSeparator + (System.getenv("PATH") ?: ""))
    }
}

val upstreamDts = rootProject.layout.projectDirectory.file("prebuilts/web/filament.d.ts")
val overlayDts = layout.projectDirectory.file("patches/filament.patch.d.ts")
val overridesJson = layout.projectDirectory.file("patches/filament.dts-overrides.json")
val karakumConfig = layout.projectDirectory.file("karakum.config.json")
val patchScript = layout.projectDirectory.file("scripts/patch-externals.mjs")

// Local npm package that Karakum resolves `libraryName: "filament"` to. Karakum's
// `input` glob is relative to this package's directory.
val dtsPackageDir = layout.projectDirectory.dir("filament-types")
val nodeModulesMarker = layout.projectDirectory.file("node_modules/.package-lock.json")
val generatedDir = layout.buildDirectory.dir("generated/karakum")

// 1. Build the patched d.ts into the local `filament` package.
val assembleFilamentDts = tasks.register("assembleFilamentDts") {
    description = "Concatenates the upstream filament.d.ts with the curated overlay into the local Karakum input package."
    group = "karakum"
    dependsOn(rootProject.tasks.named("downloadPrebuilts_web"))

    val upstream = upstreamDts
    val overlay = overlayDts
    val overrides = overridesJson
    val outDir = dtsPackageDir
    val version = filaVersion

    inputs.file(upstream)
    inputs.file(overlay)
    inputs.file(overrides)
    inputs.property("filaVersion", version)
    outputs.dir(outDir)

    doLast {
        val dir = outDir.asFile
        dir.mkdirs()
        dir.resolve("package.json").writeText(
            """{"name":"filament","version":"$version","types":"filament.d.ts"}""" + "\n"
        )
        var merged = buildString {
            append(upstream.asFile.readText())
            append("\n\n")
            append(overlay.asFile.readText())
        }

        // Non-additive patches that TS declaration merging can't express live in
        // patches/filament.dts-overrides.json (the curated *data*); this is just the
        // generic apply mechanism. See that file for the rationale of each entry.
        @Suppress("UNCHECKED_CAST")
        val data = groovy.json.JsonSlurper().parse(overrides.asFile) as Map<String, Any?>

        // Literal field/type corrections (the d.ts types a member wrongly).
        (data["replacements"] as? List<Map<String, String>>).orEmpty().forEach { r ->
            val find = r.getValue("find")
            require(merged.contains(find)) { "d.ts override target not found: \"$find\"" }
            merged = merged.replace(find, r.getValue("replace"))
        }

        // Static (class_function) members spliced into the class body (Karakum can't add
        // statics by merging). Match `export class <cls> {` (whitespace-then-brace, so
        // `Texture` doesn't catch `Texture$Builder`); insert after the opening brace.
        (data["staticMembers"] as? Map<String, List<String>>).orEmpty().forEach { (cls, members) ->
            val re = Regex("export class $cls\\s*\\{")
            require(re.containsMatchIn(merged)) { "static injection target not found: class $cls" }
            merged = re.replace(merged) { m -> m.value + "\n    " + members.joinToString("\n    ") }
        }

        // Filament's d.ts uses `$` as a flat name separator (e.g. `Texture$Builder`,
        // `gltfio$FilamentInstance`). `$` is illegal in Kotlin identifiers, so map it
        // to `_` — both legal and matching this repo's historical external naming
        // (`Texture_InternalFormat`). `$` only ever appears in identifiers here (the
        // two stray mentions are in comments), so a blanket replace is safe.
        dir.resolve("filament.d.ts").writeText(merged.replace("$", "_"))
    }
}

// 2. Install the Karakum toolchain (and link the local `filament` package).
val installKarakum = tasks.register<Exec>("installKarakum") {
    description = "Installs the Karakum/TypeScript toolchain via npm."
    group = "karakum"
    dependsOn(assembleFilamentDts)

    workingDir = layout.projectDirectory.asFile
    withNodeOnPath()
    commandLine(npmExe, "install", "--no-audit", "--no-fund", "--loglevel=error")

    inputs.file(layout.projectDirectory.file("package.json"))
    inputs.dir(dtsPackageDir)
    outputs.file(nodeModulesMarker)
}

// 3. Generate the raw Kotlin externals from the patched d.ts.
val generateJsExternals = tasks.register<Exec>("generateJsExternals") {
    description = "Runs Karakum to (re)generate the Kotlin/JS external declarations for Filament."
    group = "karakum"
    dependsOn(installKarakum)

    workingDir = layout.projectDirectory.asFile
    withNodeOnPath()
    commandLine(npxExe, "karakum", "--config", karakumConfig.asFile.absolutePath)

    inputs.file(karakumConfig)
    inputs.dir(dtsPackageDir)
    inputs.file(nodeModulesMarker)
    outputs.dir(generatedDir)
}

// 4. Make Karakum's output compile for BOTH `js` and `wasmJs` (a shared `webMain`).
// Karakum's raw output already uses multiplatform kotlin-wrappers types (js.*, web.*,
// Double), which wasmJs accepts as-is; the only wasmJs-illegal patterns are its enum
// value-holders (nested object in an external interface) and bare `Any` at the interop
// boundary. `patch-externals.mjs` rewrites exactly those, in place. See that script for
// the rationale of each transform.
val patchJsExternals = tasks.register<Exec>("patchJsExternals") {
    description = "Rewrites the generated externals to compile on both js and wasmJs."
    group = "karakum"
    dependsOn(generateJsExternals)

    workingDir = layout.projectDirectory.asFile
    withNodeOnPath()
    commandLine(nodeExe, patchScript.asFile.absolutePath, generatedDir.get().asFile.absolutePath)

    inputs.file(patchScript)
    inputs.dir(generatedDir)
    outputs.dir(generatedDir)
}

kotlin {
    js {
        browser()
    }
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    // `js` + `wasmJs` + applyDefaultHierarchyTemplate() produces a shared `webMain`.
    // The Karakum externals (patched to compile on both targets by patch-externals.mjs)
    // and the kotlin-wrappers types live there so a single set feeds both targets.
    applyDefaultHierarchyTemplate()

    sourceSets {
        webMain {
            kotlin.srcDir(patchJsExternals)
            dependencies {
                // `api` so consuming modules (kotlin/*/webMain) can resolve the wrapper
                // types that appear in the generated external signatures (web.html.*,
                // js.array.*, js.core.*, …). kotlin-wrappers publishes these as
                // multiplatform (js + wasmJs) artifacts. Karakum emits raw wrapper types;
                // patch-externals.mjs leaves them intact (only enum/`Any` are rewritten).
                api(project.dependencies.platform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:2026.6.7"))
                api("org.jetbrains.kotlin-wrappers:kotlin-js")
                api("org.jetbrains.kotlin-wrappers:kotlin-web")
                api("org.jetbrains.kotlin-wrappers:kotlin-browser")
                // Supplies the stdlib-shaped org.w3c.dom / org.khronos.webgl / kotlinx.browser
                // packages for BOTH js and wasmJs, so the DOM / typed-array externals (and the
                // actuals that use them) resolve on wasmJs. patch-externals.mjs remaps those
                // types back from kotlin-wrappers to these packages.
                api("org.jetbrains.kotlinx:kotlinx-browser:0.5.0")
            }
        }
        jsMain {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
    }
}
