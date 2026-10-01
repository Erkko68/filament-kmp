package io.github.erkko68.filament

import io.github.erkko68.filament.FramePipelineEstimator.PacingSizing
import io.github.erkko68.filament.FramePipelineEstimator.TargetPercentile
import io.github.erkko68.filament.FramePipelineEstimator.Workload
import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals

class FramePipelineEstimatorTest : FilamentTestFixture() {
    private val ms = 1_000_000L
    private val period = 16_666_667L

    // Constant stages (no variance, so any Z gives the same answer): main CPU 6ms, backend 4ms, GPU 10ms.
    private val history = List(8) { i ->
        val vsync = 1_000 * ms + i * period
        Renderer.FrameInfo(
            frameId = i + 1, gpuFrameDuration = 10 * ms, denoisedGpuFrameDuration = 10 * ms,
            beginFrame = vsync + 2 * ms, endFrame = vsync + 6 * ms,
            backendBeginFrame = vsync + 6 * ms, backendEndFrame = vsync + 10 * ms,
            gpuFrameComplete = vsync + 20 * ms, vsync = vsync, displayPresent = -1, presentDeadline = -1,
            displayPresentInterval = period, compositionToPresentLatency = -1, expectedPresentLatency = -1,
            frameScheduleTime = 0,
        )
    }

    @Test
    fun testZScores() {
        assertEquals(0.0, FramePipelineEstimator.getZScore(TargetPercentile.P50))
        assertEquals(1.282, FramePipelineEstimator.getZScore(TargetPercentile.P90))
        assertEquals(1.645, FramePipelineEstimator.getZScore(TargetPercentile.P95))
    }

    @Test
    fun testWorkloadIsTheSlowestStage() {
        assertEquals(Workload(10 * ms, 100f), FramePipelineEstimator.estimateWorkload(history))
        assertEquals(Workload(10 * ms, 100f), FramePipelineEstimator.estimateWorkload(history, 2.0))
        assertEquals(Workload(), FramePipelineEstimator.estimateWorkload(emptyList()))
    }

    @Test
    fun testPacingCoversTheTransitTime() {
        // 20ms in transit needs 2 frames of 16.67ms, leaving 13.33ms of slack.
        val sizing = FramePipelineEstimator.estimatePacing(history, period, TargetPercentile.P95)
        assertEquals(2, sizing.latencyFrames)
        assertEquals(2 * period - 20 * ms, sizing.safeDelayDuration, 1)
        assertEquals(sizing, FramePipelineEstimator.estimatePacing(history, period, 0.5))
        assertEquals(PacingSizing(), FramePipelineEstimator.estimatePacing(emptyList(), period))
    }

    private fun assertEquals(expected: Long, actual: Long, tolerance: Long) =
        kotlin.test.assertTrue(kotlin.math.abs(expected - actual) <= tolerance, "expected $expected, got $actual")
}
