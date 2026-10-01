# Migrating from 0.6.0

This release changes how the library works underneath and, as a result, part of its API.

- **One C API on every platform.** Android, iOS, desktop and web now all call the same `Fila*` C API,
  generated from Filament's C++ headers. Android no longer uses upstream's `filament-android` AARs, web no
  longer uses upstream's embind `filament.js`, and desktop moved from Project Panama (FFM) to JNI.
- **The Kotlin API follows Filament's C++ API**, not the Android Java API: C++'s class names, method names,
  owners, overloads and default values, on every platform. Kotlin properties still replace get/set pairs.

The Filament version is unchanged (1.77.1), so your `.filamat` files keep working.

This guide is the full list of changes in 0.7.0; the [changelog](../../CHANGELOG.md) has the summary.

## What you need to do

| You use | Steps |
| :--- | :--- |
| `filament-compose` only | [Setup](#1-setup) for your platforms, then [behavior changes](#4-behavior-changes-that-still-compile). Raw-API code inside `FilamentEffect { }` also needs [the renames](#3-api-renames). |
| `filament`, `gltfio`, `filament-utils`, `filamat` directly | All of it. Most renames are mechanical: the compiler finds every one. |

> [!TIP]
> **Rule of thumb for anything not listed here:** look the class up in Filament's C++ header
> (`filament/View.h`, `gltfio/AssetLoader.h`, …). The Kotlin name is the C++ name, and the Kotlin member
> lives on the class that declares it in C++.

## 1. Setup

### Toolchain

`filament-compose` now builds on **Compose Multiplatform 1.12.1** and **Kotlin 2.4.20**; consumers resolve
Compose 1.12.1 transitively (skiko stays 0.150.1).

### Web

`filament.js` / `filament.wasm` (upstream's embind build) are replaced by our own runtime, downloaded from
the [GitHub release](https://github.com/Erkko68/filament-kmp/releases) matching your filament-kmp version:

| Remove | Add to `src/webMain/resources/` |
| :--- | :--- |
| `filament.js`, `filament.wasm` | `filament-kmp.js`, `filament-kmp.wasm` |
| — | `filamat-kmp.js`, `filamat-kmp.wasm`, only if you compile materials at runtime |

Update the `<script>` tag in `index.html` to `filament-kmp.js`. The `downloadPrebuilts_web` task and the
`io.github.erkko68.filament.web` externals are gone. See [Getting Started → Web](../guide/getting-started.md#web--wasm).

### Desktop / JVM

- **JDK 17+** is enough now (was 22+).
- The runtime moved from group `io.github.erkko68.filament-ffm` to `io.github.erkko68.filament`. It still
  comes in transitively; only change it if you depend on a per-platform runtime directly:

  | 0.6.0 | Now |
  | :--- | :--- |
  | `io.github.erkko68.filament-ffm:filament-ffm` | `io.github.erkko68.filament:filament-jni-desktop` |
  | `io.github.erkko68.filament-ffm:filament-ffm-runtime-<os>-<arch>` | `io.github.erkko68.filament:filament-jni-runtime-<os>-<arch>` |

- `NativeSurface` takes the native window handle as a `Long` (was a `MemorySegment`).
- New runtime: `windows-arm64`. The natives are cached under `~/.filament-kmp/`; see
  [`desktop/README.md`](../../desktop/README.md) for the loader's properties.

### Android

Nothing to change in Gradle: `filament-jni-android` replaces the upstream AARs transitively. If you mixed in
upstream's `com.google.android.filament` classes, they no longer interoperate: `nativeObject` is now our C
handle (`Long`), not an upstream Java object.

### iOS

Nothing to change.

## 2. Getting the compiler green

Work module by module, top to bottom of the tables in section 3. A few changes cascade widely and are worth
doing first with a project-wide find/replace:

1. `engine.destroyX(x)` → `engine.destroy(x)` and `engine.isValidX(x)` → `engine.isValid(x)`.
2. `View.XxxOptions` → `XxxOptions` (add an import from `io.github.erkko68.filament`).
3. `Colors` → `Color`.
4. `Engine.create()` now returns `Engine?`.

## 3. API renames

### `filament` — `Engine`

| 0.6.0 | Now |
| :--- | :--- |
| `engine.destroyView(v)`, `destroyScene`, `destroyTexture`, … (every `destroyX`) | `engine.destroy(v)` |
| `engine.isValidMaterial(m)`, … (every `isValidX`) | `engine.isValid(m)` |
| `engine.isValidExpensiveMaterialInstance(mi)` | `engine.isValidExpensive(mi)` |
| `engine.destroyEntity(e)` | `engine.destroy(e)` (components only, as in C++), then `engine.entityManager.destroy(e)` |
| `engine.destroyCamera(camera)` | `engine.destroyCameraComponent(camera.entity)` |
| `engine.destroy()` | `Engine.destroy(engine)` or `engine.close()` |
| `Engine.create(…)`: `Engine` | `Engine?` (null when no backend can start); same for `Engine.Builder.build()` |
| `engine.activeFeatureLevel = level` | `engine.setActiveFeatureLevel(level)` (returns the level now active) |
| `engine.getFeatureFlag(name)`: `Boolean` | `Boolean?` (null for an unknown flag) |
| `engine.maxStereoscopicEyes` | `Engine.maxStereoscopicEyes` (static) |
| `Engine.CompilerPriorityQueue` | `Material.CompilerPriorityQueue`; the `compile` callback receives the `Material` |
| `Engine.Config.*SizeMB`: `Long` | `Int` |

### `filament` — `View`, `Renderer`, `Camera`

| 0.6.0 | Now |
| :--- | :--- |
| `View.BloomOptions`, `View.FogOptions`, … and their nested enums | top-level `BloomOptions`, `FogOptions`, … as in `Options.h`: `AmbientOcclusionOptions`, `AntiAliasing`, `BlendMode`, `DepthOfFieldOptions`, `Dithering`, `DynamicResolutionOptions`, `GuardBandOptions`, `MultiSampleAntiAliasingOptions`, `RenderQuality`, `ScreenSpaceReflectionsOptions`, `ShadowType`, `SoftShadowOptions`, `StereoscopicOptions`, `TemporalAntiAliasingOptions`, `VignetteOptions`, `VsmShadowOptions` |
| `View.Quality` | `QualityLevel` |
| `DynamicResolutionOptions.minScale` / `maxScale`: `Float` | `FloatArray` (horizontal, vertical) |
| `Renderer.MirrorFrameFlag.COMMIT` / `SET_PRESENTATION_TIME` / `CLEAR` | `Renderer.COMMIT` / `SET_PRESENTATION_TIME` / `CLEAR` (`Int` flags) |
| `renderer.displayInfo`, `renderer.frameRateOptions` (read) | removed, as in C++; keep your own copy and call `setDisplayInfo` / `setFrameRateOptions` |
| `FrameRateOptions.interval`: `Float` | `Int` |
| `camera.near`, `camera.cullingFar`: `Float` | `Double` |
| `camera.getFieldOfViewInDegrees(fov)`: `Double` | `Float` |
| `camera.getPosition()`: `FloatArray` | `DoubleArray` |
| `camera.getModelMatrix(FloatArray)`, `getViewMatrix(FloatArray)` | the `DoubleArray` overloads |
| `swapChain.isFrameRateChangeSupported`: `Boolean` | `Engine.FeatureState` |

### `filament` — scene, entities and managers

| 0.6.0 | Now |
| :--- | :--- |
| `scene.getEntities()` | `scene.forEach { entity -> … }` |
| `scene.removeEntity(e)` | `scene.remove(e)` |
| `EntityManager.get().maxEntityCount` | `EntityManager.maxEntityCount` (static) |
| `transformManager.create(e)`: instance | `Unit`; look the instance up with `getInstance(e)` |
| `transformManager.getChildren(i, out)`: `IntArray` | returns the count written to `out` |
| `rm.setCullingEnabled(i, b)` / `rm.isFogEnabled(i)` | `rm.setCulling(i, b)` / `rm.getFogEnabled(i)` |
| `rm.setShadowCaster(i, b)` / `rm.setShadowReceiver(i, b)` | `rm.setCastShadows(i, b)` / `rm.setReceiveShadows(i, b)` |
| `rm.setBonesAsMatrices(…)` / `setBonesAsQuaternions(…)` (also on `SkinningBuffer`) | `setBones(…)` overloads (matrices, or `RenderableManager.Bone`s) |
| `RenderableManager.GeometryType` | `RenderableManager.Builder.GeometryType` |
| `RenderableManager.Builder.build(…)`, `LightManager.Builder.build(…)`: `Unit` | `Builder.Result` (`Success` / `Error`) |
| `lm.getInnerConeAngle(i)` / `getOuterConeAngle(i)` | `lm.getSpotLightInnerCone(i)` / `getSpotLightOuterCone(i)` |
| `ShadowOptions.elvsm`, `ShadowOptions.blurWidth` | `ShadowOptions.vsm.elvsm`, `ShadowOptions.vsm.blurWidth` |

### `filament` — materials, textures, buffers, misc

| 0.6.0 | Now |
| :--- | :--- |
| `Material.Parameter` (+ `.Type`, `.Precision`) | `Material.ParameterInfo`, `Material.ParameterType`, `Material.Precision` |
| `Material.RefractionMode`, `RefractionType`, `ReflectionMode`, `UserVariantFilterBit` | top-level, plus `MaterialDomain` |
| `MaterialInstance.StencilOperation.INCR_CLAMP` / `DECR_CLAMP` | `INCR` / `DECR` |
| `TextureSampler.CompareFunction` | `TextureSampler.CompareFunc` (`LE`, `GE`, … as in C++) |
| `sampler.compareFunction = f` | `sampler.setCompareMode(mode, f)`; read it with `sampler.compareFunc` |
| `Texture.Builder.importTexture(id)` | `Texture.Builder.import(id)` |
| `Texture.computeDataSize(…)` | `Texture.computeTextureDataSize(…)` |
| `IndexBuffer.Builder.IndexType` | `IndexBuffer.IndexType` |
| `VertexBuffer.VertexAttribute.UNUSED` | removed (not a C++ value) |
| `Fence.FenceStatus.ALREADY_SIGNALED` | `CONDITION_SATISFIED` (what `wait` actually returns) |
| `SurfaceOrientation.Builder.triangles16(…)` / `triangles32(…)` | `triangles(…)` |
| `SurfaceOrientation.getQuatsAsFloat` / `getQuatsAsHalf` | `getQuats` / `getHalfQuats`; `getQuatsAsShort` removed; `Builder.build` is nullable |
| `Colors`, `Colors.RgbType`, `Colors.RgbaType` | `Color`, top-level `RgbType`, `RgbaType` |
| `IndirectLight.getColorEstimate(sh, x, y, z)` (static) | takes `Float`s; `IndirectLight.Builder` cubemaps are nullable |
| `Box` from gltfio's `boundingBox` | `Aabb`, mirroring `Box.h` |

### `gltfio`

| 0.6.0 | Now |
| :--- | :--- |
| `AssetLoader.create(engine, provider, entityManager)` | `AssetLoader.create(AssetConfiguration(engine, provider, entityManager))` |
| `UbershaderProvider(engine)` | `createUbershaderProvider(engine)` (a `MaterialProvider`) |
| `ResourceLoader(engine, normalizeSkinningWeights)` | `ResourceLoader(ResourceConfiguration(engine, normalizeSkinningWeights = …))` |
| (textures decoded automatically) | register decoders: `addTextureProvider("image/png", createStbProvider(engine))`, also `image/jpeg`, and `image/ktx2` with `createKtx2Provider(engine)`; destroy them after the loader |
| `asset.getMorphTargetNames(entity)` | `asset.getMorphTargetCountAt(entity)` + `asset.getMorphTargetNameAt(entity, i)` |
| `instance.materialVariantNames` | `instance.materialVariantCount` + `instance.getMaterialVariantName(i)` |
| `instance.skinNames` | `instance.skinCount` + `instance.getSkinNameAt(i)` |
| `asset.boundingBox`, `instance.boundingBox`: `Box` | `Aabb` |

A complete loader setup is in [Using the Engine Without Compose → Loading a glTF model](../guide/engine.md#loading-a-gltf-model).

### `filament-utils`

| 0.6.0 | Now |
| :--- | :--- |
| `KTX1Loader.createTexture(engine, bytes, options)` | `Ktx1Reader.createTexture(engine, Ktx1Bundle(bytes), srgb)` |
| `KTX1Loader.createIndirectLight(…)` / `createSkybox(…)` | build the cubemap with `Ktx1Reader.createTexture`, then `IndirectLight.Builder` / `Skybox.Builder` as in C++ |
| `KTX1Loader.getSphericalHarmonics(bytes)` | `Ktx1Bundle(bytes).getSphericalHarmonics(out)` (returns false when absent) |
| `EquirectangularToCubemap(context).run(texture)` | `IBLPrefilterContext.EquirectangularToCubemap(context)(texture)` |
| `SpecularFilter(context).run(cubemap)` | `IBLPrefilterContext.SpecularFilter(context)(cubemap)` |
| `Manipulator.Mode.ORBIT` / `MAP` / `FLIGHT` | top-level `Mode.ORBIT` / `MAP` / `FREE_FLIGHT` |
| `Manipulator.Bookmark` | top-level `Bookmark` (closeable, with `interpolate` / `duration`) |
| `Manipulator.Fov` | top-level `Fov` |
| `manipulator.raycast(x, y, result)`: `Unit` | `Boolean`: whether the ray hit the ground plane (`result` holds the point) |

```kotlin
// 0.6.0
val ibl = KTX1Loader.createIndirectLight(engine, bytes).indirectLight

// Now
val cubemap = Ktx1Reader.createTexture(engine, Ktx1Bundle(bytes), srgb = false)
val sh = Ktx1Bundle(bytes).use { b -> FloatArray(27).takeIf { b.getSphericalHarmonics(it) } }
val ibl = IndirectLight.Builder().reflections(cubemap).apply { sh?.let { irradiance(3, it) } }.build(engine)
```

### `filamat`

| 0.6.0 | Now |
| :--- | :--- |
| `Filamat.init()` / `Filamat.shutdown()` | `MaterialBuilder.init()` / `MaterialBuilder.shutdown()` |
| `uniformParameter(type, name)` | `parameter(name, type)` |
| `uniformParameter(type, precision, name)` | `parameter(name, type, precision)` |
| `uniformParameterArray(type, size, name)` | `parameter(name, size, type)` |
| `samplerParameter(type, format, precision, name)` | `parameter(name, type, format, precision)` |
| `materialPackage.buffer` | `materialPackage.data` |

## 4. Behavior changes that still compile

These take C++'s values now. Check them if your scene looks different after upgrading:

- **`LightManager.ShadowOptions.lispsm` defaults to `true`** (C++'s default), and so does
  `filament-compose`'s `ShadowConfig.lispsm`. Set it to `false` to get 0.6.0's shadows back.
- **`TextureSampler()` defaults to `NEAREST` filtering and `CLAMP_TO_EDGE` wrapping**, as in C++. Pass
  the filters and wrap modes you want explicitly.
- **Tone mapper and option-struct constructors use C++'s defaults.**
- **Stencil setters on `MaterialInstance` default to `face = FRONT_AND_BACK`**, as in C++.
- **`engine.destroy(entity)` destroys only the entity's components**; the entity id stays alive until
  `entityManager.destroy(entity)`.
- **`Material.Builder.build()` returns `null` for a payload that isn't a `.filamat`** instead of crashing.
- **`Color` conversions no longer modify the array you pass in**; they return a new one.
- **`nativeObject` is the C handle on every platform** (`Long`; `Int` on web), not an upstream Java or JS
  object.
- **Compose Desktop's CPU readback** (still the default) reads each frame back through `Renderer` into its
  own Skia image, replacing the old two-slot buffer.

## 5. Removed

- API with no C++ counterpart: `VertexAttribute.UNUSED`, `SurfaceOrientation.getQuatsAsShort`, the
  `Scene.getEntities` / `removeEntity` aliases, `Renderer`'s display-info and frame-rate getters. The
  tables above give the replacement where there is one.
- The `io.github.erkko68.filament-ffm` artifacts, the embind web externals and `downloadPrebuilts_web`.

## 6. New

Filament C++ API that 0.6.0 didn't bind, now on every platform:

- **`filament`**: `Engine.createAsync`, per-type object counts and `defaultMaterial`,
  `Renderer.getFrameInfoHistory`, `RenderableManager.computeAABB`, `Exposure`, `FramePacer`,
  `FramePipelineEstimator`, `FrameHistoryStream`, `InstanceBuffer`, async calls (`runCommandAsync`, builder
  `async`, `set*Async`), `ColorGrading.Builder.outputColorSpace` with `ColorSpace`,
  `MaterialInstance.setConstant` / `compile` / `commit` and unsigned (`uint`…`uint4`) parameters,
  `Camera.getEyeFromViewMatrix`, `RenderTarget.Builder.multiview`, builder `name()`, `isCreationComplete`
  and more.
- **`gltfio`**: `detachFilamentComponents`, `recomputeBoundingBoxes`, `detachMaterialInstances`,
  `addEntitiesToScene`, `MaterialKey` specular / volume / dispersion fields.
- **`filament-utils`**: `Ktx2Reader`, `TangentSpaceMesh` (with `aux` / `getAux`), `Transcoder`,
  `IBLPrefilterContext.IrradianceFilter`, `Manipulator.getRay`.
- **`filamat`**: the rest of `MaterialBuilder`: `constant`, sampler `filterable` / `multisample` / `stages`,
  `quality`, `featureLevel`, `customBlendFunctions`, `instanced`, `stereoscopic*`, `output`, compute
  materials (`MaterialDomain.COMPUTE`, `groupSize`) and more. `MaterialBuilder` also runs on web through
  the optional `filamat-kmp.wasm`; load it with `MaterialBuilder.initJs`.

Beyond the bindings:

- **Windows on ARM** desktop runtime: `filament-jni-runtime-windows-arm64`.
- **Experimental GPU-to-GPU frame sharing on Compose Desktop** (macOS, Windows, Linux):
  `FilamentComposeDesktop.isGpuToGpuFrameSharingEnabled` skips the per-frame CPU readback. See
  [Platform Notes](../guide/platform-notes.md).
- **`renderingEnabled` on `FilamentView` / `FilamentSceneView`**: `false` pauses the render loop and keeps
  the last frame.
- **Runtime Material sample**: a scene that compiles its shaders with filamat.
- **API generator** (contributors): `generateCApi`, `generateKotlinExternals` and `apiGaps` replace the
  hand-written C layer and `check-common-api.sh`.

The [API reference](https://erkko68.github.io/filament-kmp/api/) has the details.

## 7. Fixed

- **Compose teardown no longer aborts the app** in a `LazyColumn` or other subcomposition, on a discarded
  composition, a glTF asset leaving mid-load, or a resized `rememberRenderTargetTexture`.
- **Filament panics say why on desktop and Android**: the message and native call stack are logged (stderr /
  logcat, and Android's crash-report abort message) before the process aborts, instead of only
  `uncaught exception of type utils::PreconditionPanic`.
- **Compressed `Texture.InternalFormat`s** (ETC2, DXT, ASTC, RGTC, BPTC) were silently created as `RGBA8`.
- **`Fence.wait` reports `CONDITION_SATISFIED`** instead of a nonexistent `ALREADY_SIGNALED`.
- **`MorphTargetBuffer.setPositionsAt` reads 3 floats per vertex**, not 4.
- **Compose leaked a `ToneMapper` per color grade.**
- **Vector and matrix math** (`filament-utils`): `++` / `--` no longer mutate their operand,
  `Float4 * Float3` keeps `z`, `equal` / `compareTo` match exact values at `delta = 0`, and `fract` follows
  GLSL for negatives.
- **`rememberMapCameraController`**: its eye sat on the target and drags never panned; it now looks down on
  the XZ plane, north up.
- **Web API gaps closed**: `setShadowType`, HDR decoding, IBL prefiltering, morph weights, gltfio instance
  queries, shadow options, `customLut`, `geometryType` and more now work on web.

---

[← Back to docs](../README.md)
