# `kotlin/` — Filament KMP modules

This directory contains the Kotlin Multiplatform modules published to Maven Central. Each subdirectory is an independent Gradle module.

| Module | Artifact | Description |
| :--- | :--- | :--- |
| [`filament/`](filament) | `io.github.erkko68.filament:filament` | Core renderer wrapper. |
| [`filament-compose/`](filament-compose) | `io.github.erkko68.filament:filament-compose` | Compose Multiplatform integration. |
| [`gltfio/`](gltfio) | `io.github.erkko68.filament:gltfio` | glTF / GLB asset loading. |
| [`filamat/`](filamat) | `io.github.erkko68.filament:filamat` | Runtime material compilation. |
| [`filament-utils/`](filament-utils) | `io.github.erkko68.filament:filament-utils` | Math, manipulators, HDR/KTX loaders. |

See [`docs/modules.md`](../docs/modules.md) for the full coordinates list, dependency graph, and per-module usage notes.

## Targets

Each module publishes the following Kotlin Multiplatform targets:

- `androidTarget`
- `iosArm64`, `iosSimulatorArm64`, `iosX64`
- `macosArm64`
- `jvm` (Desktop — Windows, Linux, macOS)
- `js(IR)` (Web — experimental)

## Building locally

From the repository root:

```bash
./gradlew :kotlin:filament-compose:build
./gradlew :kotlin:gltfio:build
# …or build everything:
./gradlew build
```

The first build downloads Filament prebuilts via `scripts/gradle/download_filament_prebuilts.py` — expect a few minutes.

## Contributing

See [`docs/repo-structure.md`](../docs/repo-structure.md) for how the Kotlin modules tie into the C wrapper (`c/`), JNI bindings (`java/`), and JS externals (`js/`).
