package io.github.erkko68.filament.web

// Material::getParameters returns plain JS objects rather than a value_object, because
// ParameterInfo is a bitfield/union POD. `type`, `samplerType` and `subpassType` are a
// union: exactly one is present, selected by isSampler/isSubpass.
external interface Material_ParameterInfo : JsAny {
    val name: String
    val isSampler: Boolean
    val isSubpass: Boolean
    val type: Int?
    val samplerType: Int?
    val subpassType: Int?
    val count: Int
    val precision: Int
}
