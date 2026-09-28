package io.github.erkko68.filament.interop

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture

internal actual val Engine.pointer: NativePointer get() = nativeHandle
internal actual val Texture.pointer: NativePointer get() = nativeHandle
internal actual fun textureOf(ptr: NativePointer): Texture = Texture(ptr)
