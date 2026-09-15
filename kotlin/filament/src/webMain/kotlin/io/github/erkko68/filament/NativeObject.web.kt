package io.github.erkko68.filament

// Escape hatch: the underlying Filament.js object behind each wrapper, for interop with code
// that talks to Filament directly. Read-only — the wrapper owns the object's lifetime.

@InternalFilamentApi
val BufferObject.nativeObject: io.github.erkko68.filament.web.BufferObject get() = jsBufferObject
@InternalFilamentApi
val Camera.nativeObject: io.github.erkko68.filament.web.Camera get() = jsCamera
@InternalFilamentApi
val ColorGrading.nativeObject: io.github.erkko68.filament.web.ColorGrading get() = jsColorGrading
@InternalFilamentApi
val Engine.nativeObject: io.github.erkko68.filament.web.Engine get() = jsEngine
@InternalFilamentApi
val EntityManager.nativeObject: io.github.erkko68.filament.web.EntityManager get() = jsEntityManager
@InternalFilamentApi
val Fence.nativeObject: io.github.erkko68.filament.web.Fence get() = jsFence
@InternalFilamentApi
val IndexBuffer.nativeObject: io.github.erkko68.filament.web.IndexBuffer get() = jsIndexBuffer
@InternalFilamentApi
val IndirectLight.nativeObject: io.github.erkko68.filament.web.IndirectLight get() = jsIndirectLight
@InternalFilamentApi
val LightManager.nativeObject: io.github.erkko68.filament.web.LightManager get() = jsLightManager
@InternalFilamentApi
val Material.nativeObject: io.github.erkko68.filament.web.Material get() = jsMaterial
@InternalFilamentApi
val MaterialInstance.nativeObject: io.github.erkko68.filament.web.MaterialInstance get() = jsMaterialInstance
@InternalFilamentApi
val MorphTargetBuffer.nativeObject: io.github.erkko68.filament.web.MorphTargetBuffer get() = jsMorphTargetBuffer
@InternalFilamentApi
val RenderTarget.nativeObject: io.github.erkko68.filament.web.RenderTarget get() = jsRenderTarget
@InternalFilamentApi
val RenderableManager.nativeObject: io.github.erkko68.filament.web.RenderableManager get() = jsRenderableManager
@InternalFilamentApi
val Renderer.nativeObject: io.github.erkko68.filament.web.Renderer get() = jsRenderer
@InternalFilamentApi
val Scene.nativeObject: io.github.erkko68.filament.web.Scene get() = jsScene
@InternalFilamentApi
val SkinningBuffer.nativeObject: io.github.erkko68.filament.web.SkinningBuffer get() = jsSkinningBuffer
@InternalFilamentApi
val Skybox.nativeObject: io.github.erkko68.filament.web.Skybox get() = jsSkybox
@InternalFilamentApi
val Stream.nativeObject: Any? get() = jsStream
@InternalFilamentApi
val SurfaceOrientation.nativeObject: io.github.erkko68.filament.web.SurfaceOrientation get() = jsSurfaceOrientation
@InternalFilamentApi
val SwapChain.nativeObject: io.github.erkko68.filament.web.SwapChain get() = jsSwapChain
@InternalFilamentApi
val Texture.nativeObject: io.github.erkko68.filament.web.Texture get() = jsTexture
@InternalFilamentApi
val TransformManager.nativeObject: io.github.erkko68.filament.web.TransformManager get() = jsTransformManager
@InternalFilamentApi
val VertexBuffer.nativeObject: io.github.erkko68.filament.web.VertexBuffer get() = jsVertexBuffer
@InternalFilamentApi
val View.nativeObject: io.github.erkko68.filament.web.View get() = jsView

/** The canvas this engine renders into, when it owns one. */
@InternalFilamentApi
val Engine.canvas: org.w3c.dom.HTMLCanvasElement? get() = jsCanvas
