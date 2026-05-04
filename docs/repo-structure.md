# Repository Structure

The `filament-kmp` project is organized into several modules to handle the cross-platform nature of the Filament engine.

## Core Modules

- **`c/`**: C++ wrapper code that exposes a C-compatible API. This is primarily used by **Kotlin Native** targets (iOS, macOS) via `cinterop`.
- **`java/`**: Java/JVM modules with dedicated **JNI** bindings. This allows for JNI-specific optimizations and better integration with the JVM garbage collector.
- **`kotlin/`**: The core Kotlin Multiplatform (KMP) wrapper.
    - `filament`: Core engine components.
    - `gltfio`: Asset loading.
    - `filament-utils`: Math and helper utilities.
- **`kotlin/filament-compose/`**: UI integration layer for Compose Multiplatform.
- **`samples/`**: Multiplatform example applications for Android, iOS, Desktop, and Web.

## Build System

The project uses **Gradle (Kotlin DSL)** for dependency management and build orchestration. Native code is managed via **CMake**, which is invoked during the build process for JVM and Android targets.
