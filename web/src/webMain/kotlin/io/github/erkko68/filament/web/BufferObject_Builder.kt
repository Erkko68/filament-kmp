package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

@JsName("BufferObject\$Builder")
external class BufferObject_Builder : JsAny {
fun size(byteCount: Double): BufferObject_Builder
fun bindingType(type: BufferObject_BindingType): BufferObject_Builder
fun build(engine: Engine): BufferObject
// Unlike the other builders, filament.js's loadClassExtensions installs no
// `.build` wrapper on BufferObject_Builder — only the raw embind `_build` is
// callable, and it does not auto-delete the builder (callers do so manually).
fun _build(engine: Engine): BufferObject
fun delete(): Unit
}
