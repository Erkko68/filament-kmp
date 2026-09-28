# `:jni` — JNI bindings over the `Fila*` C API

The JNI counterpart of [`java/`](../java/README.md) (FFM) and [`web/`](../web/README.md) (wasm): binds the `Fila*`
C API in [`c/`](../c) through JNI. Published as **`io.github.erkko68.filament:filament-jni`**, a plain Kotlin/JVM jar
at the Android bytecode floor (Java 11), so nothing in it needs a recent JDK.

It holds sources only, no native library. Each JNI runtime compiles `src/main/cpp` into its `libfilament-c`:

| Runtime | Module | Artifact |
|---|---|---|
| Android (arm64-v8a, armeabi-v7a, x86_64, x86) | [`android/`](../android/README.md) | `filament-jni-android` |
| JVM desktop | not yet (JVM uses FFM, [`java/`](../java/README.md)) | — |

- **Generated bindings:** `./gradlew :jni:generateJniBindings` parses the C headers with clang and writes, per C
  module, `src/main/cpp/generated/<Module>C.c` (JNI forwarders) and `src/main/generated/<Module>C.kt` (top-level
  `external fun`s named like the C functions, enum/typedef aliases, struct views). Both are committed; CI fails if
  they drift from the headers.
- **Runtime:** `src/main/cpp/FilaJni.cpp` + `FilaJni.kt` hold what can't be generated: `JNI_OnLoad`, native
  memory, and callbacks. Its helpers (`heapScoped`, `usePinned`, `upload`, `Callbacks`, `F32Array`, `PtrArray`…)
  mirror `:web`'s, so actuals port between the two. Platform-only entry points live in the runtime module
  (e.g. Android's `FilaAndroid`).
- **Exports:** `src/main/cpp/filament-c-jni.map` seals the library to `JNI_OnLoad` and `Java_*`.

Struct layouts differ per ABI (pointer and `size_t` width, x86-32 alignment), so struct views ask the C side for
`sizeof`/`offsetof` at runtime instead of baking offsets in.
