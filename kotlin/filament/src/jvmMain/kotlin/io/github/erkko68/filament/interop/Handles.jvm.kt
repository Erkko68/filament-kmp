package io.github.erkko68.filament.interop

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture
import java.lang.foreign.MemorySegment

internal actual val Engine.pointer: NativePointer get() = nativeHandle?.address() ?: 0L
internal actual val Texture.pointer: NativePointer get() = nativeHandle?.address() ?: 0L
internal actual fun textureOf(ptr: NativePointer): Texture = Texture(MemorySegment.ofAddress(ptr))
