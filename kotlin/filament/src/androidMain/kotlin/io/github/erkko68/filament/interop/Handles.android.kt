package io.github.erkko68.filament.interop

import io.github.erkko68.filament.IndirectLight
import io.github.erkko68.filament.Skybox

internal actual val Skybox.pointer: NativePointer get() = nativeHandle
internal actual val IndirectLight.pointer: NativePointer get() = nativeHandle
