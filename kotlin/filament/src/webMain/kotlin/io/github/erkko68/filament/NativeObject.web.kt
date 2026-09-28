package io.github.erkko68.filament

/** The canvas this engine renders into. */
@InternalFilamentApi
val Engine.canvas: org.w3c.dom.HTMLCanvasElement? get() = (platform as? WebEnginePlatform)?.canvas
