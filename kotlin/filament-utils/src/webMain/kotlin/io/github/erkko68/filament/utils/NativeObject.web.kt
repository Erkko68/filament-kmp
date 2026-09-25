package io.github.erkko68.filament.utils

import io.github.erkko68.filament.InternalFilamentApi

// Escape hatch: the underlying cinterop pointer behind each wrapper, for interop with code
// that talks to Filament directly. Read-only — the wrapper owns the object's lifetime.

@InternalFilamentApi
val Manipulator.Bookmark.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val Manipulator.nativeObject: Int get() = nativeHandle
