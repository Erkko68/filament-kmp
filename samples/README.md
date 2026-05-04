# Samples

This directory contains multiplatform example applications demonstrating `filament-compose` across all supported targets.

## Applications

| App | Target | Entry point |
| :--- | :--- | :--- |
| `androidApp/` | Android | Standard Android Compose app using the official Filament Android library |
| `iosApp/` | iOS | Xcode project with a SwiftUI entry point embedding the Kotlin Multiplatform framework |
| `desktopApp/` | JVM / Desktop | Compose Desktop app using the custom JNI-based Filament bindings |
| `webApp/` | Browser / WASM | Compose for Web app using the Filament.js/WASM bindings |

All apps share a common scene implementation in `shared/`:

- `commonMain` — Shared Compose scene code (models, lights, camera, post-processing).
- `androidMain`, `iosMain`, `desktopMain`, `webMain` — Platform-specific entry points and resource loading.

## Build & Run

**Android**
```shell
./gradlew :samples:androidApp:assembleDebug
```

**Desktop (JVM)**
```shell
./gradlew :samples:desktopApp:run
```

**iOS** — Open `samples/iosApp/iosApp.xcodeproj` in Xcode and run the scheme.

**Web**
```shell
./gradlew :samples:webApp:jsBrowserDevelopmentRun
```
