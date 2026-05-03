package io.github.erkko68.filament

import io.github.erkko68.filament.jni.Fence as JniFence

actual class Fence(val nativeFence: JniFence) {
    actual enum class Mode { 
        FLUSH, DONT_FLUSH;
        internal fun toJni() = JniFence.Mode.values()[ordinal]
    }
    
    actual enum class FenceStatus { 
        ERROR, ALREADY_SIGNALED, TIMEOUT_EXPIRED, CONDITION_SATISFIED;
        internal fun toJni() = JniFence.FenceStatus.values()[ordinal]
    }

    actual fun wait(mode: Mode, timeout: Long): FenceStatus =
        FenceStatus.values()[nativeFence.wait(mode.toJni(), timeout).ordinal]
    
    actual val nativeObject: Long get() = nativeFence.nativeObject

    actual companion object {
        actual fun waitAndDestroy(fence: Fence, mode: Mode): FenceStatus =
            FenceStatus.values()[JniFence.waitAndDestroy(fence.nativeFence, mode.toJni()).ordinal]
    }
}
