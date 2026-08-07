package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class Texture : JsAny {
fun setImage(engine: Engine, level: Double, pbd: driver_PixelBufferDescriptor): Unit
fun setImage(engine: Engine, level: Double, xoffset: Double, yoffset: Double, width: Double, height: Double, pbd: driver_PixelBufferDescriptor): Unit
fun setImage(engine: Engine, level: Double, xoffset: Double, yoffset: Double, zoffset: Double, width: Double, height: Double, depth: Double, pbd: driver_PixelBufferDescriptor): Unit
fun getWidth(engine: Engine, level: Double = definedExternally): Double
fun getHeight(engine: Engine, level: Double = definedExternally): Double
fun getDepth(engine: Engine, level: Double = definedExternally): Double
fun getLevels(engine: Engine): Double
fun getTarget(): Texture_Sampler
fun getFormat(): Texture_InternalFormat
fun generateMipmaps(engine: Engine): Unit
companion object {
fun isTextureFormatMipmappable(engine: Engine, format: Texture_InternalFormat): Boolean
fun isTextureFormatSupported(engine: Engine, format: Texture_InternalFormat): Boolean
fun isTextureSwizzleSupported(engine: Engine): Boolean
fun getMaxTextureSize(engine: Engine, type: Texture_Sampler): Double
fun getMaxArrayTextureLayers(engine: Engine): Double
fun validatePixelFormatAndType(internalFormat: Texture_InternalFormat, format: PixelDataFormat, type: PixelDataType): Boolean
fun Builder(): Texture_Builder
}
}
