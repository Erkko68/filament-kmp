package io.github.erkko68.filament

import io.github.erkko68.filament.interop.*

/**
 * Fence is used to synchronize the application main thread with filament's rendering thread.
 */
class Fence @InternalFilamentApi constructor(
    internal var nativeHandle: NativePointer,
    /** The engine that created it; destroys the fence where waitAndDestroy can't block (web). */
    private val engine: NativePointer = NullPointer,
) {
    /**
     * Mode controls the behavior of the command stream when calling wait().
     *
     * @note It would be unwise to call `wait(..., Mode.DONT_FLUSH)` from the same thread
     * the Fence was created, as it would most certainly create a dead-lock.
     */
    enum class Mode {
        /** The command stream is flushed */
        FLUSH,
        /** The command stream is not flushed */
        DONT_FLUSH
    }
    /** Error codes for Fence.wait() */
    enum class FenceStatus {
        ERROR,
        ALREADY_SIGNALED,
        TIMEOUT_EXPIRED,
        CONDITION_SATISFIED
    }

    /**
     * Client-side wait on the Fence.
     *
     * Blocks the current thread until the Fence signals.
     *
     * @param mode Whether the command stream is flushed before waiting or not.
     * @param timeout Wait time out in nanoseconds. Using a timeout of 0 is a way to query the state of the fence.
     * @return FenceStatus.CONDITION_SATISFIED on success,
     *         FenceStatus.TIMEOUT_EXPIRED if the time out expired, or
     *         FenceStatus.ERROR in other cases.
     */
    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "the timeout is clamped to 0 — wasm is single-threaded, so wait() is a non-blocking poll (a FLUSH has already executed every command).")
    fun wait(mode: Mode, timeout: Long): FenceStatus {
        // Single-threaded wasm rejects a non-zero timeout; a FLUSH has already run every command there.
        val result = FilaFence_wait(nativeHandle, mode.ordinal, if (singleThreaded) 0L else timeout)
        return FenceStatus.entries[result + 1] // ERROR is -1, ordinal 0
    }

    val nativeObject: NativePointer get() = nativeHandle

    companion object {
        /**
         * Client-side wait on a Fence and destroy the Fence.
         *
         * @param fence Fence object to wait on.
         * @param mode Whether the command stream is flushed before waiting or not.
         * @return FenceStatus.CONDITION_SATISFIED on success, FenceStatus.ERROR otherwise.
         */
        fun waitAndDestroy(fence: Fence, mode: Mode): FenceStatus {
            if (singleThreaded) {
                val status = fence.wait(mode, 0L)
                FilaEngine_destroyFence(fence.engine, fence.nativeHandle)
                fence.nativeHandle = NullPointer
                return status
            }
            val result = FilaFence_waitAndDestroy(fence.nativeHandle, mode.ordinal)
            fence.nativeHandle = NullPointer
            return FenceStatus.entries[result + 1]
        }
    }
}

@ExternalSymbolName("FilaFence_wait")
private external fun FilaFence_wait(fence: NativePointer, mode: Int, timeoutNanoSeconds: Long): Int

@ExternalSymbolName("FilaFence_waitAndDestroy")
private external fun FilaFence_waitAndDestroy(fence: NativePointer, mode: Int): Int

@ExternalSymbolName("FilaEngine_destroyFence")
internal external fun FilaEngine_destroyFence(engine: NativePointer, fence: NativePointer): Boolean
