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