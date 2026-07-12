# Repository Structure

The `filament-kmp` project is organized into several modules to handle the cross-platform nature of the Filament engine.

## Architecture at a glance

One shared `expect` surface in `commonMain` fans out into four platform `actual`
implementations, each bound to the native engine by a different mechanism. Everything above
the dashed line is Kotlin we maintain; everything below is the upstream Google Filament engine.

```mermaid
flowchart TB
    subgraph common["kotlin/ · commonMain — one expect surface"]
        API["filament · filamat · gltfio · filament-utils<br/>filament-compose (Compose MP UI)"]
    end

    API --> AND["androidMain<br/>(actual)"]
    API --> JVM["jvmMain<br/>(actual)"]
    API --> NAT["iosMain / macosMain<br/>(actual)"]
    API --> WEB["jsMain<br/>(actual)"]

    AND -->|official AAR| MAVEN["com.google.android.filament<br/>Maven artifact"]
    JVM -->|jextract → FFM| FFM["java/ · libfilament-c<br/>(shared, built from c/)"]
    NAT -->|cinterop| CIN["c/ wrapper<br/>(per-module static libs)"]
    WEB -->|committed externals<br/>vs jsbindings.cpp| JS["web/ · Filament.js (WASM)"]

    FFM --> CWRAP["c/ — C-ABI over Filament C++"]
    CIN --> CWRAP

    CWRAP -.-> ENGINE
    MAVEN -.-> ENGINE
    JS -.-> ENGINE

    subgraph upstream["Google Filament (upstream)"]
        ENGINE["Native engine + prebuilt binaries<br/>(prebuilts/ · include/ · downloaded per filaVersion)"]
    end
```

See [Binding Strategy by Platform](#binding-strategy-by-platform) below for the same mapping
in table form.

## Core Modules

- **`c/`**: C++ wrapper that exposes a C-compatible ABI around the official Filament C++ API. Consumed by two code generators: **Kotlin Native** targets (iOS, macOS) via `cinterop` (one CMake library per sub-module — `libfilament-c.a`, `libfilamat-c.a`, `libfilament-utils-c.a`, `libgltfio-c.a`), and the **JVM/Desktop** target via `jextract` (the `FILAMENT_BUILD_SHARED` path links all four into one `libfilament-c` shared image). Each module's C-ABI types live in its own `*Types.h` header (core types in `filament/c/FilaTypes.h`).

- **`java/`**: The single Project Panama (FFM) JVM binding module used by the **JVM/Desktop** target only. It drives the combined `libfilament-c` shared build via CMake, runs `jextract` over the C headers to generate the low-level bindings, bundles the native image as a JAR resource, and loads it at runtime. **Android does not use this folder** — it depends on the official `com.google.android.filament` Maven packages instead. See [`java/README.md`](../java/README.md).

- **`web/`**: Kotlin external declarations wrapping the official Filament.js (WASM) library, used by the **Web** targets (`js` + `wasmJs`). The declarations are **committed, hand-maintained sources** audited against upstream `jsbindings.cpp` (the real embind surface — the shipped `filament.d.ts` under-reports it). See [`web/README.md`](../web/README.md).

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
| **JVM / Desktop** | Project Panama (FFM) over the C wrapper | `java/` module + `c/` wrapper |
| **iOS / macOS** | C-interop (Kotlin Native) | `c/` wrapper + `prebuilts/` |
| **Web / WASM** | committed Kotlin externals | `web/` module + prebuilt Filament.js |

## Build System

The project uses **Gradle (Kotlin DSL)** for dependency management and build orchestration.

- **Android** delegates entirely to the official Filament Gradle plugin / Maven artifact.
- **JVM** builds invoke **CMake** from the `:java` module build script to compile the combined `libfilament-c` shared image, then run **`jextract`** over the C headers to generate the FFM bindings.
- **Native (iOS / macOS)** builds invoke **CMake** from the `:kotlin:*` module build scripts to compile the C wrapper, then run `cinterop` to generate Kotlin bindings.
- **Web** links against the prebuilt Filament.js artifact; the Kotlin externals are committed sources maintained against upstream `jsbindings.cpp` (see [`web/README.md`](../web/README.md)).
