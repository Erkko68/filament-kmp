package io.github.erkko68.filament.web

/**
 * Controls whether the command stream is flushed before waiting on a [Fence].
 */
@JsName("Fence\$Mode")
external class Fence_Mode : JsAny {
companion object {
val FLUSH: Fence_Mode
val DONT_FLUSH: Fence_Mode
}
}
