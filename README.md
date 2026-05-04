# Filament KMP

> [!WARNING]
> **Early Stages**: This project is in its early stages of development. APIs and features are subject to change.

Filament KMP is a Kotlin Multiplatform (KMP) wrapper for Google's [Filament](https://github.com/google/filament) rendering engine.

The main goal of this project is to provide the same high-performance 3D rendering experience as **Android Filament**, but for the rest of the platforms (**iOS**, **Desktop/JVM**, **Web/WASM**) with first-class integration for **Compose Multiplatform**.

## API Strategy

The core philosophy of this project is to keep the API as close as possible to the **Android Filament API**. Developers with experience using Filament on Android will find familiar classes and naming conventions here.

The API is modernized to provide a better Kotlin experience:
- **Kotlin-First**: Kotlin properties are used instead of traditional getters and setters for single values.
- **Clean API**: Methods that are already deprecated in Filament or are strictly specific to the Android platform (e.g., those requiring Android `Context` or specific Android UI classes) are removed.
- **Parity**: This ensures that while the project stays true to Filament's architecture, the developer experience is optimized for modern Multiplatform projects.

## Project Structure

- **`kotlin/`**: Core KMP modules — `filament`, `filamat`, `gltfio`, `filament-utils`, and `filament-compose`.
- **`c/`**: C++ wrapper providing a C-compatible API for **Kotlin Native** targets (iOS, macOS) via `cinterop`.
- **`java/`**: JNI bindings for the **JVM/Desktop** target. Android uses the official `com.google.android.filament` package and does not depend on this module.
- **`js/`**: Kotlin/JS external declarations wrapping the official Filament.js/WASM library for the **Web** target.
- **`prebuilts/`**: Prebuilt Filament static libraries for iOS, macOS, and WASM targets.
- **`samples/`**: Multiplatform example applications for all supported targets.

## Documentation

- [Getting Started](docs/README.md)
- [Repository Architecture](docs/repo-structure.md)
- [Compose Integration](docs/compose/README.md)
