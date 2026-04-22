# Filament KMP API Coverage Roadmap

Generated against Filament v1.71.0.  
Run `python3 scripts/check_coverage.py` to get the latest coverage numbers.

## Current Status (v1.71.0)

| Module | Covered | Total | % |
|---|---|---|---|
| filament | 566 | 596 | 95% |
| filamat | 45 | 45 | 100% |
| gltfio | 60 | 67 | 90% |
| filament-utils | 48 | 57 | 84% |
| **TOTAL** | **719** | **765** | **94%** |

---

## Skip List (not applicable to KMP)

These appear as "missing" in the coverage report but should not be implemented:

| Class | Method(s) | Reason |
|---|---|---|
| `View` | `setToneMapping`, `getToneMapping`, `setAmbientOcclusion`, `getAmbientOcclusion` | `@Deprecated` — use `setColorGrading` / `setAmbientOcclusionOptions` |
| `Renderer` | `mirrorFrame` | `@Deprecated` — delegates to `copyFrame` which is already covered |
| `View` | `run` | False positive — inner class `InternalOnPickCallback.run()`, not a public API |
| `Engine` | `createSwapChainFromNativeSurface`, `getNativeJobSystem` | Android/internal plumbing |
| `Filament` | `init` | Android-specific native library loader |
| `Stream` | all | Android `SurfaceTexture`/camera streams |
| `AutomationEngine` | all | Android testing/CI infrastructure |
| `ImageDiff` | all | Android `Bitmap` comparison tool |
| `RemoteServer` | all | Android remote debugging server |
| `DeviceUtils` | `getGpuDriverInfo` | Android device driver info |
| `Texture` | `setExternalImage` | Android EGL/OpenGL ES external textures |
| `SurfaceOrientation` | `triangles_uint16`, `triangles_uint32` | Naming mismatch — we expose `triangles16`/`triangles32` |
| `UbershaderProvider` | all | Already implemented inside `MaterialProvider.kt` |
| `MaterialProvider` | `constrainMaterial(key, uvmap)` | False positive — `constrainMaterial` is on `MaterialKey` (inner class), already in `MaterialKey.kt` |
| `Texture` | `setCallback` | False positive — covered via `PixelBufferDescriptor` constructor callback parameter |

---

## Tier 1 — High impact, straightforward ✅ Done

| Class | Method(s) | Status |
|---|---|---|
| `View` | `setAntiAliasing(AntiAliasing)` / `getAntiAliasing()` | ✅ |
| `View` | `setColorGrading(ColorGrading?)` / `getColorGrading()` | ✅ |
| `View` | `pick(x, y, callback)` + `PickingQueryResult` | ✅ |
| `Scene` | `remove(entity)` | ✅ |
| `Material` | `hasParameter(name)` | ✅ |
| `Material` | `setDefaultParameter(name, bool/float/int/float2/float3/float4)` | ✅ |
| `Skybox` | `Builder.priority(Int)` | ✅ |
| `Skybox` | `setLayerMask(select, value)` / `getLayerMask()` / `getTexture()` | ✅ |
| `Fence` | `companion.waitAndDestroy(fence, mode)` | ✅ |
| `RenderableManager` | `Builder.enableSkinningBuffers(Boolean)` | ✅ |
| `RenderableManager` | `setBonesAsMatrices(instance, matrices, boneCount, offset)` | ✅ |
| `RenderableManager` | `setBonesAsQuaternions(instance, quaternions, boneCount, offset)` | ✅ |
| `RenderableManager` | `clearMaterialInstanceAt(instance, primitiveIndex)` | ✅ |
| `SkinningBuffer` | `setBonesAsQuaternions(engine, bones, boneCount, offset)` | ✅ |

> **Note on native (macOS/iOS):** `Material.hasParameter`/`setDefaultParameter` are not exposed by the Filament C API and remain stubs on native. `AntiAliasing` set/get now has full C interop via `FilaView_setAntiAliasing`/`FilaView_getAntiAliasing`.

---

## Tier 2 — Useful, slightly more involved ✅ Done

| Class | Method(s) | Status |
|---|---|---|
| `Engine` | `compile(priority, material, view, shadowReceiver, skinning, callback)` | ✅ — C wrapper + all platforms |
| `Engine` | `enableAccurateTranslations()` | ✅ — C wrapper + all platforms |
| `Texture` | `computeDataSize(format, type, stride, height, alignment)` | ✅ — C wrapper + all platforms |
| `ColorGrading.Builder` | `format(LutFormat)`, `dimensions(Int)` | ✅ — all platforms; `LutFormat` enum added |
| `Material` | `getParameterTransformName(name)` | ✅ — C wrapper + all platforms |

> **Skipped:** `EntityManager.get(index)` — no such method in the Filament C++ API. `Texture.generatePrefilterMipmap` — requires the `filament-utils` IBL prefilter library, deferred to IBLPrefilterContext new class. `ColorGrading.toneMapping(ToneMapping)` — deprecated Builder overload, omitted intentionally.

---

## Tier 3 — Nice to have / geometry utilities ✅ Done

| Class | Method(s) | Status |
|---|---|---|
| `MathUtils` | `packTangentFrame(...)` | ✅ — all platforms |
| `FilamentAsset` | `getEngine()`, `getInstance()` | ✅ — C wrapper + all platforms |
| `View` | `getLastDynamicResolutionScale()` | ✅ — all platforms |

---

## New Classes to Add

| Class | Module | Methods | Notes |
|---|---|---|---|
| `IBLPrefilterContext` | `filament-utils` | `destroy()`, `run(options)` | Generates prefiltered IBL cubemap at runtime. High value for dynamic IBL. |
