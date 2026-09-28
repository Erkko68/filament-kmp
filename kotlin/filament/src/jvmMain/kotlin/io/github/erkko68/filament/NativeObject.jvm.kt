package io.github.erkko68.filament

// Escape hatch: the underlying FFM handle behind each wrapper, for interop with code
// that talks to Filament directly. Read-only — the wrapper owns the object's lifetime.

@InternalFilamentApi
val ColorGrading.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val Engine.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val LightManager.nativeObject: java.lang.foreign.MemorySegment get() = nativeLightManager
@InternalFilamentApi
val Material.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val MaterialInstance.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val RenderableManager.nativeObject: java.lang.foreign.MemorySegment get() = nativeHandle
@InternalFilamentApi
val Renderer.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val SwapChain.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val View.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
