package io.github.erkko68.filament.web

// Filament.Buffer(typedArray) in utilities.js is the usual way to build one of these.
@JsName("driver\$BufferDescriptor")
external class driver_BufferDescriptor(byteLength: Double) : JsAny {
fun getBytes(): org.khronos.webgl.ArrayBuffer
fun delete(): Unit
}
