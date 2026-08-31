package io.github.erkko68.filament

// Escape hatch: the underlying filament-android object behind each wrapper, for interop with code
// that talks to Filament directly. Read-only — the wrapper owns the object's lifetime.

@InternalFilamentApi
val BufferObject.nativeObject: com.google.android.filament.BufferObject get() = nativeBufferObject
@InternalFilamentApi
val Camera.nativeObject: com.google.android.filament.Camera get() = nativeCamera
@InternalFilamentApi
val ColorGrading.nativeObject: com.google.android.filament.ColorGrading get() = nativeColorGrading
@InternalFilamentApi
val Engine.nativeObject: com.google.android.filament.Engine get() = nativeEngine
@InternalFilamentApi
val EntityManager.nativeObject: com.google.android.filament.EntityManager get() = nativeEntityManager
@InternalFilamentApi
val Fence.nativeObject: com.google.android.filament.Fence get() = nativeFence
@InternalFilamentApi
val IndexBuffer.nativeObject: com.google.android.filament.IndexBuffer get() = nativeIndexBuffer
@InternalFilamentApi
val IndirectLight.nativeObject: com.google.android.filament.IndirectLight get() = nativeIndirectLight
@InternalFilamentApi
val LightManager.nativeObject: com.google.android.filament.LightManager get() = nativeLightManager
@InternalFilamentApi
val Material.nativeObject: com.google.android.filament.Material get() = nativeMaterial
@InternalFilamentApi
val MaterialInstance.nativeObject: com.google.android.filament.MaterialInstance get() = nativeMaterialInstance
@InternalFilamentApi
val MorphTargetBuffer.nativeObject: com.google.android.filament.MorphTargetBuffer get() = nativeMorphTargetBuffer
@InternalFilamentApi
val RenderTarget.nativeObject: com.google.android.filament.RenderTarget get() = nativeRenderTarget
@InternalFilamentApi
val RenderableManager.nativeObject: com.google.android.filament.RenderableManager get() = nativeRenderableManager
@InternalFilamentApi
val Renderer.nativeObject: com.google.android.filament.Renderer get() = nativeRenderer
@InternalFilamentApi
val Scene.nativeObject: com.google.android.filament.Scene get() = nativeScene
@InternalFilamentApi
val SkinningBuffer.nativeObject: com.google.android.filament.SkinningBuffer get() = nativeSkinningBuffer
@InternalFilamentApi
val Skybox.nativeObject: com.google.android.filament.Skybox get() = nativeSkybox
@InternalFilamentApi
val Stream.nativeObject: com.google.android.filament.Stream get() = nativeStream
@InternalFilamentApi
val SurfaceOrientation.nativeObject: com.google.android.filament.SurfaceOrientation get() = nativeSurfaceOrientation
@InternalFilamentApi
val SwapChain.nativeObject: com.google.android.filament.SwapChain get() = nativeSwapChain
@InternalFilamentApi
val Texture.nativeObject: com.google.android.filament.Texture get() = nativeTexture
@InternalFilamentApi
val TransformManager.nativeObject: com.google.android.filament.TransformManager get() = nativeTransformManager
@InternalFilamentApi
val VertexBuffer.nativeObject: com.google.android.filament.VertexBuffer get() = nativeVertexBuffer
@InternalFilamentApi
val View.nativeObject: com.google.android.filament.View get() = nativeView
