# Getting Started

This guide walks you through adding Filament KMP to a Kotlin Multiplatform project and rendering your first scene on each supported target.

> [!NOTE]
> Filament KMP requires **Kotlin 2.0+**, **Compose Multiplatform 1.7+**, and a JDK capable of running Compose Desktop (JDK 17+).

## 1. Add the Maven Central repository

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google() // required for the Android target
    }
}
```

## 2. Pick the modules you need

Most apps want **`filament-compose`** — it pulls in the core renderer and the Compose DSL:

```kotlin
// shared/build.gradle.kts
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.erkko68.filament:filament-compose:0.1.0-alpha01")

            // Optional: glTF / GLB model loading
            implementation("io.github.erkko68.filament:gltfio:0.1.0-alpha01")

            // Optional: math helpers, HDR/KTX loaders, camera manipulators
            implementation("io.github.erkko68.filament:filament-utils:0.1.0-alpha01")

            // Optional: runtime material compilation (most apps don't need this)
            implementation("io.github.erkko68.filament:filamat:0.1.0-alpha01")
        }
    }
}
```

If you're not using Compose and want to drive the engine directly, depend on `filament` instead of `filament-compose`. See **[Modules](modules.md)** for the full coordinates list and dependency graph.

## 3. Configure each target

### Android

No extra configuration. Android uses the official `com.google.android.filament` Maven artifact, which is pulled in transitively. The minimum supported `compileSdk` is **34**.

```kotlin
// androidApp/build.gradle.kts
android {
    compileSdk = 34
    defaultConfig { minSdk = 24 }
}
```

### iOS / macOS

The native Filament libraries ship in the Kotlin/Native klib, so there's nothing to download manually. Just declare the framework in your `iosMain` source set as usual:

```kotlin
kotlin {
    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
}
```

In Xcode, link the produced `Shared.framework` from your app target. See [`samples/iosApp`](../samples/iosApp) for a working SwiftUI entry point.

### JVM / Desktop

The Compose Desktop plugin handles the rest. The JNI runtime (`io.github.erkko68.filament-jni:filament:...`) is pulled in automatically as a Gradle metadata dependency — no manual classifier setup needed.

```kotlin
// desktopApp/build.gradle.kts
plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
    }
}
```

> [!WARNING]
> **Windows JVM shutdown crash.** On Windows, the JVM occasionally crashes in Filament's native static destructors during process teardown, producing a non-zero exit code (so Gradle reports `BUILD FAILED` even when your app ran successfully). Work around this by calling `exitProcess(0)` after your Compose application returns:
>
> ```kotlin
> fun main() {
>     singleWindowApplication(title = "My App") { App() }
>     kotlin.system.exitProcess(0)
> }
> ```
>
> See [Platform Notes](platform-notes.md#jvm--desktop) for context.

### Web / WASM

> [!WARNING]
> The web target is **experimental**. Several `gltfio` and `filament-utils` APIs are unimplemented on JS (see [Platform Notes](platform-notes.md#web--wasm)). It's good enough for simple scenes.

Enable the experimental Compose JS canvas flag:

```properties
# gradle.properties
org.jetbrains.compose.experimental.jscanvas.enabled=true
```

```kotlin
// webApp/build.gradle.kts
kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
}
```

The prebuilt Filament.js / WebAssembly bundle is included in the published artifact and loaded by the wrapper.

## 4. Your first scene

```kotlin
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.erkko68.filament.LightManager
import io.github.erkko68.filament.compose.FilamentView
import io.github.erkko68.filament.compose.scene.*

@Composable
fun App() {
    val cameraState = rememberCameraState(
        eye        = Position(0f, 1f, 4f),
        target     = Position(0f, 0.5f, 0f),
        projection = Projection.Perspective(fovDegrees = 45.0),
    )
    val skybox = rememberSkyboxState(SkyboxSource.Color(Color(0.1f, 0.12f, 0.15f)))

    MaterialTheme {
        FilamentView(
            modifier    = Modifier.fillMaxSize(),
            cameraState = cameraState,
            skyboxState = skybox,
        ) {
            Light(
                type      = LightManager.Type.DIRECTIONAL,
                direction = Direction(0.3f, -1f, -0.5f),
                intensity = 100_000f,
            )

            GltfInstance(
                asset    = rememberGltfAsset { Res.readBytes("files/Duck.glb") },
                position = Position(0f, 0f, 0f),
                scale    = Scale(1f),
            )

            Bloom(strength = 0.2f)
            AntiAliasing(fxaaEnabled = true)
        }
    }
}
```

That's the complete shared code for all four targets. See [`samples/`](../samples/) for the platform-specific entry points (`Main.kt` on Desktop, `ContentView.swift` on iOS, etc.).

## Next steps

- **[Compose Integration](compose/README.md)** — Full component reference: cameras, lights, materials, post-processing.
- **[Modules](modules.md)** — Pick the right artifact for your use case.
- **[Platform Notes](platform-notes.md)** — Backends, performance, known issues.
- **[Filament Engine docs](https://google.github.io/filament/Filament.md.html)** — PBR theory, lighting model, render pipeline.
- **[Filament Materials docs](https://google.github.io/filament/Materials.md.html)** — Authoring materials with `matc` or `MaterialBuilder`.
