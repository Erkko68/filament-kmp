@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package io.github.erkko68.filament.interop

import io.github.erkko68.filament.Engine
import kotlinx.cinterop.toLong

internal actual val Engine.pointer: NativePointer get() = nativeHandle.toLong()
