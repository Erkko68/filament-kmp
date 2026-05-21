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
            implementation("io.github.erkko68.filament:filament-compose:0.1.0-beta01")

            // Optional: glTF / GLB model loading
            implementation("io.github.erkko68.filament:gltfio:0.1.0-beta01")

            // Optional: math helpers, HDR/KTX loaders, camera manipulators
            implementation("io.github.erkko68.filament:filament-utils:0.1.0-beta01")

            // Optional: runtime material compilation (most apps don't need this)
            implementation("io.github.erkko68.filament:filamat:0.1.0-beta01")
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

Entry point:

```kotlin
// androidApp/src/androidMain/kotlin/MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
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

In Xcode, link the produced `Shared.framework` from your app target. Entry point:

```swift
// iosApp/ContentView.swift
import SwiftUI
import shared

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView().ignoresSafeArea()
    }
}
```

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

Entry point:

```kotlin
// desktopApp/src/jvmMain/kotlin/Main.kt
fun main() {
    singleWindowApplication(title = "My App") {
        App()
    }
}
```

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

**Filament bundle.** `filament.js` and `filament.wasm` must be in your `src/jsMain/resources/` and served alongside your compiled JS. Download them from the [Filament release](https://github.com/google/filament/releases) that matches your `filaVersion`, or use the helper script included in the repo:

```bash
python3 scripts/download_filament_prebuilts.py <version> web
# outputs to prebuilts/web/ — copy filament.js and filament.wasm to src/jsMain/resources/
```

**`index.html`.** Load `filament.js` before your app script, and give the root element a stacking context so any Compose overlay elements (HUDs, buttons) can be layered on top via `z-index`:

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        html, body { width: 100%; height: 100%; overflow: hidden; background: #000; }
        #root { width: 100%; height: 100%; position: relative; z-index: 0; }
    </style>
</head>
<body>
    <div id="root"></div>
    <script src="filament.js"></script>
    <script src="webApp.js"></script>
</body>
</html>
```

Entry point:

```kotlin
// webApp/src/jsMain/kotlin/Main.kt
fun main() {
    window.asDynamic()._filamentReady = ::startApp
    js("""
        Filament.init([], function() {
            var nativeFetch = window.fetch;
            Object.assign(window, Filament);
            window.fetch = nativeFetch;
            window._filamentReady();
        });
    """)
}

@JsExport
fun startApp() {
    val container = document.getElementById("root") ?: error("No #root element")
    ComposeViewport(container) { App() }
}
```

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

That's the complete shared code for all four targets. The platform-specific entry points shown in step 3 wire it up for each target.

## Next steps

- **[Compose Integration](compose/README.md)** — Full component reference: cameras, lights, materials, post-processing.
- **[Modules](modules.md)** — Pick the right artifact for your use case.
- **[Platform Notes](platform-notes.md)** — Backends, performance, known issues.
- **[Filament Engine docs](https://google.github.io/filament/Filament.md.html)** — PBR theory, lighting model, render pipeline.
- **[Filament Materials docs](https://google.github.io/filament/Materials.md.html)** — Authoring materials with `matc` or `MaterialBuilder`.
