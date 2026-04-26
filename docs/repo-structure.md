# Repository Structure

The `filament-kmp` project is a Kotlin Multiplatform (KMP) wrapper for Google's [Filament](https://github.com/google/filament) rendering engine. It is organized into several modules to handle the cross-platform nature of the engine and the UI integrations.

## Modules

### 1. `c/`
This directory contains the C++ wrapper code that exposes a C-compatible API.
- **Submodules**: `filament`, `filamat`, `gltfio`, `filament-utils`.
- **Purpose**: Primarily used by **Kotlin Native** targets (iOS, macOS) via `cinterop`. It provides a stable C interface to Filament's C++ classes.

### 2. `java/`
The Java/JVM modules which include their own **JNI (Java Native Interface)** bindings.
- **Location**: `java/filament`, `java/filamat`, etc.
- **Role**: Implements the Java side of the Filament API. Unlike the Native targets, the Java modules use their own dedicated JNI C++ code (located in `src/main/cpp` within each module) rather than the shared C wrapper in `c/`. This approach allows for JNI-specific optimizations and better integration with the JVM garbage collector and lifecycle.

### 3. `kotlin/`
The core Kotlin Multiplatform wrapper.
- **`kotlin/filament`**: Core engine components (Engine, Scene, Renderer, etc.).
- **`kotlin/gltfio`**: Asset loading and management.
- **`kotlin/filament-utils`**: Math and helper utilities.
- **Targets**: Android, JVM, iOS, macOS, JS (WebGL).

### 4. `kotlin:filament-compose`
The UI integration layer for Compose Multiplatform.
- **Purpose**: Provides a declarative DSL for building Filament scenes within a Compose application.
- **Key Components**: `FilamentView`, `PointLight`, `GltfModel`, `PerspectiveCamera`.
- **Implementation**: Uses a `FilamentSurface` which adapts to different rendering strategies per platform (see [Compose Integration](compose/integration-strategies.md)).

### 5. `js/`
Specific logic and wrappers for the JavaScript/WebGL target.

### 6. `samples/`
Example applications demonstrating how to use the library.
- `shared/`: Common code for samples.
- `androidApp/`, `desktopApp/`, `iosApp/`, `webApp/`: Platform-specific entry points.

---

## Build System
The project uses Gradle with Kotlin DSL.
- `build.gradle.kts`: Root build script.
- `settings.gradle.kts`: Module inclusions and project configuration.
- `gradle/libs.versions.toml`: Centralized dependency management.

Native code is managed via **CMake**, which is invoked by Gradle during the build process for JVM and Android targets.
