# Using the Engine Without Compose

`filament-compose` is **optional**. The `filament`, `gltfio`, `filament-utils` and `filamat`
modules are plain Kotlin Multiplatform bindings over the Filament C++ API — no Compose
runtime, no Compose Gradle plugin, no `@Composable` anywhere. Depend on `filament` alone and
you get `Engine`, `Scene`, `View`, `Renderer`, `Camera`, `Material`, `Texture` and the
managers, with the same names and shapes as [Android Filament](https://google.github.io/filament/Filament.md.html).

Use this path when you are:

- rendering into a surface you already own (`SurfaceView`, `CAMetalLayer`, an LWJGL/GLFW window, a `<canvas>`),
- rendering **headless** — thumbnails, product shots, server-side image generation, tests,
- writing an engine/game loop that isn't driven by a UI framework,
- porting existing Android Filament code to other targets.

```kotlin
// build.gradle.kts — no Compose plugin required
commonMain.dependencies {
    implementation("io.github.erkko68.filament:filament:0.4.0")
    implementation("io.github.erkko68.filament:gltfio:0.4.0")        // optional
    implementation("io.github.erkko68.filament:filament-utils:0.4.0") // optional
}
```

See **[Modules](modules.md#dependencies-by-target)** for what each target needs.

## The render loop

Filament's object model is the same everywhere: one `Engine` owns everything, a `Renderer`
draws a `View` (scene + camera + viewport) into a `SwapChain`.

```kotlin
val engine    = Engine.create()
val scene     = engine.createScene()
val camera    = engine.createCamera()
val view      = engine.createView().apply {
    this.scene = scene
    this.camera = camera
    viewport = Viewport(0, 0, width, height)
}
val renderer  = engine.createRenderer()
val swapChain = engine.createSwapChain(NativeSurface(myNativeWindow))

// One frame — call this from your own loop / display link / requestAnimationFrame.
fun frame(frameTimeNanos: Long) {
    if (renderer.beginFrame(swapChain, frameTimeNanos)) {
        renderer.render(view)
        renderer.endFrame()
    }
}
```

Populating the scene is ordinary Filament: build a `Material`, a `VertexBuffer`/`IndexBuffer`,
attach them to an entity with `RenderableManager.Builder`, add the entity to the scene.

```kotlin
val sun = EntityManager.get().create()
LightManager.Builder(LightManager.Type.SUN)
    .direction(0.7f, -0.7f, 0f)
    .intensity(100_000f)
    .castShadows(true)
    .build(engine, sun)
scene.addEntity(sun)
```

## Where the surface comes from

`Engine.createSwapChain(NativeSurface(...))` takes a platform handle. Each target's
`NativeSurface` wraps a different type:

| Target | `NativeSurface` takes | Typical source |
| :--- | :--- | :--- |
| Android | `Surface` | `SurfaceView`'s `SurfaceHolder.surface`, or a `TextureView`'s `SurfaceTexture` |
| iOS (Kotlin/Native) | `COpaquePointer?` | a `CAMetalLayer` you added to your `UIView` |
| JVM / Desktop | `Long` / `MemorySegment` — a raw `HWND`, X11 `Window`, `NSView*` or `CAMetalLayer*` | LWJGL: `glfwGetWin32Window` / `glfwGetX11Window` / `glfwGetCocoaWindow` |
| Web (JS / Wasm) | `HTMLCanvasElement` | `document.getElementById("canvas")` |

Android, iOS and web hand you a first-class surface object, so those are straightforward.
On the **JVM there is no window toolkit in this library** — you bring your own window and
pass its native handle. If you'd rather not, use the headless path below, or let
`filament-compose` own the window.

> [!TIP]
> Every rule in [Threading model](platform-notes.md#threading-model) applies here and is
> now *your* responsibility: create the `Engine` on one thread and call every `engine.*`
> method from that same thread. Compose was doing this for you.

## Headless rendering

The portable non-Compose path: no window at all. Create a sized swap chain with the
`CONFIG_READABLE` flag, render, and read the pixels back. Works on Android, iOS,
macOS/Windows/Linux JVM — everything except web, where `Renderer.readPixels` is a no-op
(see [Platform Notes](platform-notes.md#web--wasm)).

```kotlin
// Filament's SwapChain::CONFIG_READABLE — not yet exposed as a Kotlin constant.
private const val CONFIG_READABLE = 0x2L

val engine = Engine.create()
val swapChain = engine.createSwapChain(width, height, CONFIG_READABLE)
val renderer = engine.createRenderer().apply {
    clearOptions = Renderer.ClearOptions().apply {
        clearColor = doubleArrayOf(0.0, 0.0, 0.0, 1.0)
        clear = true
    }
}
// ... build scene / view / camera as above ...

val pixels = ByteArray(width * height * 4)
var done = false
val pbd = Texture.PixelBufferDescriptor(
    pixels, pixels.size, Texture.Format.RGBA, Texture.Type.UBYTE,
) { done = true }

if (renderer.beginFrame(swapChain, 0L)) {
    renderer.render(view)
    renderer.readPixels(0, 0, width, height, pbd)
    renderer.endFrame()
}
while (!done) engine.flushAndWait()   // readback is asynchronous
```

Two things that bite:

- **Render a few frames before the one you keep.** Shader compilation and the shadow /
  post-processing passes need a frame or two to settle; the very first frame can be blank.
- **Row order is backend-dependent.** Metal delivers rows top-down, OpenGL bottom-up.
  Flip according to `engine.backend` if you're writing a PNG.

The same pattern drives this repo's own rendering tests — see
[`FrameProbe`](../kotlin/filament/src/commonTest/kotlin/io/github/erkko68/filament/testutils/FrameProbe.kt)
for a complete, working implementation you can copy.

## Loading a glTF model

`gltfio` is independent of Compose too:

```kotlin
val provider = UbershaderProvider(engine)
val assetLoader = AssetLoader.create(engine, provider, engine.getEntityManager())
val asset = assetLoader.createAsset(glbBytes)!!

val resourceLoader = ResourceLoader(engine)
resourceLoader.loadResources(asset)      // must run before textures/morph targets exist
scene.addEntities(asset.getEntities())

// Teardown, in this order.
resourceLoader.destroy()
assetLoader.destroyAsset(asset)
AssetLoader.destroy(assetLoader)
provider.destroy()
```

`filament-utils` similarly gives you `Manipulator` (orbit / map / flight camera control),
`KTX1Loader` and `HDRLoader` with no Compose involved.

## Lifecycle

You own every object you create. Destroy in reverse dependency order and only then the
engine, or `engine.destroy()` will panic on live resources:

```kotlin
engine.destroySwapChain(swapChain)
engine.destroyRenderer(renderer)
engine.destroyView(view)
engine.destroyScene(scene)
engine.destroyCamera(camera)
scene.remove(entity); engine.destroyEntity(entity)  // plus buffers, materials, instances
engine.destroy()
```

## Mixing with Compose later

Adopting the Compose DSL afterwards doesn't mean rewriting: `filament-compose` exposes the
raw `Engine` through `FilamentEffect`, so engine-level code keeps working inside a
`FilamentSceneView`. Going the other way, `rememberFilamentEngine` just wraps
`Engine.create()`. See [Compose Integration](compose/README.md).

---

[← Back to docs index](README.md)
