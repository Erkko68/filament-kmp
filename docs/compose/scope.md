# Filament Compose: Scope & Philosophy

The `filament-compose` module is designed to bridge the gap between the low-level, imperative Filament API and the high-level, declarative world of Compose Multiplatform.

## Core Philosophy

### 1. Simplify the 80%
The primary goal of `filament-compose` is to provide a subset of high-level utilities and Composables that cover the most common 3D rendering use cases (loading models, setting up lights, managing cameras) with minimal boilerplate.

### 2. Full Interoperability (The Escape Hatch)
While high-level abstractions are provided, the goal is never to "hide" Filament. `filament-compose` is built to allow full interoperability with the underlying Filament API. 

If the provided Composables are not sufficient for a specific use case, the raw `Engine`, `Scene`, and `View` objects remain accessible for custom low-level operations.

## Key Goals
- **Declarative Scene Graph**: The 3D scene is defined using standard Compose patterns.
- **Resource Management**: The lifecycle of Filament objects (Engine, Scene, Renderer) is automatically handled in sync with the Composable lifecycle.
- **Multiplatform by Default**: The same Compose code works across Android, iOS, Desktop, and Web.
- **Performance**: Optimized integration paths (like Zero-Copy on macOS) are provided while maintaining a consistent API.

## What it is NOT
- **A Full Game Engine**: This project does not aim to replace engines like Unity or Unreal. The focus is on high-quality 3D rendering within UI applications.
- **An Abstraction Layer over Filament**: No attempt is made to create a "generic" 3D API. Using `filament-compose` means using Filament directly.
