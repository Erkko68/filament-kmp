# `:jni` — JNI runtime over the `Fila*` C API

The JNI layer shared by **desktop** and **Android**: the common API classes' `external fun`s compile to
JNI methods, and this module supplies what they need at runtime. Published as
**`io.github.erkko68.filament:filament-jni`**, a Kotlin/JVM jar at the Android bytecode floor (Java 11).
See [Native Bindings](../docs/bindings.md) for the overall model.

It holds no native library; each native runtime links one from these sources:

| Runtime | Built by | Artifact |
|---|---|---|
| Desktop (macOS, Linux, Windows) | [`desktop/`](../desktop/README.md) (`cmakeBuild`) | `filament-jni-runtime-<os>-<arch>` |
| Android (arm64-v8a, armeabi-v7a, x86_64, x86) | [`android/`](../android/README.md) (`cmakeBuild_<abi>`) | `filament-jni-android` |

- [`CMakeLists.txt`](CMakeLists.txt): the `filament-c-jni` image (`libfilament-c`), added by `c/CMakeLists.txt`
  for the desktop and Android platforms: the C API objects, `src/main/cpp/FilaJni.cpp`, and the forwarders.
- **Forwarders:** `./gradlew :generateBindings` writes one `Java_…` function per common external into
  `build/generated/bindings/jni/` (see [`buildlogic.bindings`](../build-logic/src/main/kotlin/buildlogic/bindings)).
  A build artifact, not committed.
- **Runtime:** `FilaJni.cpp` + `FilaJni.kt` hold what can't be generated: `JNI_OnLoad`, native memory and
  callbacks. Platform-only entry points live in the runtime module (Android's `FilaAndroid`).
- **Seal:** [`filament-c-jni.map`](src/main/cpp/filament-c-jni.map) exports only `JNI_OnLoad` and `Java_*`.
- **Loading** is `Filament.init()`'s job on each platform: `System.loadLibrary` on Android, the desktop
  loader on the JVM.
