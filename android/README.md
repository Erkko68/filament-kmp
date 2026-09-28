# `:android` — Android native runtime (JNI)

The Android counterpart of [`java/`](../java/README.md) (FFM) and [`web/`](../web/README.md) (wasm): binds the
`Fila*` C API in [`c/`](../c) through JNI. Published as **`io.github.erkko68.filament:filament-jni`** and pulled
in transitively by every `:kotlin:*` module's Android target.

- **Native build:** AGP runs `c/CMakeLists.txt` with `FILAMENT_PLATFORM=android` once per ABI (arm64-v8a,
  armeabi-v7a, x86_64, x86), linking one `libfilament-c.so` over upstream's `filament-v<ver>-android-native.tgz`
  prebuilts (`downloadPrebuilts_android-<abi>`). NDK and CMake are pinned to upstream's
  (`build/common/versions`) so the prebuilts' libc++ matches.
- **Generated bindings:** `./gradlew :android:generateJniBindings` parses the C headers with clang and writes, per C
  module, `src/main/cpp/generated/<Module>C.c` (JNI forwarders) and `src/main/generated/<Module>C.kt` (top-level
  `external fun`s named like the C functions, enum/typedef aliases, struct views). Both are committed; CI fails if
  they drift from the headers.
- **Runtime:** `src/main/cpp/FilaJni.cpp` + `FilaJni.kt` hold what can't be generated — `JNI_OnLoad`, native
  memory, `Surface` → `ANativeWindow`, and callbacks. Its helpers (`heapScoped`, `usePinned`, `upload`,
  `Callbacks`, `F32Array`, `PtrArray`…) mirror `:web`'s, so actuals port between the two.

Struct layouts differ per ABI (pointer and `size_t` width, x86-32 alignment), so struct views ask the C side for
`sizeof`/`offsetof` at runtime instead of baking offsets in.
