package io.github.erkko68.filament

import io.github.erkko68.filament.capi.*
import io.github.erkko68.filament.interop.*

/**
 * Yields the frames [renderer] presented since the last [getNewFrames], tracking the last processed frame ID and
 * reporting the IDs of frames that went missing. Keep one alive across frames and [close] it when done.
 */
class FrameHistoryStream(private val renderer: Renderer) : AutoCloseable {
    private var nativeHandle = FilaFrameHistoryStream_create(renderer.nativeHandle)

    /** A presented frame's [frameInfo], or a missing frame (null [frameInfo]) with its [frameId]. */
    data class Result(val frameId: Int, val frameInfo: Renderer.FrameInfo?)

    /** The frames new since the last call, oldest first; frames still awaiting presentation timing come later. */
    fun getNewFrames(): List<Result> {
        val capacity = renderer.maxFrameHistorySize
        val handles = List(capacity) { FilaRendererFrameInfo_create() }
        try {
            val missingBefore = IntArray(capacity)
            val n = interopScope {
                missingBefore.usePinned(capacity) { FilaFrameHistoryStream_getNewFrames(nativeHandle, toInterop(handles), it, capacity) }
            }
            return buildList {
                for (i in 0 until n) {
                    val info = frameInfoOf(handles[i])
                    for (id in info.frameId - missingBefore[i] until info.frameId) add(Result(id, null))
                    add(Result(info.frameId, info))
                }
            }
        } finally {
            handles.forEach { FilaRendererFrameInfo_destroy(it) }
        }
    }

    override fun close() {
        if (nativeHandle != NullPointer) FilaFrameHistoryStream_destroy(nativeHandle)
        nativeHandle = NullPointer
    }
}
