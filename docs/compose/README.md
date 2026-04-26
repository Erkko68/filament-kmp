# Filament Compose Documentation

The `filament-compose` module provides the integration between the [Filament](https://github.com/google/filament) rendering engine and [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform).

## Overview

Integrating a high-performance 3D engine into a modern UI framework requires bridging two different rendering pipelines. This documentation explains how we achieve this integration across different platforms.

## Core Concepts

### [Integration Strategies](integration-strategies.md)
Understand how we bridge the gap between Filament's GPU output and Compose's Skia-based canvas. This covers the widely-used **ReadPixel approach**.

### [Web (JS) Integration](js-integration.md)
Learn about the **Hole Punching** strategy used for the Web target, utilizing DOM overlays and z-index layering.

### [Zero-Copy Rendering](zero-copy.md)
Learn about the high-performance path available on macOS and the current technical limitations preventing its implementation on Windows and Linux.

---

## Component Reference

- **`FilamentView`**: The main entry point. Provides the rendering surface and sets up the Filament environment (Engine, Scene, View).
- **`GltfModel`**: Declarative component for loading and placing 3D assets.
- **Lighting**:
    - `DirectionalLight`: Infinite light source.
    - `SunLight`: Directional light with a visual sun disk.
    - `PointLight`: Positional light source with falloff.
    - `SpotLight`: Directional positional light with a cone.
- **`PerspectiveCamera`**: Controls the viewport and projection.
- **`Skybox`**: Sets the background and environment lighting (IBL).
- **`FilamentEffect`**: A low-level "escape hatch" to access the raw Filament objects (Scene, View, etc.) for custom logic.
