# Modules

Filament KMP is split into five Kotlin Multiplatform modules, mirroring Filament's own module structure. Each is published independently to Maven Central so you only depend on what you need.

Only **`filament-compose`** involves Compose. The other four are plain Kotlin bindings over the Filament API and work in any Kotlin code — a game loop, a headless renderer, an existing `SurfaceView` app. See **[Using the Engine Without Compose](engine.md)**.

> [!NOTE]
> All Kotlin modules are published under the group **`io.github.erkko68.filament`**.
> The JVM/Desktop native runtime — a single Project Panama (FFM) module — is published as **`io.github.erkko68.filament-ffm:filament-ffm`** and pulled in transitively, so you never add it by hand.
>
> Browse on Maven Central: [`io.github.erkko68.filament`](https://central.sonatype.com/namespace/io.github.erkko68.filament) · [`io.github.erkko68.filament-ffm`](https://central.sonatype.com/namespace/io.github.erkko68.filament-ffm)
> Direct repository: [`repo1.maven.org/.../io/github/erkko68/filament/`](https://repo1.maven.org/maven2/io/github/erkko68/filament/)

## Quick picker

| Use case | Add |
| :--- | :--- |
| Compose Multiplatform 3D scene | `filament-compose` |
| Driving the engine yourself — own render loop, own surface, headless | `filament` |
| Load glTF / GLB models | `gltfio` |
| Camera manipulators, HDR / KTX loading, math types | `filament-utils` |
| Compile materials at runtime | `filamat` |

`filament-compose` pulls in `filament`; the others are independent add-ons. None of
`filament`, `gltfio`, `filament-utils` or `filamat` depends on Compose.

## Dependencies by target

The coordinates are identical on every target — Kotlin Multiplatform resolves the right
variant per target, so a single `commonMain` declaration is normally all you write.
What differs is the extra platform setup around it:

| Target | Kotlin dependency | Also required |
| :--- | :--- | :--- |
| **Android** | `io.github.erkko68.filament:filament` | Nothing. The official `com.google.android.filament:filament-android` AAR comes in transitively. `compileSdk 34`, `minSdk 24`. |
| **JVM / Desktop** (macOS, Windows, Linux) | same | **JDK 22+** at build and run time. The native runtime `io.github.erkko68.filament-ffm:filament-ffm` is transitive — nothing to add by hand. See [narrowing the natives](#what-gradle-actually-downloads). |
| **iOS** (`iosArm64`, `iosSimulatorArm64`) | same | Nothing. The Filament static libraries are inside the klib. Link your framework as `isStatic = true`. |
| **Web** (`js`, `wasmJs`) | same | `filament.js` + `filament.wasm` copied into `src/jsMain/resources/` — they are **not** pulled in by Gradle. See [Platform Notes](platform-notes.md#filamentjs-and-wasm-bundle). |

> [!NOTE]
> Published Apple targets are **`iosArm64`** and **`iosSimulatorArm64`** only. There is no
> `iosX64` (Intel simulator) or standalone `macosArm64`/`macosX64` Kotlin/Native target —
> desktop macOS is served by the JVM target.

Targeting only one platform is perfectly normal — a JVM-only or Android-only Gradle module
declares the dependency in `dependencies { }` exactly like any other library:

```kotlin
// A plain JVM project — no KMP plugin, no Compose.
dependencies {
    implementation("io.github.erkko68.filament:filament:0.4.0")
}
```

## What Gradle actually downloads

**You do not get every platform's binaries.** Kotlin Multiplatform publishes one variant per
target and Gradle resolves only the ones your build declares: an Android-only app never
downloads the iOS klibs or the web artifacts, a JS app never downloads the ~13 MB desktop
natives. If your project declares three targets, you download three targets' artifacts.

The one exception is the **JVM/Desktop native runtime**. `filament-ffm` defaults to depending
on all four desktop platform modules (`macos-arm64`, `linux-x64`, `linux-arm64`,
`windows-x64`, ~13 MB each), so that a plain `./gradlew run` works on any developer machine
with zero configuration. Two ways to narrow it to the one you need — worth doing before
building an installer, since `jpackage` / Compose Desktop bundle the whole runtime classpath:

```kotlin
// Option A — declare your os/arch and Gradle picks the matching variant automatically.
configurations.matching { it.isCanBeResolved }.configureEach {
    attributes {
        attribute(OperatingSystemFamily.OPERATING_SYSTEM_ATTRIBUTE, objects.named(OperatingSystemFamily.MACOS))
        attribute(MachineArchitecture.ARCHITECTURE_ATTRIBUTE, objects.named(MachineArchitecture.ARM64))
    }
}

// Option B — depend on one platform runtime directly; it excludes its siblings.
dependencies {
    implementation("io.github.erkko68.filament-ffm:filament-ffm-runtime-macos-arm64:0.X.0")
}
```

Full details in [`java/README.md`](../java/README.md).

## Published artifacts

### `filament-compose`

Compose Multiplatform UI integration. Pulls in `filament` transitively.

```kotlin
implementation("io.github.erkko68.filament:filament-compose:0.4.0")
```

Provides `rememberFilamentScene` / `FilamentView` (and the `FilamentSceneView` single-view shortcut), the declarative scene DSL (`Light`, `GltfInstance`, …), value-based `PostProcessing`, hoisted state (`rememberCameraState`, `rememberFilamentViewState`, `rememberSkyboxState`, …), and gesture modifiers (`orbitGestures`, `mapGestures`, `flightGestures`, `pickOnTap`).

See **[Compose Integration](compose/README.md)** for the full component reference.

---

### `filament`

The core renderer. Wraps Filament's `Engine`, `Scene`, `View`, `Renderer`, `Camera`, `Texture`, `Material`, `LightManager`, `TransformManager`, `RenderableManager`, and the rest of the engine surface.

```kotlin
implementation("io.github.erkko68.filament:filament:0.4.0")
```

This is the whole engine and it stands on its own — no Compose runtime, no Compose Gradle plugin. Depend on it when you drive the render loop yourself, render into a surface you already own, or render headless; see **[Using the Engine Without Compose](engine.md)**. Compose users get it transitively via `filament-compose` and can reach the raw `Engine` through the `FilamentEffect` escape hatch, so they rarely declare it explicitly.

Upstream reference: **[Filament Engine](https://google.github.io/filament/Filament.md.html)**.

---

### `gltfio`

glTF 2.0 / GLB asset loader. Wraps `AssetLoader`, `FilamentAsset`, `FilamentInstance`, `ResourceLoader`, `Animator`, and `UbershaderProvider`.

```kotlin
implementation("io.github.erkko68.filament:gltfio:0.4.0")
```

With `filament-compose`, you typically interact with this through `rememberGltfAsset { ... }` and `GltfInstance(...)`. The raw API is available for advanced cases — instancing, material swapping, morph targets.

> [!WARNING]
> On the **web target**, several `gltfio` factories (`MaterialProvider`, `TextureLoader`) currently return `null` stubs. Use simple glTF assets only, or render on a non-web platform. See [Platform Notes](platform-notes.md#web--wasm).

Upstream reference: **[gltfio README](https://github.com/google/filament/tree/main/libs/gltfio)**.

---

### `filament-utils`

Math types, camera manipulators (orbit / map / flight), and HDR / KTX texture loaders.

```kotlin
implementation("io.github.erkko68.filament:filament-utils:0.4.0")
```

`filament-compose` builds its `rememberOrbitCameraController`, `rememberMapCameraController`, and `rememberFlightCameraController` on top of this module. Use it directly if you want a manipulator outside the Compose lifecycle.

---

### `filamat`

Runtime material compilation. Wraps `MaterialBuilder` — the same API used by Filament's `matc` command-line tool, but invoked from Kotlin at runtime.

```kotlin
implementation("io.github.erkko68.filament:filamat:0.4.0")
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

All modules share a single version, currently **`0.4.0`**, tracking Filament **1.75.0** upstream. Always upgrade all `io.github.erkko68.filament:*` artifacts together — mixed versions are not supported.

The Filament version is exposed as `filaVersion` in the root `gradle.properties` and matches the upstream tag of [`google/filament`](https://github.com/google/filament/releases).
