# `:android` — Android runtime for `:jni`

Builds `libfilament-c.so` for arm64-v8a, armeabi-v7a, x86_64 and x86 and ships it with the
[`:jni`](../jni/README.md) bindings as **`io.github.erkko68.filament:filament-jni-android`** (AAR). Every
`:kotlin:*` module's Android target pulls it in.

- **Native build:** `buildJniLibs` runs `c/CMakeLists.txt` (`FILAMENT_PLATFORM=android`, target
  `filament-c-android`) once per ABI with the SDK's NDK and CMake, over upstream's
  `filament-v<ver>-android-native.tgz` prebuilts (`downloadPrebuilts_android-<abi>`). NDK 29.0.14206865 and
  CMake 3.22.1 are pinned to upstream's (`build/common/versions`) so the prebuilts' libc++ matches; install them
  with `sdkmanager "ndk;29.0.14206865" "cmake;3.22.1"`. The JNI glue for the common `external fun`s
  (`:jni:generateJniGlue`) is compiled in through `FILA_JNI_GLUE_DIR`.
- **Why not `externalNativeBuild`:** AGP's native build needs the SDK and NDK just to configure, which breaks
  every host without them (e.g. the linux-arm64 CI runner). As a plain task on the KMP Android plugin, nothing
  touches the SDK until an Android task runs.
- **Android-only JNI:** `src/androidMain/cpp/FilaAndroid.cpp` + `FilaAndroid.kt` (`Surface` → `ANativeWindow`).
- **Tests:** `./gradlew :android:connectedAndroidDeviceTest` exercises each boundary kind the generator emits.
