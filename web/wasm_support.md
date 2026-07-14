# Adding `wasmJs` Target to filament-kmp

> [!NOTE]
> **UNBLOCKED (2026-07-01) — post-processing the externals instead of waiting for Karakum.** Branch `feat/wasm-support`. Rather than wait for a wasmJs-capable Karakum, [scripts/patch-externals.mjs](file:///Users/eric/IdeaProjects/filament-kmp/web/scripts/patch-externals.mjs) rewrites Karakum's raw output into externals that compile on BOTH `js` and `wasmJs` (shared `webMain`). Karakum's raw output already uses multiplatform kotlin-wrappers types; the script fixes the wasmJs-illegal patterns: enum value-holders (`sealed external interface`→`external class`), `: JsAny` supertype on every external class/interface, `Any`→`JsAny`, unbounded `<T>`→`<T : JsAny?>`, unparameterized TypedArrays→`<js.buffer.ArrayBuffer>`, primitive array/record elements→`JsNumber`/`JsString`, `()->Void?`→`()->Unit`. It replaces the old js-only remap (which is exactly what made the externals js-only). **`:web:compileKotlinJs` + `:web:compileKotlinWasmJs` both green.**
>
> Also done: the `:js` module is renamed to `:web` (dir `web/`, package `io.github.erkko68.filament.web`); the convention plugin declares the `wasmJs` target + `wasmJsTest` assets; the 4 kotlin modules moved their `:web` dep to `webMain`.
>
> **Remaining = the actuals port (Step 3).** Dropping the `Double→Number` remap means every `Int`/`Float` handed to an external now needs `.toDouble()`, and stdlib types (`org.khronos.webgl`, `org.w3c.dom`, `Array`) must become their kotlin-wrappers equivalents, as the ~37 `jsMain` actuals move to `webMain`. Downstream `compileKotlinJs`/`compileKotlinWasmJs` are transitionally red until each module is ported (start with `:kotlin:filament` — everything depends on it). Step 0 (asDynamic elimination) is landed (commit `90018586`).

> [!NOTE]
> **Branch:** `wasm-support`. Strategy decided: **keep both `js` + `wasmJs`, share a `webMain` source set (Strategy A), long-term.**
> The sections below ("Feasibility", "Key Differences", "Proposed Changes", etc.) are the *original* exploration. The **Implementation Plan** immediately below supersedes the stale parts of it (notably: `JsAny` is NOT required on every external in Kotlin 2.3.21).

## Implementation Plan (step by step)

Spike-verified facts driving this plan:
- A shared `webMain` compiles into both `js` and `wasmJs`.
- wasmJs interop **rejects** `kotlin.Number`, `Array<T>`, stdlib `org.w3c.dom.*` / `org.khronos.webgl.*`. It accepts primitives (`Double`), `JsArray`, `JsNumber`, and the kotlin-wrappers `web.*` / `js.*` types (which are multiplatform js+wasmJs).
- Raw Karakum output (kotlin-wrappers types) is already wasmJs-compatible; the post-processing remap in [js/build.gradle.kts] down to stdlib is what makes it js-only.

### Step 0 — Eliminate `asDynamic()` (prerequisite) ✅ DONE
All 42 `asDynamic()` calls in `jsMain` removed (wasmJs has no `dynamic`). Typed-array sets use `org.khronos.webgl.set`; web-unbound APIs are explicit `// TODO` no-op stubs (verified against `jsbindings.cpp` v1.71.5); helpers `util/JsInterop.js.kt::jsNumber` and `gltfio/util/Assets.js.kt::putAsset` are single-expression `js(...)` (wasmJs-ready). js build + jsBrowserTest green.

### Step 1 — `:js` module: `webMain` foundation  ⛔ BLOCKED (see warning at top)
Attempted: added `wasmJs { browser() }` + `applyDefaultHierarchyTemplate()`, moved externals srcDir to `webMain`, dropped only the `Double→Number` and `ReadonlyArray→Array` remaps (kept typed-array/canvas remaps, with `kotlinx-browser:0.3` on `wasmJsMain` to supply `org.w3c.dom`/`org.khronos.webgl`). Result: `:js:compileKotlinJs` green, **`:js:compileKotlinWasmJs` FAILS** on the 62 enum + 15 `Any` files (see warning). Reverted; branch stays js-only and green. Resume here once Karakum emits wasmJs-compatible externals.

### Step 2 — Convention plugin: `wasmJs` target
- In [build-logic/src/main/kotlin/filament-kmp-module.gradle.kts] add `wasmJs { browser { binaries.executable() } }` and wire `wasmJsTest` web assets (mirror the `jsTest` filament.js/.wasm staging, lines ~59–72).
- `applyDefaultHierarchyTemplate()` already present → `webMain` appears once both targets are declared.

### Step 3 — Port the actuals to `webMain` (the bulk)
Per module in order `filament-utils`, `gltfio`, `filamat`, then `filament` (largest):
- Move `jsMain` actuals → `webMain` where they compile for both targets.
- Replace wasmJs-illegal types: `Number`→`Double`, `Array<T>`→`JsArray<…>`, stdlib `org.w3c.dom`/`org.khronos.webgl`→kotlin-wrappers (or kotlinx-browser) equivalents.
- Any remaining `js("…")` that references locals must become single-expression top-level `fun`s taking those as params (wasmJs rule) — same pattern as Step 0's helpers.
- Keep genuinely platform-divergent bits in `jsMain`/`wasmJsMain`.
- Acceptance after each module: its `compileKotlinJs` + `compileKotlinWasmJs` green; jsBrowserTest still passes.

### Step 4 — `filament-compose` wasmJs
- Port the Compose web surface ([WebViewCompositor.kt], [FilamentSurface.js.kt]) to `webMain`/`wasmJsMain`. Highest-risk layer (Compose web differs js vs wasmJs).

### Step 5 — Sample + run
- Add a `wasmJs` target to a sample (or a new `webApp` variant) with `index.html` loading `filament.js`/`filament.wasm`.
- Run: `./gradlew :samples:webApp:wasmJsBrowserDevelopmentRun` (task name TBD once wired).
- This is the first point where wasm is actually runnable end-to-end.

---

## Background

The project currently targets: **Android, JVM, iOS (arm64/simulatorArm64/x64), and JS (browser)**. The web target works by wrapping Google Filament's Emscripten/embind-compiled `filament.js` + `filament.wasm` — an opaque WASM module loaded by a JS loader. The Kotlin/JS code talks to it via `external` declarations generated by Karakum from `filament.d.ts`.

Adding `wasmJs` (Kotlin/Wasm targeting the browser) would let consumers build Compose Multiplatform web apps with the newer WasmGC backend while reusing the **exact same** underlying Filament WASM binary (`filament.wasm` loaded by `filament.js`).

## Feasibility Assessment

> [!IMPORTANT]
> **This is feasible** — the underlying Filament engine already runs as WASM in the browser. The `wasmJs` target would call into `filament.js` via JS interop, just like the existing `js` target does. The work is entirely on the Kotlin side.

### What stays the same
- The `filament.js` + `filament.wasm` prebuilt (Emscripten build) — unchanged
- The `prebuilts/web/` download task — already exists
- The `commonMain` expect declarations — unchanged
- The native/Android/JVM targets — untouched

### What must change
Everything in the Kotlin ↔ JS interop boundary needs adaptation because Kotlin/Wasm uses a **different type system** for JS interop than Kotlin/JS.

---

## Key Technical Differences: `js` vs `wasmJs`

| Aspect | Kotlin/JS (`js`) | Kotlin/Wasm (`wasmJs`) |
|:---|:---|:---|
| `dynamic` type | ✅ Supported | ❌ **Not supported** |
| `asDynamic()` | ✅ Supported | ❌ **Not supported** |
| `external class` base | Any (implicit) | Must extend `JsAny` |
| `external interface` base | Any (implicit) | Must extend `JsAny` |
| `js("...")` inline JS | ✅ Supported | ✅ Supported (different codegen) |
| `org.w3c.dom.*` | ✅ stdlib | ✅ stdlib (slightly different types) |
| `org.khronos.webgl.*` | ✅ stdlib | ✅ stdlib |
| `kotlinx.browser.document` | ✅ | ✅ |
| `unsafeCast<T>()` | ✅ | ✅ (target must be JsAny subtype) |
| Number types across boundary | Transparent | `JsNumber` ↔ Kotlin Number bridge |
| Compose Multiplatform support | Compose HTML / Canvas | Compose Canvas (Beta, primary target) |

---

## Proposed Changes

### Layer 1 — `:js` Module (External Declarations)

#### [MODIFY] [build.gradle.kts](file:///Users/eric/IdeaProjects/filament-kmp/js/build.gradle.kts)

Currently only declares a `js { browser() }` target. Must add `wasmJs { browser() }` and create a `wasmJsMain` source set (or use the shared `webMain` approach).

**Two strategies:**

**Strategy A — Shared `webMain` source set (recommended):**
```kotlin
kotlin {
    js { browser() }
    wasmJs { browser() }

    sourceSets {
        val webMain by creating {
            // Generated externals go here (must be JsAny-compatible)
        }
        val jsMain by getting { dependsOn(webMain) }
        val wasmJsMain by getting { dependsOn(webMain) }
    }
}
```

**Strategy B — Duplicate source sets:**
Generate separate externals for `jsMain` and `wasmJsMain`. More work, less code sharing.

> [!IMPORTANT]  
> **Strategy A requires that the Karakum-generated externals are compatible with both `js` and `wasmJs`.** Currently they are NOT — Karakum generates `external class Engine { ... }` without extending `JsAny`. For `wasmJs`, all external types must extend `JsAny`.

**Karakum output adaptation needed:**
The post-processing step in `generateJsExternals` (lines 147–164) already patches the output. We'd need to add additional transformations:
1. Make all `external class Foo` → `external class Foo : JsAny` (unless already extending something)
2. Make all `external interface Foo` → `external interface Foo : JsAny`
3. Replace `definedExternally` parameter defaults (should work as-is)
4. Handle `Number` return types → may need `JsNumber` bridging

However, this creates a **compatibility concern**: `JsAny` exists in `wasmJs` stdlib but not in `js` stdlib. As of Kotlin 2.3.x, there is a compatibility shim — `JsAny` is available in both targets when using the default hierarchy template with both `js` and `wasmJs` declared. This needs verification.

> [!WARNING]
> **Open question**: Does Kotlin 2.3.21 provide `JsAny` in the `js` target's stdlib (or via `webMain`)? If not, the shared source set approach won't work and we'd need Strategy B with separate externals.

---

### Layer 2 — Convention Plugin

#### [MODIFY] [filament-kmp-module.gradle.kts](file:///Users/eric/IdeaProjects/filament-kmp/build-logic/src/main/kotlin/filament-kmp-module.gradle.kts)

Changes needed:
1. **Add `wasmJs` target declaration** (line ~48):
   ```kotlin
   wasmJs {
       browser { binaries.executable() }
   }
   ```
2. **Wire `wasmJsTest` resources** for Filament web assets (similar to `jsTest` on lines 59–72)
3. **Set up `webMain` intermediate source set** if using Strategy A, so both `jsMain` and `wasmJsMain` inherit from it
4. The `applyDefaultHierarchyTemplate()` call (line 74) should automatically create `webMain` when both `js` and `wasmJs` are declared

---

### Layer 3 — Kotlin Modules (`kotlin/filament`, `kotlin/filamat`, `kotlin/filament-utils`, `kotlin/gltfio`)

Each of these has a `jsMain` source set with `actual` implementations. For `wasmJs`, we need equivalent `wasmJsMain` actuals.

#### Scale of the problem

| Module | jsMain files | jsMain lines | Key blocker |
|:---|:---|:---|:---|
| `kotlin/filament` | ~25 files | ~3500 lines | Heavy `asDynamic()` usage (30+ calls), `kotlinx.browser.document`, `org.w3c.dom` |
| `kotlin/filamat` | ~3 files | ~245 lines | Light, mostly delegation |
| `kotlin/filament-utils` | ~4 files | ~200 lines | `org.khronos.webgl` typed arrays |
| `kotlin/gltfio` | ~5 files | ~300 lines | `unsafeCast<ArrayBufferView>` |
| **Total** | **~37 files** | **~4250 lines** | |

**The critical blocker is `asDynamic()`** — used 30+ times in `kotlin/filament/src/jsMain/`. Each call must be replaced with either:
- A properly typed `external interface` / `external fun` declaration
- A `@JsFun` annotation wrapping inline JS
- A `js("...")` expression

#### Strategy: `webMain` + minimal platform-specific overrides

If `JsAny` is available in the shared `webMain` source set (Kotlin 2.3.x default hierarchy), most of the `jsMain` actuals could move to `webMain` — **IF** the `asDynamic()` calls are replaced with proper external declarations. The few remaining platform differences would stay in `jsMain`/`wasmJsMain`.

If `webMain` sharing is not viable, each `wasmJsMain` would need its own copy of the actuals with `asDynamic()` replaced.

#### Per-module build.gradle.kts changes

Each module's `build.gradle.kts` needs a `wasmJsMain` dependency block:
```kotlin
wasmJsMain.dependencies {
    implementation(project(":js"))
}
```

---

### Layer 4 — `kotlin/filament-compose`

#### [MODIFY] [build.gradle.kts](file:///Users/eric/IdeaProjects/filament-kmp/kotlin/filament-compose/build.gradle.kts)

The Compose module uses `HtmlElementView` (or `WebElementView`) for embedding the Filament canvas in Compose HTML. For `wasmJs`, Compose Multiplatform uses a canvas-based renderer with `HtmlElementView` for DOM interop — the same API but potentially different imports.

This is the **highest-risk layer** because Compose Multiplatform's web support differs between `js` and `wasmJs`:
- `js`: Uses Compose HTML (DOM-based) or Compose Canvas
- `wasmJs`: Uses Compose Canvas (primary), with `HtmlElementView` for DOM interop

The existing `jsMain` Compose code in [WebViewCompositor.kt](file:///Users/eric/IdeaProjects/filament-kmp/kotlin/filament-compose/src/jsMain/kotlin/io/github/erkko68/filament/compose/internal/WebViewCompositor.kt) and [FilamentSurface.js.kt](file:///Users/eric/IdeaProjects/filament-kmp/kotlin/filament-compose/src/jsMain/kotlin/io/github/erkko68/filament/compose/internal/FilamentSurface.js.kt) need `wasmJsMain` equivalents.

---

## Open Questions

> [!IMPORTANT]
> **Q1: Is `JsAny` available in `webMain` (shared between `js` and `wasmJs`) with Kotlin 2.3.21?**
> This determines whether we can use Strategy A (shared `webMain`) or need Strategy B (duplicate source sets). I can write a small test to verify this.

> [!IMPORTANT]
> **Q2: Does upstream Filament's `filament.js` work correctly when loaded from a Kotlin/Wasm application?**
> The Emscripten WASM module (`filament.wasm`) is loaded by `filament.js`, which manages its own WASM instantiation. Kotlin/Wasm produces a *separate* WASM module for the application code. These two WASM modules coexist in the same browser context via JS glue. This should work but needs testing.

> [!WARNING]
> **Q3: How much effort to replace all `asDynamic()` calls?**
> There are 30+ `asDynamic()` calls across `jsMain` in `kotlin/filament`. Most are for accessing properties not covered by the Karakum-generated externals (e.g., `jsView.asDynamic().getColorGrading()`). Each needs a proper `external` declaration or `@JsFun` wrapper. This is the bulk of the migration work.

> [!IMPORTANT]
> **Q4: Should we maintain backward compatibility with the `js` target, or consider dropping it in favor of `wasmJs`?**
> Maintaining both means either sharing code via `webMain` (ideal) or maintaining two parallel sets of actuals (expensive). If `wasmJs` is the future, we could deprecate `js` to avoid the dual-maintenance burden.

---

## Verification Plan

### Automated Tests
- Existing `jsTest` suites should pass unchanged
- New `wasmJsTest` suites should pass with equivalent test infrastructure
- `./gradlew :kotlin:filament:wasmJsBrowserTest` (once wired)

### Manual Verification
- Build a sample Compose Multiplatform app targeting `wasmJs` with Filament rendering
- Verify `filament.js` + `filament.wasm` load correctly in the WasmGC browser context
- Test WebGL canvas rendering works end-to-end

---

## Effort Estimate

| Work Item | Effort | Risk |
|:---|:---|:---|
| `:js` module — add `wasmJs` target + adapt Karakum output | Medium (2-3 days) | Medium — `JsAny` compatibility |
| Convention plugin — add `wasmJs` target | Small (0.5 day) | Low |
| `kotlin/filament` — port 25 jsMain files | Large (3-5 days) | High — `asDynamic()` elimination |
| `kotlin/filamat` — port 3 jsMain files | Small (0.5 day) | Low |
| `kotlin/filament-utils` — port 4 jsMain files | Small (0.5 day) | Low |
| `kotlin/gltfio` — port 5 jsMain files | Small (1 day) | Low |
| `kotlin/filament-compose` — port Compose web | Medium (2-3 days) | High — Compose wasmJs maturity |
| Testing & integration | Medium (2-3 days) | Medium |
| **Total** | **~10-16 days** | |

---

## Recommended Next Steps

1. **Spike**: Create a minimal `wasmJs` target in the `:js` module to verify `JsAny` compatibility and that `filament.js` loads correctly from Kotlin/Wasm
2. **Decide Strategy A vs B** based on the spike results
3. **Start with `kotlin/filament`** (the largest module) — port the actuals, replacing `asDynamic()` calls
4. **Cascade to smaller modules** (`filamat`, `filament-utils`, `gltfio`)
5. **Tackle `filament-compose`** last (depends on all the above)
