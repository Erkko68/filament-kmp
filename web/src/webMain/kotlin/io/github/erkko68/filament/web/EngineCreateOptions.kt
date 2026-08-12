package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external interface EngineCreateOptions : JsAny {
    var backend: Backend?
    var majorVersion: Double?
    var minorVersion: Double?
    var alpha: Boolean?
    var depth: Boolean?
    var stencil: Boolean?
    var antialias: Boolean?
    var premultipliedAlpha: Boolean?
    var preserveDrawingBuffer: Boolean?
    var powerPreference: String?
    var failIfMajorPerformanceCaveat: Boolean?
    var desynchronized: Boolean?
    // Engine::Builder settings with no other route: Engine::Builder is not bound, so
    // Engine.create forwards these to its _create binding.
    /** Engine feature flags, keyed by name (`Engine::Builder::feature`). */
    var features: JsAny?
    /** `Engine::Builder::colorGrading`. */
    var colorGrading: ColorGrading_Builder?
}

