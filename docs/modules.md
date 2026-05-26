# Modules

Filament KMP is split into five Kotlin Multiplatform modules, mirroring Filament's own module structure. Each is published independently to Maven Central so you only depend on what you need.

> [!NOTE]
> All Kotlin modules are published under the group **`io.github.erkko68.filament`**.
> JVM/Desktop JNI runtime JARs are published under **`io.github.erkko68.filament-jni`** and pulled in transitively — you never add them by hand.
>
> Browse on Maven Central: [`io.github.erkko68.filament`](https://central.sonatype.com/namespace/io.github.erkko68.filament) · [`io.github.erkko68.filament-jni`](https://central.sonatype.com/namespace/io.github.erkko68.filament-jni)
> Direct repository: [`repo1.maven.org/.../io/github/erkko68/filament/`](https://repo1.maven.org/maven2/io/github/erkko68/filament/)

## Quick picker

| Use case | Add |
| :--- | :--- |
| Compose Multiplatform 3D scene | `filament-compose` |
| Load glTF / GLB models | `gltfio` |
| Camera manipulators, HDR / KTX loading | `filament-utils` |
| Driving Filament directly (no Compose) | `filament` |
| Compile materials at runtime | `filamat` |

## Published artifacts

### `filament-compose`

Compose Multiplatform UI integration. Pulls in `filament` transitively.

```kotlin
implementation("io.github.erkko68.filament:filament-compose:0.1.1-rc02")
```

Provides `FilamentView`, the declarative scene DSL (`Light`, `GltfInstance`, post-processing composables), hoisted state (`rememberCameraState`, `rememberSkyboxState`, …), and gesture modifiers (`orbitGestures`, `mapGestures`, `flightGestures`, `pickOnTap`).

See **[Compose Integration](compose/README.md)** for the full component reference.

---

### `filament`

The core renderer. Wraps Filament's `Engine`, `Scene`, `View`, `Renderer`, `Camera`, `Texture`, `Material`, `LightManager`, `TransformManager`, `RenderableManager`, and the rest of the engine surface.

```kotlin
implementation("io.github.erkko68.filament:filament:0.1.1-rc02")
```

Use this directly only if you're not using Compose, or if you need an API not yet exposed by `filament-compose`. The Compose DSL provides an escape hatch (`FilamentEffect`) that hands you the raw `Engine`, so most apps don't need to depend on `filament` directly.

Upstream reference: **[Filament Engine](https://google.github.io/filament/Filament.md.html)**.

---

### `gltfio`

glTF 2.0 / GLB asset loader. Wraps `AssetLoader`, `FilamentAsset`, `FilamentInstance`, `ResourceLoader`, `Animator`, and `UbershaderProvider`.

```kotlin
implementation("io.github.erkko68.filament:gltfio:0.1.1-rc02")
```

With `filament-compose`, you typically interact with this through `rememberGltfAsset { ... }` and `GltfInstance(...)`. The raw API is available for advanced cases — instancing, material swapping, morph targets.

> [!WARNING]
> On the **web target**, several `gltfio` factories (`MaterialProvider`, `TextureLoader`) currently return `null` stubs. Use simple glTF assets only, or render on a non-web platform. See [Platform Notes](platform-notes.md#web--wasm).

Upstream reference: **[gltfio README](https://github.com/google/filament/tree/main/libs/gltfio)**.

---

### `filament-utils`

Math types, camera manipulators (orbit / map / flight), and HDR / KTX texture loaders.

```kotlin
implementation("io.github.erkko68.filament:filament-utils:0.1.1-rc02")
```

`filament-compose` builds its `rememberOrbitCameraState`, `rememberMapCameraState`, and `rememberFlightCameraState` on top of this module. Use it directly if you want a manipulator outside the Compose lifecycle.

---

### `filamat`

Runtime material compilation. Wraps `MaterialBuilder` — the same API used by Filament's `matc` command-line tool, but invoked from Kotlin at runtime.

```kotlin
implementation("io.github.erkko68.filament:filamat:0.1.1-rc02")
```

Most apps **don't need this**. The standard workflow is to compile `.mat` source files to `.filamat` binaries at build time with `matc`, ship the `.filamat` as a resource, and load it with `Material.Builder().payload(...)`. Add `filamat` only if you generate material source dynamically at runtime.

Upstream reference: **[Materials](https://google.github.io/filament/Materials.md.html)**.

## Dependency graph

```
filament-compose
    └── filament

gltfio
    └── filament

filament-utils
    └── filament

filamat
    └── filament
```

Adding `filament-compose` gives you `filament`. Adding `gltfio` or `filament-utils` does **not** give you Compose support — they are usable from non-Compose code too.

## Versioning

All modules share a single version, currently **`0.1.1-rc02`**, tracking Filament **1.71.4** upstream. Always upgrade all `io.github.erkko68.filament:*` artifacts together — mixed versions are not supported.

The Filament version is exposed as `filaVersion` in the root `gradle.properties` and matches the upstream tag of [`google/filament`](https://github.com/google/filament/releases).
