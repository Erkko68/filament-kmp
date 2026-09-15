package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.InternalFilamentApi

// Escape hatch: the underlying FFM handle behind each wrapper, for interop with code
// that talks to Filament directly. Read-only — the wrapper owns the object's lifetime.

@InternalFilamentApi
val MaterialPackage.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
