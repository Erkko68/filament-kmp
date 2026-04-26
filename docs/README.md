# Filament KMP Documentation

Welcome to the documentation for the `filament-kmp` project, a Kotlin Multiplatform wrapper for the Filament rendering engine.

## Getting Started

### [Repository Structure](repo-structure.md)
Understand how the project is organized, including the C++ wrapper, JNI layers, and KMP modules.

### [Compose Multiplatform Integration](compose/README.md)
Deep dive into how we integrate Filament with Compose UI, including rendering strategies and platform-specific optimizations.

### [Automation & Utility Scripts](scripts/README.md)
Documentation for our Python-based automation tools for API coverage and library management.

### [JVM GPU Rendering (Advanced)](jvm-gpu-rendering.md)
Technical details on the low-level GPU-to-GPU sharing implementation for JVM targets.

---

## Key Modules

- **`kotlin/`**: The main KMP API.
- **`filament-compose/`**: The Compose UI layer.
- **`samples/`**: Example applications for all platforms.

## Contributing
Please refer to the [main README](../README.md) for build instructions and contribution guidelines.
