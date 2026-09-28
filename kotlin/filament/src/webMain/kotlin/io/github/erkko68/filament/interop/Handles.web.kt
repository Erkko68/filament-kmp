package io.github.erkko68.filament.interop

import io.github.erkko68.filament.Engine

internal actual val Engine.pointer: NativePointer get() = nativeHandle
