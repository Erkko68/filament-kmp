package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

// This enum is a bit different the others because it can be used in a bitfield.
// It is a "const enum" which means TypeScript will simply create a constant for each member.
// It does not contain the _ delimiter to avoid interference with the embind class.
external class TextureUsage : JsAny {
companion object {
val COLOR_ATTACHMENT: TextureUsage
val DEPTH_ATTACHMENT: TextureUsage
val STENCIL_ATTACHMENT: TextureUsage
val UPLOADABLE: TextureUsage
val SAMPLEABLE: TextureUsage
val SUBPASS_INPUT: TextureUsage
val DEFAULT: TextureUsage
}
}
