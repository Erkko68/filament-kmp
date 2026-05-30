# `:java` — JVM/Desktop native runtime (Project Panama / FFM)

This is the single module that binds Filament on the **JVM/Desktop** target. It uses
**Project Panama** (the Foreign Function & Memory API, finalised in JDK 22) to call the
combined C wrapper directly — no JNI. Android does **not** use this module; it depends on
the official `com.google.android.filament` Maven library instead.

Published as **`io.github.erkko68.filament-ffm:filament-ffm`** and pulled in transitively
by every `:kotlin:*` JVM target (each declares `api(project(":java"))` in its `jvmMain`),
so consumers never add it by hand.

## What it does (`build.gradle.kts` + [`buildSrc/FilamentJvmNative.kt`](../buildSrc/src/main/kotlin/FilamentJvmNative.kt))

1. **CMake** builds the combined `libfilament-c.{dylib,so,dll}` from
   [`c/CMakeLists.txt`](../c/CMakeLists.txt) (`-DFILAMENT_BUILD_SHARED=ON`). All four C
   wrappers — filament + filamat + filament-utils + gltfio — and their Filament static
   archives are linked into **one** shared image. One image means one set of Filament's
   process-global singletons (notably `EntityManager`); splitting them across multiple
   shared libraries duplicates those singletons and silently corrupts cross-library entities.
2. **`jextract`** runs once over the whole C header surface to generate a single
   `io.github.erkko68.filament.ffm.FilamentC` class of low-level `MethodHandle` bindings.
   The jextract task is wired as a source dir, so every consumer (`compileJava`,
   `compileKotlin`, `sourcesJar`) depends on it.
3. The dylib is packaged into the JAR under `natives/<platform>-<arch>/`. At runtime
   [`FilamentLoader`](src/main/java/io/github/erkko68/filament/ffm/FilamentLoader.java)
   extracts it to a temp file and `System.load`s it so jextract's `loaderLookup` resolves
   the symbols. No system install of Filament is needed.
4. [`Ffm.kt`](src/main/kotlin/io/github/erkko68/filament/Ffm.kt) hosts the shared FFM
   helpers (arenas, struct/array marshalling, upcall stubs) that the `:kotlin:*` `jvmMain`
   actuals build their idiomatic Kotlin API on top of `FilamentC`.

`jextract` is a one-time setup install — see [`scripts/gradle/download_jextract.py`](../scripts/gradle/download_jextract.py).

## Requirements

- **JDK 22+** at runtime (the FFM API floor). Compilation targets `--release 22`; the
  Gradle daemon itself runs on a newer JDK.

## Publishing

CI's [`publish.yml`](../.github/workflows/publish.yml) builds `libfilament-c` on each
platform runner, then publishes this module with `-PcArtifactsDir=<dir>` (one
`<platform>-<arch>/` subdir per platform) so the released JAR bundles natives for every
supported platform. The artifact id is pinned to `filament-ffm` in `build.gradle.kts`
(the project directory is `java/`, but the published id is not).
