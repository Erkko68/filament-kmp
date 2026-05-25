# Upstream Filament inconsistencies (WebGL/JS surface)

Working notes accumulated while reconciling this repo's Kotlin externals and
actuals with upstream Filament. Three categories:

1. **d.ts lies** — `web/filament-js/filament.d.ts` declares signatures that
   don't match the embind binding in `web/filament-js/jsbindings.cpp`. The
   binding is the source of truth; the d.ts is hand-maintained and drifts.
2. **Missing embind bindings** — methods declared in the C++ public API
   (`filament/include/.../*.h`) but not exposed via `jsbindings.cpp`. Often
   asymmetric (getter or setter only).
3. **Not exposed at all** — entire classes the JS build deliberately omits.

Each entry below records where the gap is and what we did about it locally.
Some are worth filing upstream PRs for; flagged where applicable.

Refresh by running `scripts/check-js-bindings.sh` and `scripts/check-common-api.sh`
on a new `filaVersion` bump.

---

## 1. `filament.d.ts` lies (binding exists, type was wrong)

All fixed in this repo by correcting [`js/src/jsMain/kotlin/filament.js.kt`](../js/src/jsMain/kotlin/filament.js.kt).
No upstream PR needed — `jsbindings.cpp` is correct, the d.ts is what's wrong.
Each of these had been worked-around with `asDynamic()` calls or hardcoded
constants in the actuals before being fixed properly.

| Method | d.ts said | Actually returns / accepts | Symptom before fix |
| :--- | :--- | :--- | :--- |
| `Animator.applyAnimation(index)` | 1-arg `(index: number)` | 2-arg `(index, time)` | Time parameter silently dropped; skeletal animations stuck at first keyframe. |
| `View.PickingQueryResult.renderable` | `number` | `Entity` JS wrapper | `.toInt()` returned garbage; picking always returned id 0 → `entityToIndex` lookup failed → duck/planet "not clickable". **This is the actual root cause of the "duck not selectable" bug we initially attributed to the `glClearBufferfv` validator error.** |
| `TransformManager.getParent(instance)` | (missing entirely) | `Entity` | We stubbed it returning `0`. |
| `Skybox.getIntensity()` / `getLayerMask()` | (missing) | `number` | Mirrors returned constructor-default mirror values. |
| `Skybox.Builder.priority(value)` | (missing) | builder fluent | Calls were no-ops. |
| `BufferObject.getByteCount()` | (missing) | `number` | Stub returned `0`. |
| `View.getFogEntity()` | (missing) | `Entity` | Stub returned `0`. |
| `View.setShadowingEnabled` / `setFrontFaceWindingInverted` | (missing) | setters | Stubs no-op. |
| `View.setMaterialGlobal(idx, vec4)` / `getMaterialGlobal(idx)` | (missing) | both bound | Stubs no-op + empty array. |
| `View.clearFrameHistory(engine)` / `setDynamicLightingOptions(near, far)` | (missing) | bound | Stubs no-op. |
| `gltfio_FilamentAsset.geAssetInstances` | **misspelled** in d.ts | upstream is `getAssetInstances` | Method was unreachable from Kotlin (no compile error, just wrong runtime symbol). |

The full list of newly-declared bindings is in this session's git diff of
`filament.js.kt`. The take-away: don't trust `filament.d.ts` — verify against
`jsbindings.cpp` when something looks stubbed or wrong.

---

## 2. Embind bindings missing for upstream C++ methods

These exist in `filament/include/.../*.h` but aren't bound in
`jsbindings.cpp`. The asymmetric ones (only setter, no getter) are likely
oversights — sibling methods on the same class are bound, so the omission
looks accidental. Filing small PRs upstream would fix them.

### `MaterialInstance.isStencilWriteEnabled()` ⚠ likely oversight

- C++ method [exists](https://github.com/google/filament/blob/v1.71.4/filament/include/filament/MaterialInstance.h#L444):
  `bool isStencilWriteEnabled() const noexcept;`
- Every sibling getter (`isColorWriteEnabled`, `isDepthWriteEnabled`,
  `isDepthCullingEnabled`, `isDoubleSided`, …) IS bound right next to its
  setter — except this one.
- **Fix upstream**: one-line addition in `jsbindings.cpp`:
  ```cpp
  .function("isStencilWriteEnabled", &MaterialInstance::isStencilWriteEnabled)
  ```
- **Local workaround**: getter returns `false` (matches Filament's
  runtime default `StencilState::stencilWrite = false` from
  `backend/DriverEnums.h`). See [MaterialInstance.js.kt](../kotlin/filament/src/jsMain/kotlin/io/github/erkko68/filament/MaterialInstance.js.kt).

### `View` setter-only bindings (no read-back)

| Method | Bound? | Comment |
| :--- | :---: | :--- |
| `setPostProcessingEnabled` | yes | |
| `isPostProcessingEnabled` | **no** | Reasonable PR candidate — symmetry. |
| `setShadowingEnabled` | yes | |
| `isShadowingEnabled` | **no** | |
| `setFrustumCullingEnabled` | **no** | Setter itself missing too; harder PR. |
| `isFrustumCullingEnabled` | **no** | |
| `setScreenSpaceRefractionEnabled` | **no** | Same. |
| `isScreenSpaceRefractionEnabled` | **no** | |
| `setVisibleLayers` | yes | |
| `getVisibleLayers` | **no** | |

For each of the missing getters above, we mirror locally in
[View.js.kt](../kotlin/filament/src/jsMain/kotlin/io/github/erkko68/filament/View.js.kt)
— so reads of `isShadowingEnabled` after a `setShadowingEnabled(true)`
correctly return `true`, but reads before any setter call return the
Filament runtime default. This is correct as long as the user always
writes-before-reads, which is the common pattern.

---

## 3. Classes / methods not exposed in JS at all

These are conscious omissions in `jsbindings.cpp`. Implementing them locally
isn't possible without rebuilding `filament.wasm`. Affected APIs are
stubbed to safe defaults and documented in their respective actuals.

### Buffer-introspection getters

| Class.method | Notes |
| :--- | :--- |
| `VertexBuffer.getVertexCount()` | Builder only exposes setting; no read-back. |
| `IndexBuffer.getIndexCount()` | Same. |
| `SurfaceOrientation.getVertexCount()` | Same. |
| `BufferObject.getByteCount()` | (was) Now fixed — d.ts lie, see §1. |

### `MorphTargetBuffer` — entire class missing

`jsbindings.cpp` has no `class_<MorphTargetBuffer>(...)` block at all.
Affects `vertexCount`, `count`, `hasPositions`, `hasTangents`,
`isCustomMorphingEnabled`. All stubbed to zero/false. Morph-target sample
won't work on web.

### `SkinningBuffer` — entire class missing

Same situation. `boneCount` stubbed to zero. Skeletal animation works for
GLTF assets because `gltfio` handles the buffer internally — but the
top-level `SkinningBuffer` API isn't usable on web.

### `Texture` sub-region uploads

`jsbindings.cpp` exposes `_setImage(engine, level, pbd)` and
`_setImageCube` but NOT the offset/width/height/depth overloads.
The actuals fall back to whole-level upload — see [Texture.js.kt:194](../kotlin/filament/src/jsMain/kotlin/io/github/erkko68/filament/Texture.js.kt#L194).

### `Renderer.displayInfo` / `frameRateOptions`

Only the field-storage variants are bound (no `setDisplayInfo` /
`setFrameRateOptions` accessible). Stored locally; the renderer never
actually receives the values. See [Renderer.js.kt:8-19](../kotlin/filament/src/jsMain/kotlin/io/github/erkko68/filament/Renderer.js.kt#L8-L19).

### `LightManager.Builder.intensity(watts, efficiency)` overload

Only single-argument `intensity(value)` is bound on the Builder; the
energy-based overload falls back to passing `watts` only. See
[LightManager.js.kt:240](../kotlin/filament/src/jsMain/kotlin/io/github/erkko68/filament/LightManager.js.kt#L240).
Affects `intensity(watts, efficiency)` and `intensityCandela(intensity)`.

### `ToneMapper` enum values

`PBR_NEUTRAL`, `GT7`, `AGX` are bound, but `ColorGrading.toneMapping`
on JS uses string names and only `ACES`, `ACES_LEGACY`, `FILMIC`,
`LINEAR`, `GENERIC` are registered as JS-side strings — so the modern
tone mappers fall back to `ACES` on the JS path.

### `Renderer.beginFrame(frameTimeNanos)`

JS `beginFrame(swapChain)` doesn't take a `frameTimeNanos` argument; the
actual silently drops it. See [Renderer.js.kt:34-40](../kotlin/filament/src/jsMain/kotlin/io/github/erkko68/filament/Renderer.js.kt#L34-L40).

### `Renderer.setPresentationTime` / `skipFrame(vsyncTime)`

JS binding for `setPresentationTime` exists but takes no useful arg
shape on web; we no-op it. `skipFrame()` on JS takes no parameter even
though the C++ overload that accepts a vsync time exists.

### `Camera.getEntity()` — not bound

C++ has `utils::Entity Camera::getEntity() const noexcept` but
`jsbindings.cpp` doesn't expose it. We work around it by passing the
entity to the `Camera` constructor at creation time (see
[Camera.js.kt](../kotlin/filament/src/jsMain/kotlin/io/github/erkko68/filament/Camera.js.kt)
and the matching `Engine.createCamera()` / `getCameraComponent()`
call sites in [Engine.js.kt](../kotlin/filament/src/jsMain/kotlin/io/github/erkko68/filament/Engine.js.kt)).
Worth a one-line upstream PR:

```cpp
.function("getEntity", &Camera::getEntity)
```

### `Camera.getFieldOfViewInDegrees(direction)` — not bound

Recovering FOV from the projection matrix requires re-implementing the
math; the common API stub returns `0.0`. Worth an upstream PR to bind
`Camera::getFieldOfViewInDegrees`.

### `Camera.setCustomEyeProjection` / `setEyeModelMatrix`

Multi-eye stereoscopic projection setters: C++ overloads exist but no
JS binding. Stubbed no-op. Stereo-on-web is incomplete in Filament
generally (the experimental WebGPU path has more support).

### `Engine.FeatureLevel.FEATURE_LEVEL_0` — not bound on JS

The Android-mirrored Kotlin common API has `FEATURE_LEVEL_0` (ES2-class
hardware), but the JS `FeatureLevel` enum in `jsenums.cpp` only declares
`FEATURE_LEVEL_1` … `_3`. Filament's WebGL build targets GLES3/WebGL2
only, so FL0 isn't reachable from JS. The JS actual maps FL0 → FL1
when called via `setActiveFeatureLevel(FEATURE_LEVEL_0)`. Not really an
upstream bug — just an architectural decision that means one enum
value silently aliases on web.

### `Engine.flushAndWait` / `flush` / `isPaused` / `setPaused` / feature-flag API

None of these are bound in JS. Currently the actuals approximate
`flush*` via `Engine::execute` (close enough), and the rest are pure
stubs. If any of these get used in common code, they'll fail silently.
Probably worth an upstream PR to expose at least `flush` and
`flushAndWait` since they're commonly needed for resource
synchronization.

### `Engine.Config.preferredShaderLanguage` enum mapping

The JS-side `Engine$Config.preferredShaderLanguage` is bound as a
number (the enum is registered separately as `ShaderLanguage` with
values `DEFAULT`, `MSL`, `METAL_LIBRARY`). Mapping to/from the Kotlin
`Engine.Config.ShaderLanguage` enum isn't wired in `getConfig()` yet —
the field is left at its default. Low-priority since shader-language
selection is mostly Metal-relevant.

---

## Suggested upstream PRs

Worth filing as small follow-ups to the WebGL spec-constant PR — each is a
one-line addition next to its sibling method:

1. **`MaterialInstance.isStencilWriteEnabled`** — bind it, matches the
   existing pattern for `isColorWriteEnabled` etc.
2. **`View.isPostProcessingEnabled`** / **`isShadowingEnabled`** — bind the
   getters since the setters are already there.
3. **`Camera.getEntity`** / **`Camera.getFieldOfViewInDegrees`** — bind these
   getters; both exist in C++ and unblock useful queries from JS code.
4. **Fix the `filament.d.ts` typos** — at minimum `geAssetInstances` →
   `getAssetInstances`, the `applyAnimation` arity, the picking result
   `renderable` type. The d.ts is hand-maintained so each fix is one line.

Each is small enough to bundle as a single PR. Prioritise the typo fix
since it silently misroutes Kotlin code (no compile error, just runtime
failure).

---

## How this file is maintained

Add entries when running `scripts/check-js-bindings.sh` or
`scripts/check-common-api.sh` after a `filaVersion` bump reveals a new
mismatch. Remove entries when:

- An upstream PR lands fixing the binding (delete the row).
- A new release tag picks up the fix (drop the row at `filaVersion` bump
  time).
- We work around it locally without further upstream interaction (move the
  row to the "fixed locally" section above).
