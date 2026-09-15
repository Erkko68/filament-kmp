package io.github.erkko68.filament.utils

import io.github.erkko68.filament.InternalFilamentApi

// Escape hatch: the underlying FFM handle behind each wrapper, for interop with code
// that talks to Filament directly. Read-only — the wrapper owns the object's lifetime.

@InternalFilamentApi
val Manipulator.Bookmark.nativeObject: java.lang.foreign.MemorySegment get() = nativeHandle
@InternalFilamentApi
val Manipulator.nativeObject: java.lang.foreign.MemorySegment get() = nativeHandle
