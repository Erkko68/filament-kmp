@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament

// Escape hatch: the underlying cinterop pointer behind each wrapper, for interop with code
// that talks to Filament directly. Read-only — the wrapper owns the object's lifetime.

@InternalFilamentApi
val BufferObject.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaBufferObject>? get() = nativeHandle
@InternalFilamentApi
val Camera.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaCamera>? get() = nativeHandle
@InternalFilamentApi
val ColorGrading.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaColorGrading>? get() = nativeHandle
@InternalFilamentApi
val Engine.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaEngine>? get() = nativeHandle
@InternalFilamentApi
val EntityManager.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaEntityManager>? get() = nativeHandle
@InternalFilamentApi
val Fence.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaFence>? get() = nativeHandle
@InternalFilamentApi
val IndexBuffer.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaIndexBuffer>? get() = nativeHandle
@InternalFilamentApi
val IndirectLight.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaIndirectLight>? get() = nativeHandle
@InternalFilamentApi
val LightManager.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaLightManager> get() = nativeLightManager
@InternalFilamentApi
val Material.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaMaterial>? get() = nativeHandle
@InternalFilamentApi
val MaterialInstance.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaMaterialInstance>? get() = nativeHandle
@InternalFilamentApi
val MorphTargetBuffer.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaMorphTargetBuffer>? get() = nativeHandle
@InternalFilamentApi
val RenderTarget.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaRenderTarget>? get() = nativeHandle
@InternalFilamentApi
val RenderableManager.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaRenderableManager> get() = nativeHandle
@InternalFilamentApi
val Renderer.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaRenderer>? get() = nativeHandle
@InternalFilamentApi
val Scene.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaScene>? get() = nativeHandle
@InternalFilamentApi
val SkinningBuffer.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaSkinningBuffer>? get() = nativeHandle
@InternalFilamentApi
val Skybox.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaSkybox>? get() = nativeHandle
@InternalFilamentApi
val Stream.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaStream>? get() = nativeHandle
@InternalFilamentApi
val SurfaceOrientation.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaSurfaceOrientation> get() = nativeHandle
@InternalFilamentApi
val SwapChain.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaSwapChain>? get() = nativeHandle
@InternalFilamentApi
val Texture.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaTexture>? get() = nativeHandle
@InternalFilamentApi
val TransformManager.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaTransformManager>? get() = nativeHandle
@InternalFilamentApi
val VertexBuffer.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaVertexBuffer>? get() = nativeHandle
@InternalFilamentApi
val View.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaView>? get() = nativeHandle
