package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class BufferObject : JsAny {
fun setBuffer(engine: Engine, data: BufferReference, byteOffset: Double = definedExternally): Unit
fun getByteCount(): Double
fun delete(): Unit
companion object {
fun Builder(): BufferObject_Builder
}
}

// ── SwapChain ─────────────────────────────────────────────────────────────────
// ── BufferObject ──────────────────────────────────────────────────────────────
