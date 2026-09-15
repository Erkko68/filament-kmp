package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.InternalFilamentApi

// Escape hatch: the underlying filamat-android object behind the wrapper, for interop with code
// that talks to Filament directly. Read-only — the wrapper owns the object's lifetime.

@InternalFilamentApi
val MaterialPackage.nativeObject: com.google.android.filament.filamat.MaterialPackage get() = javaPackage
