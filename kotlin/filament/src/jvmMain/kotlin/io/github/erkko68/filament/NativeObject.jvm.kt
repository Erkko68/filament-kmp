package io.github.erkko68.filament

// Escape hatch: the underlying FFM handle behind each wrapper, for interop with code
// that talks to Filament directly. Read-only — the wrapper owns the object's lifetime.

@InternalFilamentApi
val BufferObject.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val Camera.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val ColorGrading.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val Engine.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val EntityManager.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val Fence.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val IndexBuffer.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val IndirectLight.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val LightManager.nativeObject: java.lang.foreign.MemorySegment get() = nativeLightManager
@InternalFilamentApi
val Material.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val MaterialInstance.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val MorphTargetBuffer.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val RenderTarget.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val RenderableManager.nativeObject: java.lang.foreign.MemorySegment get() = nativeHandle
@InternalFilamentApi
val Renderer.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val Scene.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val SkinningBuffer.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val Skybox.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val Stream.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val SurfaceOrientation.nativeObject: java.lang.foreign.MemorySegment get() = nativeHandle
@InternalFilamentApi
val SwapChain.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val Texture.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val TransformManager.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val VertexBuffer.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val View.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
