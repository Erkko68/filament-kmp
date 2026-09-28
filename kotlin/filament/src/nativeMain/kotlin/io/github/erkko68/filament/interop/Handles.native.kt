@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package io.github.erkko68.filament.interop

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture
import kotlinx.cinterop.toCPointer
import kotlinx.cinterop.toLong

internal actual val Engine.pointer: NativePointer get() = nativeHandle.toLong()
internal actual val Texture.pointer: NativePointer get() = nativeHandle.toLong()
internal actual fun textureOf(ptr: NativePointer): Texture = Texture(ptr.toCPointer())
