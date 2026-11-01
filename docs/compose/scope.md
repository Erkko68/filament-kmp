# Filament Compose: Scope & Philosophy

The `filament-compose` module is designed to bridge the gap between the low-level, imperative Filament API and the high-level, declarative world of Compose Multiplatform.

## Core Philosophy

### 1. Simplify the 80%
The primary goal of `filament-compose` is to provide a subset of high-level utilities and Composables that cover the most common 3D rendering use cases (loading models, setting up lights, managing cameras) with minimal boilerplate.

### 2. Full Interoperability (The Escape Hatch)
While high-level abstractions are provided, the goal is never to "hide" Filament. `filament-compose` is built to allow full interoperability with the underlying Filament API. 

If the provided Composables are not sufficient for a specific use case, the raw `Engine`, `Scene`, and `View` objects remain accessible for custom low-level operations.

## Key Goals
- **Declarative Scene Graph**: The 3D scene is defined using standard Compose patterns.
- **Resource Management**: The lifecycle of Filament objects (Engine, Scene, Renderer) is automatically handled in sync with the Composable lifecycle.
- **Multiplatform by Default**: The same Compose code works across Android, iOS, Desktop, and Web. Web's remaining limits come from WebGL and single-threaded wasm. See [Platform Notes — Web](../guide/platform-notes.md#web--wasm) for details.
- **Performance**: Platform-specific integration paths are optimized per target (native swap-chain on Android/iOS, CPU readback or experimental GPU-to-GPU sharing on Desktop) while maintaining a consistent API. See [Platform Notes](../guide/platform-notes.md) for per-platform details, and [Performance limits](#performance-limits) for what the DSL can't do.

## What it is NOT
- **A Full Game Engine**: This project does not aim to replace engines like Unity or Unreal. The focus is on high-quality 3D rendering within UI applications.
- **An Abstraction Layer over Filament**: No attempt is made to create a "generic" 3D API. Using `filament-compose` means using Filament directly.
- **The Fastest Way to Use Filament**: The DSL puts convenience first, and heavy loads stall the UI while they run. See [Performance limits](#performance-limits).

## Performance limits

Composition runs on the main thread and the DSL makes its Filament calls there, so loading blocks the UI. Rendering doesn't: a frame costs the main thread about 1 ms, even with 1,000 shadowed cubes on a phone.

These are from a stress test on Filament 1.79.0: a 2M-triangle glTF, an animated model, a 2K HDR environment and four 2K PNGs, all reloaded together.

| Device | Worst UI gap | Frames over 100 ms |
|:--|--:|--:|
| Galaxy S23 (OpenGL, real `FilamentView`) | 207-236 ms | 5-6 per run |
| Apple M4 (Metal, `ComposeWindow`) | 37 ms | 0 |

Smaller scenes stall less, though a typical app wasn't measured. Only a flagship phone was tested, so a mid-range one will stall for longer. Image decoding already runs off the main thread. What's left on the Galaxy S23, per call:

| Call | ms |
|:--|--:|
| `Engine.create`, first | 22 |
| `Material.build`, Lit, first | 50 |
| `createAsset`, 47 MB glb | 37-38 |
| `asyncBeginLoad`, 2M triangles | 50-64 |
| `asyncBeginLoad`, 50 parts of 180k triangles | 85-91 |
| `destroy(Renderer)`, backend busy | 221 |
| `destroy(Renderer)`, backend idle | 0.27 |

### Avoiding the stalls

- **Load ahead of the screen.** Hoist the engine and loaders above your navigation, so a transition doesn't build them. A first material build is also paid only once this way.

  ```kotlin
  val engine = rememberFilamentEngine()
  val model = rememberGltfAsset(engine = engine) { Res.readBytes("files/model.glb") }
  ```

- **Keep what you can.** `key()` around a loader destroys and rebuilds everything in it. Change values by recomposing instead.
- **Prefer KTX to HDR environments.** `rememberKtxEnvironment` uses textures `cmgen` prefiltered offline. Each `rememberHdrEnvironment` owns a `Renderer`, and that's the expensive destroy in the table above.
- **Use smaller assets.** `asyncBeginLoad` is one native call, and its cost grows with triangles and parts.
- **Read very large files off the main thread** ([Materials](materials.md)).

### When to leave Compose

If dropped frames during loading matter more to your app than the DSL's convenience, this module is not the right tool. Use the [base libraries](../guide/engine.md) instead: they have no Compose dependency, and you can run Filament on a thread of your own, so loading never touches the UI. The [threading rules](../guide/platform-notes.md#threading-model) tell you what that thread must do.

### Why Filament stays on the main thread

Moving all of Filament off the main thread was prototyped: the stress test's worst gap fell from 156-245 ms to 20-36 ms on a Galaxy S23 (on a headless swap chain, so these differ from the table above) and from 37 ms to 18 ms on an M4. We decided against it, because every way of doing it costs too much for what it buys:

| Design | Why not |
|:--|:--|
| Scene composition on the Filament thread | The prototype above, and the least code. It isn't safe: tracked lambdas cross between two `Recomposer`s, which lost updates and raced in Compose's internals. |
| A lock around Filament calls | A long load holds the lock, so the main thread still waits. Calls that need the engine's creating thread would still abort. |
| One serial queue per engine | Workable, but every effect and loader in the module has to go through it, and each desktop GPU target and iOS needs its own rework. Hooks such as `FilamentEffect`, `onCreate` and `configure` would leave the main thread. |

The queue would also take away what this module promises. A raw Filament call on main, through `viewState.view` or `rememberFilamentEngine()`, would race the Filament thread and crash, and the compiler can't catch that. That breaks the [escape hatch](#2-full-interoperability-the-escape-hatch), so it could only ship as an opt-in. And it still wouldn't remove the first `Engine.create` or material build, since those calls must return an object, and it can't help web at all. It would add a whole threading model to the module to fix loading alone, and an app that needs that is better off with the base libraries.
