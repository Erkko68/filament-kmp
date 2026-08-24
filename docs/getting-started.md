# Getting Started

This guide walks you through adding Filament KMP to a Kotlin Multiplatform project and rendering your first scene on each supported target.

There are two ways to use the library. The platform setup in step 3 — JDK floor, framework linking, web bundle — applies to both; the entry points shown there are the Compose ones.

- **With Compose Multiplatform** — `filament-compose` owns the surface and the render loop; you declare the scene. This guide takes that route.
- **Without Compose** — depend on `filament` alone and drive `Engine` / `Renderer` / `SwapChain` yourself, against your own window or fully headless. That route is documented in **[Using the Engine Without Compose](engine.md)**.

> [!NOTE]
> Filament KMP requires **Kotlin 2.0+** and, for the Desktop/JVM target, **JDK 22+** (the floor for the Project Panama / FFM bindings). **Compose Multiplatform 1.7+** is needed only if you use `filament-compose`.

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
            implementation("io.github.erkko68.filament:filament-compose:0.4.0")

            // Optional: glTF / GLB model loading
            implementation("io.github.erkko68.filament:gltfio:0.4.0")

            // Optional: math helpers, HDR/KTX loaders, camera manipulators
            implementation("io.github.erkko68.filament:filament-utils:0.4.0")

            // Optional: runtime material compilation (most apps don't need this)
            implementation("io.github.erkko68.filament:filamat:0.4.0")
        }
    }
}
```

If you're not using Compose and want to drive the engine directly, depend on `filament` instead of `filament-compose` — it has no Compose dependency at all. See **[Using the Engine Without Compose](engine.md)**.

> [!NOTE]
> **You only download the targets you declare.** Kotlin Multiplatform publishes one variant per target, so an Android-only app never fetches the iOS klibs and a JS app never fetches the desktop natives. The single exception is the JVM/Desktop native runtime, which defaults to carrying all four desktop platforms — narrow it to one before packaging an installer. See **[What Gradle actually downloads](modules.md#what-gradle-actually-downloads)**.

See **[Modules](modules.md)** for the full coordinates list, the per-target dependency table, and the dependency graph.

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

### iOS

The native Filament libraries ship in the Kotlin/Native klib, so there's nothing to download manually. Just declare the framework in your `iosMain` source set as usual:

```kotlin
kotlin {
    // Published Apple targets: iosArm64 (device) and iosSimulatorArm64. There is no iosX64.
    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
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

The Compose Desktop plugin handles the rest. The native runtime — a Project Panama (FFM) module, `io.github.erkko68.filament-ffm:filament-ffm:...` — is pulled in automatically as a Gradle metadata dependency, so there's no manual classifier setup. It requires a **JDK 22+** runtime (the FFM API floor).

```kotlin
// desktopApp/build.gradle.kts
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinJvm) // or kotlin("jvm") if not using a version catalog
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    // Pin the JDK toolchain — Gradle will download JDK 22 automatically if needed.
    jvmToolchain(22)
}
```

For a shared KMP module that also targets JVM, set the JVM compiler target explicitly:

```kotlin
// shared/build.gradle.kts
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

kotlin {
    jvmToolchain(22)

    jvm {
        // Ensure the bytecode target is compatible with FFM (Java 22+)
        compilerOptions {
            jvmTarget = JvmTarget.JVM_22
        }
    }

    // Android uses a separate jvmTarget
    androidTarget {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }
}
```

> [!IMPORTANT]
> Make sure your Gradle daemon also runs on JDK 22+
> And verify with `./gradlew --version`. IntelliJ IDEA / Android Studio will prompt you to configure the Gradle JDK under **Settings → Build → Gradle → Gradle JDK**.

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

**Filament bundle.** `filament.js` and `filament.wasm` must be in your `src/jsMain/resources/` and served alongside your compiled JS. Download them from the [Filament release](https://github.com/google/filament/releases) that matches your `filaVersion`, or use the download task included in the repo:

```bash
./gradlew downloadPrebuilts_web
# outputs to prebuilts/web/ — copy filament.js and filament.wasm to src/jsMain/resources/
```

**`index.html`.** Load `filament.js` before your app script. The `FilamentApp` helper (used in the entry point below) automatically injects the root element, configures the stacking context (so Compose overlays like buttons layer correctly), and mounts the Canvas:

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        html, body { width: 100%; height: 100%; overflow: hidden; background: #000; }
    </style>
</head>
<body>
    <script src="filament.js"></script>
    <script src="webApp.js"></script>
</body>
</html>
```

Entry point:

```kotlin
// webApp/src/jsMain/kotlin/Main.kt
import io.github.erkko68.filament.compose.FilamentApp

fun main() = FilamentApp { App() }
```

## 4. Your first scene

```kotlin
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.erkko68.filament.compose.FilamentSceneView
import io.github.erkko68.filament.compose.scene.*

@Composable
fun App() {
    val cameraState = rememberCameraState(
        eye        = Position(0f, 1f, 4f),
        target     = Position(0f, 0.5f, 0f),
        projection = Projection.Perspective(fovDegrees = 45.0),
    )
    val skybox = rememberSkyboxState(SkyboxSource.Color(LinearColor(0.1f, 0.12f, 0.15f)))

    MaterialTheme {
        // The content lambda declares the world; the viewport is configured by value.
        FilamentSceneView(
            modifier       = Modifier.fillMaxSize(),
            cameraState    = cameraState,
            skyboxState    = skybox,
            postProcessing = PostProcessing(
                bloom        = Bloom(strength = 0.2f),
                antiAliasing = AntiAliasing(fxaaEnabled = true),
            ),
        ) {
            DirectionalLight(
                direction = Direction(0.3f, -1f, -0.5f),
                intensity = LightIntensity.LuminousPower(100_000f),   // lux
            )

            GltfInstance(
                asset    = rememberGltfAsset { Res.readBytes("files/Duck.glb") },
                position = Position(0f, 0f, 0f),
                scale    = Scale(1f),
            )
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
