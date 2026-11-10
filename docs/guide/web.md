# Rendering into a Canvas (Web)

On web, `NativeSurface(canvas)` turns an `HTMLCanvasElement` into a Filament surface. You get the raw
engine on a canvas of your own, without `filament-compose`. Only the `filament` artifact is needed.

`filament-compose` doesn't work this way: its `FilamentView` draws on Compose's own WebGL context
([Integration Strategies](../compose/integration-strategies.md)). Use this page when you want Filament
outside Compose, or next to another WebGL renderer.

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

## Sharing another renderer's context

Pass a live `WebGL2RenderingContext` instead of a canvas and the engine adopts it rather than creating
one. This is how `filament-compose` puts Filament inside Compose's canvas.

```kotlin
val engine = Engine.create(sharedContext = gl)!!   // gl: the other renderer's WebGL2RenderingContext
```

Filament and the other renderer each cache the context's GL state, so every hand-over needs a resync:

- **Before Filament runs again**, call `engine.resetBackendState()`. It is queued, so call it right
  after your Filament frame: it then runs first next time, ahead of anything queued in between.
- **After Filament runs**, tell the other renderer its cached state is stale (Skia:
  `DirectContext.resetAll()`), and rebind its framebuffer.
- **To render into the other renderer's texture**, register it with `fila.registerGlTexture(texture)`
  and import the id with `Texture.Builder().import(id)`, then attach it to a `RenderTarget`.

The engine never loses or deletes an adopted context; destroying the engine only lets go of it.

---

[← Back to docs index](../README.md)
