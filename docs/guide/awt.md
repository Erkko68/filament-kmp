# Rendering into an AWT Window (JVM)

On the JVM, `NativeSurface(canvas)` turns a `java.awt.Canvas` into a Filament surface. You get the
raw engine in an AWT or Swing window, without `filament-compose` and without a third-party window
toolkit. Only the `filament` artifact is needed.

The runnable version of this page is the [`awtApp` sample](../../samples/awtApp/src/jvmMain/kotlin/eric/bitria/samples/awt/Main.kt).

## Platform support

| OS | Backend | Native window behind the canvas | Status |
| :--- | :--- | :--- | :--- |
| Windows | Vulkan (default) | `HWND` | Works |
| Windows | OpenGL | `HWND` | **Open problem**: hangs, see [below](#known-issue-opengl-on-windows) |
| macOS | Metal (default, the only one) | a `CAMetalLayer` attached to the canvas | Not yet verified |
| Linux | Vulkan (default) / OpenGL | X11 `Window` | Not yet verified |

Use Filament's default backend (`Engine.Backend.DEFAULT`) unless you have a reason not to.

## The minimal setup

```kotlin
EventQueue.invokeLater {
    val engine = Engine.create()!!                  // on the thread that will drive it
    val canvas = Canvas().apply { preferredSize = Dimension(800, 600) }
    val frame = Frame("Filament").apply { add(canvas); pack(); isVisible = true }

    // The canvas has a native window only once its frame is showing.
    val swapChain = engine.createSwapChain(NativeSurface(canvas))
    val renderer = engine.createRenderer()
    val view = engine.createView().apply { scene = engine.createScene(); camera = /* … */ }

    Timer(16) {
        val scale = canvas.graphicsConfiguration.defaultTransform
        view.viewport = Viewport(0, 0, (canvas.width * scale.scaleX).roundToInt(), (canvas.height * scale.scaleY).roundToInt())
        if (renderer.beginFrame(swapChain)) {
            renderer.render(view)
            renderer.endFrame()
        }
    }.start()
}
```

The rest of the engine (scenes, materials, glTF, cameras) works the same as on any other surface.
See [Using the Engine Without Compose](engine.md).

## Rules

**Threading.** Filament objects belong to the thread that created the `Engine`
([Threading model](platform-notes.md#threading-model)). In an AWT app the natural choice is the
event dispatch thread. Create the engine in `EventQueue.invokeLater` and drive frames from a
`javax.swing.Timer`, which fires on that thread. A dedicated render thread also works, as long as
every Filament call happens on it and it reads the canvas size safely.

**Displayable first.** `NativeSurface(canvas)` throws if the canvas has no native window yet. Add it
to a frame and make the frame visible first.

**Viewport in pixels.** `canvas.width`/`height` are in AWT's logical units. Multiply them by
`canvas.graphicsConfiguration.defaultTransform` (2 on Retina, 1.25 at 125 % on Windows) to get pixels.
Recompute the viewport whenever the canvas is resized or moves to a screen with another scale.
Doing it every frame is cheap. The swap chain follows the window's size by itself.

**Teardown order.** Destroy the swap chain, and `engine.flushAndWait()`, before the canvas loses its
native window: before `frame.dispose()`, or before removing the canvas from its parent.

```kotlin
override fun windowClosing(e: WindowEvent) {
    timer.stop()
    engine.destroy(swapChain)
    engine.flushAndWait()
    frame.dispose()
    // …then the renderer, view, scene, and Engine.destroy(engine)
}
```

**Swing and Compose.** A heavyweight `Canvas` can sit inside a Swing `JFrame` or `JPanel`, or inside
Compose Desktop through `SwingPanel`. If you are already in Compose, `filament-compose`'s
`FilamentView` is usually the better fit, see [Integration Strategies](../compose/integration-strategies.md).

## Per-platform details

- **Windows.** The canvas's `HWND` goes to Filament as is.
- **Linux.** The canvas's X11 `Window` goes to Filament. AWT runs on X11, or on XWayland in a Wayland
  session.
- **macOS.** AWT only gives out a slot for a `CALayer`. Filament KMP puts a `CAMetalLayer` in it, sizes
  its drawable from the layer's bounds and the screen's scale, and hands that layer to Filament.
  Only the Metal backend can render into it.

## Known issue: OpenGL on Windows

With `Engine.Backend.OPENGL` on Windows, rendering hangs. The first frame goes through, then
Filament's driver thread blocks and the next `flushAndWait()` never returns. The window stays white.
The same hang happens with a headless swap chain (`createSwapChain(width, height)`), so it is not
specific to AWT. Turning off Java2D's Direct3D pipeline (`-Dsun.java2d.d3d=false`) doesn't change it.
Seen on a laptop with an AMD Radeon integrated GPU and an NVIDIA RTX 3050. The cause is still open.

**Use Vulkan on Windows**, Filament's default there.

## Running the sample

```shell
cd samples
./gradlew :awtApp:run                  # the platform's default backend
./gradlew :awtApp:run --args=opengl    # or force one: opengl, vulkan, metal
```

The view is filled by a skybox whose colour cycles over a grey clear colour. If the viewport stops
covering the canvas after a resize or a move to another display, a grey border shows. The title bar
shows the backend and the viewport size in pixels.

---

[← Back to docs index](../README.md)
