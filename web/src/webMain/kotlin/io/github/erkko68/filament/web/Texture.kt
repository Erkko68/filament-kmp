// Automatically generated - do not modify!

package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class Texture : JsAny {
fun setImage(engine: Engine, level: Double, pbd: driver_PixelBufferDescriptor): Unit
fun getWidth(engine: Engine, level: Double = definedExternally): Double
fun getHeight(engine: Engine, level: Double = definedExternally): Double
fun getDepth(engine: Engine, level: Double = definedExternally): Double
fun getLevels(engine: Engine): Double
fun generateMipmaps(engine: Engine): Unit
companion object {
fun isTextureFormatMipmappable(engine: Engine, format: Texture_InternalFormat): Boolean
fun validatePixelFormatAndType(internalFormat: Texture_InternalFormat, format: PixelDataFormat, type: PixelDataType): Boolean
fun Builder(): Texture_Builder
}
}
