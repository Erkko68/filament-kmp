@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament

// Escape hatch: the underlying cinterop pointer behind each wrapper, for interop with code
// that talks to Filament directly. Read-only — the wrapper owns the object's lifetime.

@InternalFilamentApi
val ColorGrading.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaColorGrading>? get() = nativeHandle
@InternalFilamentApi
val Engine.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaEngine>? get() = nativeHandle
@InternalFilamentApi
val LightManager.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaLightManager> get() = nativeLightManager
@InternalFilamentApi
val Material.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaMaterial>? get() = nativeHandle
@InternalFilamentApi
val MaterialInstance.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaMaterialInstance>? get() = nativeHandle
@InternalFilamentApi
val RenderableManager.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaRenderableManager> get() = nativeHandle
@InternalFilamentApi
val Renderer.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaRenderer>? get() = nativeHandle
@InternalFilamentApi
val SwapChain.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaSwapChain>? get() = nativeHandle
@InternalFilamentApi
val View.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaView>? get() = nativeHandle
