@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament

// Escape hatch: the underlying cinterop pointer behind each wrapper, for interop with code
// that talks to Filament directly. Read-only — the wrapper owns the object's lifetime.

@InternalFilamentApi
val Engine.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaEngine>? get() = nativeHandle
@InternalFilamentApi
val LightManager.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaLightManager> get() = nativeLightManager
@InternalFilamentApi
val Renderer.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaRenderer>? get() = nativeHandle
@InternalFilamentApi
val View.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaView>? get() = nativeHandle
