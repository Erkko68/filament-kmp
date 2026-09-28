@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package io.github.erkko68.filament.interop

import io.github.erkko68.filament.IndirectLight
import io.github.erkko68.filament.Skybox
import kotlinx.cinterop.toLong

internal actual val Skybox.pointer: NativePointer get() = nativeHandle.toLong()
internal actual val IndirectLight.pointer: NativePointer get() = nativeHandle.toLong()
