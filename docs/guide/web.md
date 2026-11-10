# Rendering into a Canvas (Web)

On web, `NativeSurface(canvas)` turns an `HTMLCanvasElement` into a Filament surface. You get the raw
engine on a canvas of your own, without `filament-compose`. Only the `filament` artifact is needed.

If you are in Compose, `filament-compose`'s `FilamentView` is usually the better fit
([Integration Strategies](../compose/integration-strategies.md)).

## The minimal setup

```kotlin
val canvas = document.getElementById("canvas") as HTMLCanvasElement
val engine = Engine.create(sharedContext = canvas)!!   // the engine's WebGL2 context lives on this canvas
val swapChain = engine.createSwapChain(NativeSurface(canvas))
val renderer = engine.createRenderer()
val view = engine.createView().apply { scene = engine.createScene(); camera = /* … */ }

fun frame(time: Double) {
    view.viewport = Viewport(0, 0, canvas.width, canvas.height)
    if (renderer.beginFrame(swapChain)) {
        renderer.render(view)
        renderer.endFrame()
    }
    window.requestAnimationFrame(::frame)
}
window.requestAnimationFrame(::frame)
```

Without a `sharedContext` the engine makes a hidden canvas of its own, which is only useful headless.

## Rules

**One engine, one canvas.** An engine is bound to a single WebGL2 context. Every swap chain of that
engine renders into the same canvas; for several views, give each a `View.viewport` region of it.

**Canvas size in pixels.** Set `canvas.width`/`height` to the CSS size times `devicePixelRatio`, and
the viewport to match.

**No threads.** The wasm build is single-threaded: Filament runs its GL commands inside
`beginFrame`/`endFrame`, `flush`, `flushAndWait` and `Engine.create`/`destroy`, on the calling task.
The limits that follow are in [Platform Notes](platform-notes.md#web--wasm).

---

[← Back to docs index](../README.md)
