# Filament Compose Documentation

The `filament-compose` module provides the integration between the [Filament](https://github.com/google/filament) rendering engine and **Compose Multiplatform**.

## Overview

- **[Scope & Philosophy](scope.md)**: Understand the goals and design principles behind `filament-compose`.
- **[Integration Strategies](integration-strategies.md)**: Details on the mechanism used to bridge Filament's GPU output with the Compose canvas (ReadPixel vs. Zero-Copy).

## Component Reference

- **`FilamentView`**: The main entry point for rendering a 3D scene within Compose.
- **`GltfModel`**: Declarative component for loading and placing 3D assets.
- **Lighting**:
    - `DirectionalLight`, `SunLight`, `PointLight`, `SpotLight`.
- **`PerspectiveCamera`**: Controls the viewport and projection.
- **`Skybox`**: Sets the background and environment lighting (IBL).
- **`FilamentEffect`**: An "escape hatch" to access raw Filament objects for custom logic.
