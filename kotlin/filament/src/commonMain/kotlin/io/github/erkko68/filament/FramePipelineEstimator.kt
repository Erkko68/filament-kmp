package io.github.erkko68.filament

import io.github.erkko68.filament.capi.*
import io.github.erkko68.filament.interop.*

/**
 * Estimates the ideal frame rate (throughput) and latency (pipeline depth) from [Renderer.getFrameInfoHistory],
 * modelling the main CPU, backend and GPU stages by their mean plus Z standard deviations:
 * ```
 * val history = renderer.getFrameInfoHistory(renderer.maxFrameHistorySize)
 * val workload = FramePipelineEstimator.estimateWorkload(history)
 * val sizing = FramePipelineEstimator.estimatePacing(history, pacingPeriod = 16_666_666)
 * pacer.configuration = FramePacer.Configuration(workload.idealFrameRate, sizing.latencyFrames * 16_666_666L)
 * ```
 */
object FramePipelineEstimator {
    init { Filament.init() } // CPU-only, usable before any Engine exists

    /**
     * The confidence that a frame fits the estimated budget; 1 - P is the theoretical miss rate. In practice
     * stutters are rarer: spikes cluster, and the [FramePacer]'s queue depth absorbs them.
     */
    enum class TargetPercentile {
        /** Mean workload, Z = 0: a miss every other frame. */
        P50,
        /** Z = 1.282: a miss every 10 frames. */
        P90,
        /** Z = 1.645: a miss every 20 frames. */
        P95,
    }

    /** The ideal throughput: the bottleneck stage's duration in nanoseconds, and its rate in Hz. */
    data class Workload(val idealFrameDuration: Long = 16_666_666, val idealFrameRate: Float = 60f)

    /** The recommended pipeline depth in frames, and the slack in nanoseconds before CPU work must start. */
    data class PacingSizing(val latencyFrames: Int = 2, val safeDelayDuration: Long = 0)

    /** The normal distribution Z-score of [targetPercentile]. */
    fun getZScore(targetPercentile: TargetPercentile): Double = FilaFramePipelineEstimator_getZScore(targetPercentile.ordinal)

    /** The unthrottled throughput [history] supports at [targetPercentile]. */
    fun estimateWorkload(history: List<Renderer.FrameInfo>, targetPercentile: TargetPercentile = TargetPercentile.P90): Workload =
        workload { out -> history.useArray { h, n -> FilaFramePipelineEstimator_estimateWorkload_TargetPercentile(h, n, targetPercentile.ordinal, out) } }

    /** The unthrottled throughput [history] supports, [zScore] standard deviations above the mean. */
    fun estimateWorkload(history: List<Renderer.FrameInfo>, zScore: Double): Workload =
        workload { out -> history.useArray { h, n -> FilaFramePipelineEstimator_estimateWorkload_double(h, n, zScore, out) } }

    /** The latency and safe delay for pacing [history] every [pacingPeriod] nanoseconds, at [targetPercentile]. */
    fun estimatePacing(
        history: List<Renderer.FrameInfo>,
        pacingPeriod: Long,
        targetPercentile: TargetPercentile = TargetPercentile.P90,
    ): PacingSizing = pacing { out ->
        history.useArray { h, n -> FilaFramePipelineEstimator_estimatePacing_TargetPercentile(h, n, pacingPeriod, targetPercentile.ordinal, out) }
    }

    /** The latency and safe delay for pacing [history] every [pacingPeriod] nanoseconds, [zScore] deviations above the mean. */
    fun estimatePacing(history: List<Renderer.FrameInfo>, pacingPeriod: Long, zScore: Double): PacingSizing = pacing { out ->
        history.useArray { h, n -> FilaFramePipelineEstimator_estimatePacing_double(h, n, pacingPeriod, zScore, out) }
    }

    private inline fun List<Renderer.FrameInfo>.useArray(crossinline block: (NativePointer, Int) -> Unit) =
        useNative { handles -> interopScope { block(toInterop(handles), handles.size) } }

    private inline fun workload(fill: (NativePointer) -> Unit): Workload =
        withHandle({ FilaFramePipelineEstimatorWorkload_create() }, { FilaFramePipelineEstimatorWorkload_destroy(it) }) { w ->
            fill(w)
            val ns = LongArray(1).also { d -> d.usePinned { FilaFramePipelineEstimatorWorkload_getIdealFrameDuration(w, it) } }[0]
            Workload(ns, FilaFramePipelineEstimatorWorkload_getIdealFrameRate(w))
        }

    private inline fun pacing(fill: (NativePointer) -> Unit): PacingSizing =
        withHandle({ FilaFramePipelineEstimatorPacingSizing_create() }, { FilaFramePipelineEstimatorPacingSizing_destroy(it) }) { s ->
            fill(s)
            val ns = LongArray(1).also { d -> d.usePinned { FilaFramePipelineEstimatorPacingSizing_getSafeDelayDuration(s, it) } }[0]
            PacingSizing(FilaFramePipelineEstimatorPacingSizing_getLatencyFrames(s), ns)
        }
}
