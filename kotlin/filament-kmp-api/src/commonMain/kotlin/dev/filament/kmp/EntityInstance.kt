package dev.filament.kmp

@Target(
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.FIELD,
)
annotation class EntityInstance

const val ENTITY_INSTANCE_NULL: Int = 0

