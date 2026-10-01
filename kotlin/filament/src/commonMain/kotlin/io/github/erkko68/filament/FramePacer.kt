package io.github.erkko68.filament

import io.github.erkko68.filament.capi.*
import io.github.erkko68.filament.interop.*

/**
 * Coordinates frame scheduling and presentation timestamps: a filter between the platform's VSYNC callbacks
 * (such as Android's Choreographer) and [Renderer.setPresentationTime].
 *
 * Created with [Builder] and destroyed with [Engine.destroy]. All times are steady-clock nanoseconds
 * (`CLOCK_MONOTONIC` on Android, which `System.nanoTime()` reads there). In each VSYNC callback:
 * ```
 * if (pacer.setupFrame(tick) != FramePacer.FrameStatus.ACCEPTED) return // yield to keep the cadence
 * if (pacer.hasGpuFallenBehind(renderer)) { renderer.skipFrame(tick.baseTime); return }
 * pacer.applyPresentationTime(renderer)
 * if (renderer.beginFrame(swapChain)) { renderer.render(view); renderer.endFrame() }
 * ```
 */
class FramePacer @InternalFilamentApi constructor(internal var nativeHandle: NativePointer) {
    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    enum class FrameStatus {
        /** Skipped to keep the target frame rate cadence (e.g. 30 FPS on a 60Hz display). */
        SKIPPED_SPURIOUS,
        /** Skipped to prevent out-of-order presentation. */
        SKIPPED_STALE,
        /** The frame is approved for rendering. */
        ACCEPTED,
    }

    enum class PacingStatus {
        /** Operating at or near the configured latency. */
        STEADY,
        /** Latency has shrunk: the display is starving for buffers. */
        DISPLAY_STARVING,
        /** Latency has bloated: the display queue is stuffed. */
        DISPLAY_STUFFED,
    }

    /** One expected hardware presentation timeline. */
    data class HardwareTimeline(
        /** The anticipated buffer presentation time. */
        val expectedPresentationTime: Long,
        /** The CPU/GPU submission deadline to make this timeline. */
        val deadline: Long,
    )

    /** VSYNC telemetry from the platform compositor. */
    data class VsyncTick(
        /** The hardware VSYNC time, e.g. Choreographer's `frameTimeNanos`. */
        val baseTime: Long,
        /** The hardware VSYNC period, e.g. `1e9 / Display.getRefreshRate()`. */
        val vsyncPeriod: Long = 16_666_666,
        /** When the frame callback was entered, to drop timelines whose deadline passed; null is the [setupFrame] call time. */
        val frameScheduleTime: Long? = null,
        /** Candidate presentation timelines in chronological order, where the platform has them (Android 33+). */
        val timelines: List<HardwareTimeline> = emptyList(),
    )

    /** Pacing targets. */
    data class Configuration(
        /** The desired frame rate in Hz. */
        val targetFrameRate: Float = 60f,
        /** Target latency in nanoseconds (~2 frames at 60Hz). */
        val latency: Long = 33_333_333,
    )

    class Builder {
        private val nativeBuilder = FilaFramePacerBuilder_create()

        /** The desired frame rate in Hz (e.g. 60 or 30); must be greater than 0. */
        fun targetFrameRate(fps: Float): Builder = apply { FilaFramePacerBuilder_targetFrameRate(nativeBuilder, fps) }

        /** The target latency in nanoseconds, measured from [VsyncTick.baseTime] (default: 33ms). */
        fun latency(latency: Long): Builder = apply { FilaFramePacerBuilder_latency(nativeBuilder, latency) }

        /** The target latency in 60Hz frames (2 frames = 33.3ms). */
        fun latencyFrames(frames: Int): Builder = apply { FilaFramePacerBuilder_latencyFrames(nativeBuilder, frames) }

        /** Creates the FramePacer. */
        fun build(engine: Engine): FramePacer {
            val handle = FilaFramePacerBuilder_build(nativeBuilder, engine.nativeHandle)
            FilaFramePacerBuilder_destroy(nativeBuilder)
            return FramePacer(handle)
        }
    }

    /** The active pacing targets; setting it rescales subsequent frames (e.g. for thermal mitigation). */
    var configuration: Configuration
        get() = withHandle({ FilaFramePacerConfiguration_create() }, { FilaFramePacerConfiguration_destroy(it) }) { c ->
            FilaFramePacer_getConfiguration(nativeHandle, c)
            Configuration(FilaFramePacerConfiguration_getTargetFrameRate(c), longs { FilaFramePacerConfiguration_getLatency(c, it) })
        }
        set(value) = withHandle({ FilaFramePacerConfiguration_create() }, { FilaFramePacerConfiguration_destroy(it) }) { c ->
            FilaFramePacerConfiguration_setTargetFrameRate(c, value.targetFrameRate)
            FilaFramePacerConfiguration_setLatency(c, value.latency)
            FilaFramePacer_configure(nativeHandle, c)
        }

    /** Evaluates the pacing state for the upcoming frame; call it first thing in the VSYNC callback. */
    fun setupFrame(tick: VsyncTick): FrameStatus {
        val times = LongArray(tick.timelines.size * 2) { tick.timelines[it / 2].run { if (it % 2 == 0) expectedPresentationTime else deadline } }
        val status = withHandle({ FilaFramePacerVsyncTick_create() }, { FilaFramePacerVsyncTick_destroy(it) }) { t ->
            FilaFramePacerVsyncTick_setBaseTime(t, tick.baseTime)
            FilaFramePacerVsyncTick_setVsyncPeriod(t, tick.vsyncPeriod)
            tick.frameScheduleTime?.let { FilaFramePacerVsyncTick_setFrameScheduleTime(t, it) }
            times.usePinned { // the tick borrows the timelines until setupFrame returns
                FilaFramePacerVsyncTick_setTimelines(t, it, tick.timelines.size)
                FilaFramePacer_setupFrame(nativeHandle, t)
            }
        }
        return FrameStatus.entries[status + 2] // SKIPPED_SPURIOUS is -2
    }

    /**
     * Targets one more presentation frame without advancing the cadence, to recover queue depth when
     * [pacingStatus] is [PacingStatus.DISPLAY_STARVING]; follow it with [applyPresentationTime] and a frame.
     * Returns false, doing nothing, when the pipeline is already at its target latency.
     */
    fun setupExtraFrame(): Boolean = FilaFramePacer_setupExtraFrame(nativeHandle)

    /**
     * Whether the GPU has fallen behind; if so, the state [setupFrame] advanced is rolled back and the frame should be
     * skipped with [Renderer.skipFrame], without [applyPresentationTime] or [Renderer.beginFrame].
     */
    fun hasGpuFallenBehind(renderer: Renderer): Boolean = FilaFramePacer_hasGpuFallenBehind(nativeHandle, renderer.nativeHandle)

    /** Sets the computed presentation time, desired presentation time and rendering deadline on [renderer]; call before endFrame. */
    fun applyPresentationTime(renderer: Renderer) = FilaFramePacer_applyPresentationTime(nativeHandle, renderer.nativeHandle)

    /** The presentation time computed by the last [setupFrame], for judder-free animation. */
    val expectedPresentationTime: Long get() = longs { FilaFramePacer_getExpectedPresentationTime(nativeHandle, it) }

    /** The time by which the last [setupFrame]'s frame must finish rendering to make its presentation. */
    val renderingDeadline: Long get() = longs { FilaFramePacer_getRenderingDeadline(nativeHandle, it) }

    /** The target presentation time minus the frame's base time, in nanoseconds. */
    val effectiveLatency: Long get() = longs { FilaFramePacer_getEffectiveLatency(nativeHandle, it) }

    /** The pipeline's flow control status. */
    val pacingStatus: PacingStatus get() = when (FilaFramePacer_getPacingStatus(nativeHandle)) {
        -1 -> PacingStatus.DISPLAY_STARVING
        1 -> PacingStatus.DISPLAY_STUFFED
        else -> PacingStatus.STEADY
    }

    /** Drops the relative pacing state and re-anchors to the target latency on the next frame. */
    fun resetPacing() = FilaFramePacer_resetPacing(nativeHandle)

    /** The pacing frame rate in use: the target, matched to the display's cadence and clamped to its refresh rate. */
    val selectedFrameRate: Float get() = FilaFramePacer_getSelectedFrameRate(nativeHandle)

    /** Whether [selectedFrameRate] is an exact integer fraction of the display refresh rate (false for 45 FPS on 60Hz). */
    val isExactFrameRateAchieved: Boolean get() = FilaFramePacer_isExactFrameRateAchieved(nativeHandle)

    private inline fun longs(fill: (NativePointer) -> Unit): Long = LongArray(1).also { it.usePinned(fill) }[0]
}
