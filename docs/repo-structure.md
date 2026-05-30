# Repository Structure

The `filament-kmp` project is organized into several modules to handle the cross-platform nature of the Filament engine.

## Core Modules

- **`c/`**: C++ wrapper that exposes a C-compatible ABI around the official Filament C++ API. Consumed by two code generators: **Kotlin Native** targets (iOS, macOS) via `cinterop` (one CMake library per sub-module — `libfilament-c.a`, `libfilamat-c.a`, `libfilament-utils-c.a`, `libgltfio-c.a`), and the **JVM/Desktop** target via `jextract` (the `FILAMENT_BUILD_SHARED` path links all four into one `libfilament-c` shared image). Each module's C-ABI types live in its own `*Types.h` header (core types in `filament/c/FilaTypes.h`).

- **`java/`**: The single Project Panama (FFM) JVM binding module used by the **JVM/Desktop** target only. It drives the combined `libfilament-c` shared build via CMake, runs `jextract` over the C headers to generate the low-level bindings, bundles the native image as a JAR resource, and loads it at runtime. **Android does not use this folder** — it depends on the official `com.google.android.filament` Maven packages instead.

- **`js/`**: Kotlin/JS external declarations (a single `.kt` file with `external` annotations) that wrap the official Filament.js library compiled to WebAssembly. Used by the **Web** target.

- **`kotlin/`**: The core Kotlin Multiplatform wrapper. Contains five modules:
    - `filament` — Core engine components (Engine, Scene, View, Renderer, …).
    - `filamat` — Material compilation (MaterialBuilder).
    - `gltfio` — glTF asset loading (AssetLoader, FilamentAsset, Animator, …).
    - `filament-utils` — Math utilities, camera manipulators, HDR/KTX loaders.
    - `filament-compose` — Compose Multiplatform UI integration layer (see [Compose docs](compose/README.md)).

- **`prebuilts/`**: Prebuilt Filament static libraries for platforms that do not have an official Maven package: `iosArm64`, `iosSimulatorArm64`, `iosX64`, `macosArm64`, and WASM/JS. Downloaded via `scripts/gradle/download_filament_prebuilts.py` (invoked by the `downloadPrebuilts` Gradle task). The matching public headers land in `include/` via `scripts/gradle/download_filament_includes.py`.

- **`samples/`**: Multiplatform example applications for Android, iOS, Desktop (JVM), and Web.

## Binding Strategy by Platform

| Platform | Filament binding | Source |
| :--- | :--- | :--- |
| **Android** | Official Maven library | `com.google.android.filament:filament-android` |
| **JVM / Desktop** | Custom JNI layer | `java/` modules |
| **iOS / macOS** | C-interop (Kotlin Native) | `c/` wrapper + `prebuilts/` |
| **Web / WASM** | JS external declarations | `js/` module + prebuilt Filament.js |

## Build System

The project uses **Gradle (Kotlin DSL)** for dependency management and build orchestration.

- **Android** delegates entirely to the official Filament Gradle plugin / Maven artifact.
- **JVM** builds invoke **CMake** from the `:java:*` module build scripts to compile the JNI layer.
- **Native (iOS / macOS)** builds invoke **CMake** from the `:kotlin:*` module build scripts to compile the C wrapper, then run `cinterop` to generate Kotlin bindings.
- **Web** links against the prebuilt Filament.js artifact via npm.
