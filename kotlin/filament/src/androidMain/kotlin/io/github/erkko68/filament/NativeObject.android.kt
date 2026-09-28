package io.github.erkko68.filament

// Escape hatch: the native address behind each wrapper, for interop with code that calls
// the generated Fila* JNI functions (io.github.erkko68.filament.jni) directly. Read-only — the wrapper owns
// the object's lifetime.

@InternalFilamentApi
val BufferObject.nativeObject: Long get() = nativeHandle
@InternalFilamentApi
val ColorGrading.nativeObject: Long get() = nativeHandle
@InternalFilamentApi
val Engine.nativeObject: Long get() = nativeHandle
@InternalFilamentApi
val IndexBuffer.nativeObject: Long get() = nativeHandle
@InternalFilamentApi
val LightManager.nativeObject: Long get() = nativeLightManager
@InternalFilamentApi
val Material.nativeObject: Long get() = nativeHandle
@InternalFilamentApi
val MaterialInstance.nativeObject: Long get() = nativeHandle
@InternalFilamentApi
val RenderableManager.nativeObject: Long get() = nativeHandle
@InternalFilamentApi
val Renderer.nativeObject: Long get() = nativeHandle
@InternalFilamentApi
val Texture.nativeObject: Long get() = nativeHandle
@InternalFilamentApi
val VertexBuffer.nativeObject: Long get() = nativeHandle
@InternalFilamentApi
val View.nativeObject: Long get() = nativeHandle
