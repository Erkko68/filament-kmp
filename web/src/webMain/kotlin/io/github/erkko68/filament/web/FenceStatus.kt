package io.github.erkko68.filament.web

/**
 * Result codes for [Fence.wait].
 */
external class FenceStatus : JsAny {
companion object {
val ERROR: FenceStatus
val CONDITION_SATISFIED: FenceStatus
val TIMEOUT_EXPIRED: FenceStatus
}
}
