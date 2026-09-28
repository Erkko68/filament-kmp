# `:android` — Android runtime for `:jni`

Builds `libfilament-c.so` for arm64-v8a, armeabi-v7a, x86_64 and x86 and ships it with the
[`:jni`](../jni/README.md) runtime as **`io.github.erkko68.filament:filament-jni-android`** (AAR). Every
`:kotlin:*` module's Android target pulls it in.

- **Native build:** `cmakeBuild_<abi>` builds the `filament-c-jni` target of [`c/CMakeLists.txt`](../c/CMakeLists.txt)
  with the SDK's NDK and CMake, over upstream's `android-native` prebuilts (`:prebuilts_android-<abi>`). NDK
  29.0.14206865 and CMake 3.22.1 are pinned to upstream's (`build/common/versions`) so the prebuilts' libc++
  matches; install them with `sdkmanager "ndk;29.0.14206865" "cmake;3.22.1"`.
- **Why not `externalNativeBuild`:** AGP's native build needs the SDK and NDK just to configure, which breaks
  every host without them (e.g. the linux-arm64 CI runner). As plain tasks, nothing touches the SDK until an
  Android task runs.
- **Android-only JNI:** `src/androidMain/cpp/FilaAndroid.cpp` + `FilaAndroid.kt` (`Surface` → `ANativeWindow`).
- **Tests:** the `:kotlin:*` modules' `connectedAndroidDeviceTest` run the whole API on a device.
