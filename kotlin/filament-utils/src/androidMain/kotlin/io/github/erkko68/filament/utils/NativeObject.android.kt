package io.github.erkko68.filament.utils

import io.github.erkko68.filament.InternalFilamentApi

// Escape hatch: the underlying filament-android object behind each wrapper, for interop with code
// that talks to Filament directly. Read-only — the wrapper owns the object's lifetime.

@InternalFilamentApi
val Manipulator.nativeObject: com.google.android.filament.utils.Manipulator get() = androidHandle
@InternalFilamentApi
val Manipulator.Bookmark.nativeObject: Any get() = androidValue
