package io.github.erkko68.filament

// Escape hatch: the wasm heap address behind each wrapper, for interop with code that calls
// the Fila* externals (io.github.erkko68.filament.wasm) directly. Read-only — the wrapper owns
// the object's lifetime.

@InternalFilamentApi
val Engine.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val LightManager.nativeObject: Int get() = nativeLightManager
@InternalFilamentApi
val Renderer.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val View.nativeObject: Int get() = nativeHandle

/** The canvas this engine renders into. */
@InternalFilamentApi
val Engine.canvas: org.w3c.dom.HTMLCanvasElement? get() = jsCanvas
