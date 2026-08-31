package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

// Clients should use the [PixelBuffer/CompressedPixelBuffer] helper function to contruct PixelBufferDescriptor objects.
@JsName("driver\$PixelBufferDescriptor")
external class driver_PixelBufferDescriptor : JsAny {
constructor (byteLength: Double, format: PixelDataFormat, datatype: PixelDataType)
constructor (byteLength: Double, cdtype: CompressedPixelDataType, imageSize: Double, compressed: Boolean)
fun getBytes(): js.buffer.ArrayBuffer
}
