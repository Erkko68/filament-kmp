@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.InternalFilamentApi

// Escape hatch: the underlying cinterop pointer behind each wrapper, for interop with code
// that talks to Filament directly. Read-only — the wrapper owns the object's lifetime.

@InternalFilamentApi
val MaterialPackage.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaPackage>? get() = nativeHandle
