plugins {
    kotlin("multiplatform")
    id("filament-publish")
}

version = findProperty("libVersion") as? String ?: "0.1.0-SNAPSHOT"

val filaVersion: String by project

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

// `npm`/`npx` are taken from PATH; override via env (mirrors the PYTHON pattern
// used by the root prebuilt-download tasks).
val npmExe = providers.environmentVariable("NPM").orElse("npm")
val npxExe = providers.environmentVariable("NPX").orElse("npx")

val upstreamDts = rootProject.layout.projectDirectory.file("prebuilts/web/filament.d.ts")
val overlayDts = layout.projectDirectory.file("patches/filament.patch.d.ts")
val overridesJson = layout.projectDirectory.file("patches/filament.dts-overrides.json")
val karakumConfig = layout.projectDirectory.file("karakum.config.json")

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
    commandLine(npmExe.get(), "install", "--no-audit", "--no-fund", "--loglevel=error")

    inputs.file(layout.projectDirectory.file("package.json"))
    inputs.dir(dtsPackageDir)
    outputs.file(nodeModulesMarker)
}

// 3. Generate the Kotlin externals.
val generateJsExternals = tasks.register<Exec>("generateJsExternals") {
    description = "Runs Karakum to (re)generate the Kotlin/JS external declarations for Filament."
    group = "karakum"
    dependsOn(installKarakum)

    workingDir = layout.projectDirectory.asFile
    commandLine(npxExe.get(), "karakum", "--config", karakumConfig.asFile.absolutePath)

    inputs.file(karakumConfig)
    inputs.dir(dtsPackageDir)
    inputs.file(nodeModulesMarker)
    outputs.dir(generatedDir)

    // Post-generation normalisations applied to Karakum's output:
    //
    //  • Typed arrays: Karakum (alpha.107) emits `js.typedarrays.*`, but every current
    //    kotlin-wrappers release makes those generic over the backing buffer
    //    (`Float32Array<B : ArrayBufferLike>`) without Karakum parameterising them.
    //    Remap to the non-generic stdlib `org.khronos.webgl.*` equivalents (also what
    //    the jsMain actuals already use).
    //
    //  • Numbers: Karakum maps JS `number` to the concrete `Double`. JS numbers are
    //    untyped, and the jsMain actuals pass `Int`/`Float` freely, so map to the
    //    abstract `kotlin.Number` (the type the hand-written externals used). Every
    //    `Double` in the externals originates from a `number`, so the replace is total.
    //  • Canvas: Karakum types the one DOM touchpoint (Engine.create's canvas) as the
    //    kotlin-wrappers `web.html.HTMLCanvasElement`, but the rest of this binding and
    //    its Compose HTML interop (WebElementView) speak stdlib `org.w3c.dom`. Remap so
    //    the surfaces line up without per-call conversions.
    val outRoot = generatedDir
    val doubleWord = Regex("\\bDouble\\b")
    doLast {
        outRoot.get().asFile.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .forEach { f ->
                val patched = f.readText()
                    .replace("js.typedarrays.", "org.khronos.webgl.")
                    .replace("web.html.HTMLCanvasElement", "org.w3c.dom.HTMLCanvasElement")
                    // Karakum maps TS `T[]` to the covariant `js.array.ReadonlyArray<T>`
                    // (= `Array<out T>`); the actuals are written against the invariant
                    // stdlib `Array<T>` (what the old externals used). Remap so call sites
                    // and the IntArray/registerAndGetIds helpers line up.
                    .replace("js.array.ReadonlyArray", "Array")
                    .replace(doubleWord, "Number")
                if (patched != f.readText()) f.writeText(patched)
            }
    }
}

kotlin {
    js {
        browser()
    }

    sourceSets {
        val jsMain by getting {
            kotlin.srcDir(generateJsExternals)
            dependencies {
                implementation(kotlin("stdlib-js"))
                // `api` so consuming modules (kotlin/*/jsMain) can resolve the wrapper
                // types that appear in the generated external signatures (web.html.*,
                // js.array.*, …). Pinned to the version Karakum 1.0.0-alpha.107
                // generates against (see karakum-team/karakum gradle.properties); newer
                // wrappers made the TypedArrays generic (Float32Array<B>), which
                // Karakum's output doesn't parameterise.
                api(project.dependencies.platform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:2026.4.7"))
                api("org.jetbrains.kotlin-wrappers:kotlin-js")
                api("org.jetbrains.kotlin-wrappers:kotlin-web")
                api("org.jetbrains.kotlin-wrappers:kotlin-browser")
            }
        }
    }
}
