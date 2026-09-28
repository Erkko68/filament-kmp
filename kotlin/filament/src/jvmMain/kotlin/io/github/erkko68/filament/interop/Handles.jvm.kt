package io.github.erkko68.filament.interop

import io.github.erkko68.filament.IndirectLight
import io.github.erkko68.filament.Skybox

internal actual val Skybox.pointer: NativePointer get() = nativeHandle?.address() ?: 0L
internal actual val IndirectLight.pointer: NativePointer get() = nativeHandle?.address() ?: 0L
