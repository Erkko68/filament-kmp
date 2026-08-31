// Hand-maintained external — jsbindings.cpp registers `enum_<…>("ShaderLanguage")`.

package io.github.erkko68.filament.web

external class ShaderLanguage : JsAny {
companion object {
val DEFAULT: ShaderLanguage
val MSL: ShaderLanguage
val METAL_LIBRARY: ShaderLanguage
}
}
