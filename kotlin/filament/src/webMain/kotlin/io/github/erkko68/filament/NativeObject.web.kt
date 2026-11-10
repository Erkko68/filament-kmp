package io.github.erkko68.filament

/** The canvas this engine renders into. */
@InternalFilamentApi
val Engine.canvas: org.w3c.dom.HTMLCanvasElement? get() = (platform as? WebEnginePlatform)?.canvas
/** Whether this engine renders on a WebGL context it adopted from another renderer (e.g. Compose's). */
val Engine.sharesContext: Boolean get() = (platform as? WebEnginePlatform)?.ownsContext == false
/** Runs after each call that executed this engine's GL commands, so a renderer sharing the context can resync GL state. */
@InternalFilamentApi
var Engine.onExecuted: (() -> Unit)?
    get() = (platform as? WebEnginePlatform)?.onExecuted
    set(value) { (platform as? WebEnginePlatform)?.onExecuted = value }
