package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

/** A C++ std::vector. */
external interface Vector<T : JsAny?> : JsAny {
fun size(): Double
fun get(i: Double): T
}
