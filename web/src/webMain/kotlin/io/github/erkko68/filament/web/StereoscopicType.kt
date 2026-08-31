// Hand-maintained external — jsbindings.cpp registers `enum_<…>("StereoscopicType")`.

package io.github.erkko68.filament.web

external class StereoscopicType : JsAny {
companion object {
val NONE: StereoscopicType
val INSTANCED: StereoscopicType
val MULTIVIEW: StereoscopicType
}
}
