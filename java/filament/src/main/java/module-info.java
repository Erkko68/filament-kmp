/**
 * Combined JNI bindings for Google Filament — exposes the renderer surface plus the
 * native loader used by the sibling filamat / gltfio / utils modules. The actual native
 * library (libfilament-jni) is loaded by {@code NativeLoader.load("filament-jni")}.
 *
 * Consumers running on the module path can grant native access narrowly with:
 *     --enable-native-access=io.github.erkko68.filament.jni
 * Classpath consumers must still use {@code --enable-native-access=ALL-UNNAMED}.
 */
module io.github.erkko68.filament.jni {
    exports io.github.erkko68.filament.jni;
    exports io.github.erkko68.filament.jni.internal;
    exports io.github.erkko68.filament.jni.proguard;

    requires static org.jetbrains.annotations;
}
