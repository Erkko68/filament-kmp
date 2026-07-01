# `:js` — Kotlin/JS externals for Filament.js

This module provides the `external` Kotlin declarations that the Web/WASM target
binds against. They are **generated at build time** by
[Karakum](https://github.com/karakum-team/karakum) — there is no hand-written
`filament.js.kt`, and nothing generated is committed.

## Why generated (and why patched)

Filament ships a TypeScript definition, `filament.d.ts`, alongside `filament.js`
/ `filament.wasm`. But that d.ts is hand-maintained and **lags the real binding
surface** registered via embind in upstream `web/filament-js/jsbindings.cpp`
(the source of truth): methods are missing, mis-typed, under-arity'd, or
misspelled. So before generating we patch the d.ts.

## The pipeline (`build.gradle.kts`)

1. **`downloadPrebuilts_web`** (root) extracts `filament.d.ts` next to
   `filament.js`/`.wasm` into `prebuilts/web/`.
2. **`assembleFilamentDts`** concatenates the upstream d.ts with the curated
   patches, then normalises `$`→`_` (illegal in Kotlin identifiers), writing the
   result into a local npm package `filament-types/` (git-ignored).
3. **`installKarakum`** runs `npm install` (Karakum + TypeScript).
4. **`generateJsExternals`** runs Karakum, then applies a few output
   normalisations (see below). Output: `build/generated/karakum/…`, wired as a
   `jsMain` source dir.

`npm`/`npx` are taken from `PATH` (override with the `NPM`/`NPX` env vars).

## Patches (`patches/`)

- **`filament.patch.d.ts`** — *additive* declarations, via TypeScript declaration
  merging. Reopen a class as an `interface` of the same name to add the missing
  instance methods (and the missing `FeatureLevel` / `TransparencyMode` enums,
  `Engine$Config`, etc.). Edit this for the common case.
- **`filament.dts-overrides.json`** — *non-additive* fixes that merging can't
  express: literal `find`/`replace` corrections (e.g. a wrongly-typed field, an
  upstream typo) and `static` members spliced into a class body (Karakum renders
  a merged `namespace` as a separate object, so statics can't be merged).

Both are faithful to `jsbindings.cpp`. Run
[`scripts/dev/check-js-bindings.sh`](../scripts/dev/check-js-bindings.sh) on every
Filament bump to surface bindings present upstream but absent from the patches.

## Output normalisations (post-Karakum, in `generateJsExternals`)

Pinned to the kotlin-wrappers BOM that Karakum `1.0.0-alpha.107` generates
against (`2026.4.7`). Three textual remaps keep the output compiling and aligned
with the `jsMain` actuals:

- `js.typedarrays.*` → `org.khronos.webgl.*` (wrappers made TypedArrays generic;
  Karakum doesn't parameterise them).
- `js.array.ReadonlyArray` → `Array`, and `web.html.HTMLCanvasElement` →
  `org.w3c.dom.HTMLCanvasElement` (the actuals and the Compose HTML interop use
  the stdlib types).
- `Double` → `Number` (JS numbers are untyped; the actuals pass `Int`/`Float`).

## Regenerating

```sh
./gradlew :js:generateJsExternals   # or just build any Web target
```
