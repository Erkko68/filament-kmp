# `:jni` — JNI runtime over the `Fila*` C API

The JNI layer shared by **JVM desktop** and **Android**: the common API classes' `external fun`s
compile to JNI methods, and this module supplies what they need at runtime. Published as
**`io.github.erkko68.filament:filament-jni`**, a plain Kotlin/JVM jar at the Android bytecode floor
(Java 11). See [Native Bindings](../docs/bindings.md) for the overall model.

It holds sources only, no native library. Each runtime compiles `src/main/cpp` and the generated
glue into its `libfilament-c`:

| Runtime | Built by | Artifact |
|---|---|---|
| Android (arm64-v8a, armeabi-v7a, x86_64, x86) | [`android/`](../android/README.md) (`buildJniLibs`) | `filament-jni-android` |
| JVM desktop (macOS, Linux, Windows) | `java/`'s CMake build (`FILAMENT_BUILD_SHARED`) | `filament-ffm-runtime-*` for now |

- **JNI glue:** `./gradlew :jni:generateJniGlue`
  ([`GenerateJniGlue`](../build-logic/src/main/kotlin/generators/GenerateJniGlue.kt)) scans
  `kotlin/*/src/commonMain` for `@ExternalSymbolName` externals and writes one `Java_…` forwarder
  per declaration into `build/generated/jniGlue/`. It is a build artifact, not committed; both
  native builds depend on the task and compile the output in (`FILA_JNI_GLUE_DIR`).
- **Runtime:** `src/main/cpp/FilaJni.cpp` + `FilaJni.kt` hold what can't be generated:
  `JNI_OnLoad`, native memory, and callbacks. Platform-only entry points live in the runtime
  module (e.g. Android's `FilaAndroid`).
- **Loading:** `FilaJni` loads `libfilament-c` on first use: `System.loadLibrary` on Android, and on
  desktop [`FilamentLoader`](src/main/java/io/github/erkko68/filament/jni/FilamentLoader.java), which
  extracts it from the runtime jar into a content-hashed cache dir (`~/.filament-kmp/`, overridable
  with `filament.library.path` / `filament.data.path`).
- **Exports:** `src/main/cpp/filament-c-jni.map` seals the Android library to `JNI_OnLoad` and
  `Java_*`; the desktop library also keeps `Fila*` for the FFM bindings still in use.

## Being retired

`./gradlew :jni:generateJniBindings` still generates, from the C headers, the committed
`src/main/cpp/generated/<Module>C.c` + `src/main/generated/<Module>C.kt` (top-level `external fun`s
named like the C functions, enum/typedef aliases, struct views) and the helpers in `FilaJni.kt`
(`heapScoped`, `usePinned`, `F32Array`…). They serve the Android `actual`s that haven't moved to
`commonMain` yet and are deleted once nothing uses them.
