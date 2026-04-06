@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package dev.filament.kmp

import dev.filament.kmp.cinterop.*
import kotlinx.cinterop.*

actual class Stream(private var nativeInternal: Long) {
    actual enum class StreamType {
        NATIVE,
        ACQUIRED,
    }

    actual class Builder actual constructor() {
        private val builder = FilaStreamBuilder_create()

        actual fun stream(streamSource: Any): Builder {
            if (streamSource is Long) {
                FilaStreamBuilder_externalStream(builder, streamSource.toCPointer())
            }
            return this
        }

        actual fun width(width: Int): Builder {
            FilaStreamBuilder_width(builder, width.toUInt())
            return this
        }

        actual fun height(height: Int): Builder {
            FilaStreamBuilder_height(builder, height.toUInt())
            return this
        }

        actual fun build(engine: Engine): Stream {
            val stream = FilaStreamBuilder_build(builder, engine.nativeObject.toCPointer())
                ?: throw Exception("Failed to build Stream")
            return Stream(stream.toLong())
        }
    }

    actual val streamType: StreamType
        get() = when (FilaStream_getStreamType(nativeObject.toCPointer())) {
            FILA_STREAM_NATIVE -> StreamType.NATIVE
            FILA_STREAM_ACQUIRED -> StreamType.ACQUIRED
            else -> StreamType.NATIVE
        }

    actual fun setAcquiredImage(hwbuffer: Any, handler: Any, callback: () -> Unit) {
        // Implementation for setAcquiredImage with callback.
        // For now, we assume hwbuffer is a native pointer (Long).
        if (hwbuffer is Long) {
            val userData = StableRef.create(callback)
            val trampoline = staticCFunction<COpaquePointer?, COpaquePointer?, Unit> { _, user ->
                val cb = user?.asStableRef<() -> Unit>()?.get()
                cb?.invoke()
                user?.asStableRef<() -> Unit>()?.dispose()
            }
            FilaStream_setAcquiredImage(nativeObject.toCPointer(), hwbuffer.toCPointer(), trampoline, userData.asCPointer(), null)
        }
    }

    actual fun setDimensions(width: Int, height: Int) =
        FilaStream_setDimensions(nativeObject.toCPointer(), width.toUInt(), height.toUInt())

    actual val timestamp: Long
        get() = FilaStream_getTimestamp(nativeObject.toCPointer())

    actual val nativeObject: Long
        get() = nativeInternal

    actual internal fun invalidate() {
        nativeInternal = 0L
    }
}
