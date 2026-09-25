package io.github.erkko68.filament

// Escape hatch: the wasm heap address behind each wrapper, for interop with code that calls
// the Fila* externals (io.github.erkko68.filament.wasm) directly. Read-only — the wrapper owns
// the object's lifetime.

@InternalFilamentApi
val BufferObject.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val Camera.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val ColorGrading.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val Engine.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val EntityManager.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val Fence.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val IndexBuffer.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val IndirectLight.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val LightManager.nativeObject: Int get() = nativeLightManager
@InternalFilamentApi
val Material.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val MaterialInstance.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val MorphTargetBuffer.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val RenderTarget.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val RenderableManager.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val Renderer.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val Scene.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val SkinningBuffer.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val Skybox.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val Stream.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val SurfaceOrientation.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val SwapChain.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val Texture.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val TransformManager.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val VertexBuffer.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val View.nativeObject: Int get() = nativeHandle

/** The canvas this engine renders into. */
@InternalFilamentApi
val Engine.canvas: org.w3c.dom.HTMLCanvasElement? get() = jsCanvas
