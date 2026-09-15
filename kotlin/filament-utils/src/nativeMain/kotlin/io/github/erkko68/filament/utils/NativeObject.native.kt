@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament.utils

import io.github.erkko68.filament.InternalFilamentApi

// Escape hatch: the underlying cinterop pointer behind each wrapper, for interop with code
// that talks to Filament directly. Read-only — the wrapper owns the object's lifetime.

@InternalFilamentApi
val Manipulator.Bookmark.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaBookmark> get() = nativeHandle
@InternalFilamentApi
val Manipulator.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaManipulator> get() = nativeHandle
