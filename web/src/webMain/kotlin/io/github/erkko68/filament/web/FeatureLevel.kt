package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

/*
 * Curated overlay for Filament's upstream `filament.d.ts`.
 *
 * WHY THIS EXISTS
 * Filament.js exposes its API through embind registrations in
 * `web/filament-js/jsbindings.cpp` — that file is the source of truth. The
 * shipped `filament.d.ts` is hand-maintained and lags behind it: methods that
 * are reachable at runtime are routinely missing or under-typed (wrong arity).
 *
 * The :js Gradle build concatenates this overlay onto the upstream
 * `filament.d.ts` before running Karakum, so the generated Kotlin externals
 * cover the *real* surface. Everything declared here is registered in
 * jsbindings.cpp for Filament 1.72.0.
 *
 * HOW TO EDIT
 * - Instance methods: reopen the class as an `interface` of the same name
 *   (TypeScript declaration merging). Adding an existing signature again is a
 *   harmless overload; adding a new-arity overload makes the under-typed
 *   upstream declaration callable with the real arguments.
 * - Missing enums / value objects: declare them here as a top-level `export`.
 * - Static (class) methods can't be added by merging — Karakum renders a merged
 *   `namespace` as a separate object. They're injected into the upstream class
 *   body instead; see `staticInjections` in js/build.gradle.kts.
 *
 * Names use `_` (the upstream separator); the build normalises `_`→`_` for the
 * whole assembled d.ts before generation. Run `scripts/dev/check-js-bindings.sh`
 * on every Filament bump to surface bindings present upstream but absent here.
 */
// ── Enums missing from the d.ts (registered as embind enums) ──────────────────
external class FeatureLevel : JsAny {
companion object {
val FEATURE_LEVEL_1: FeatureLevel
val FEATURE_LEVEL_2: FeatureLevel
val FEATURE_LEVEL_3: FeatureLevel
}
}
