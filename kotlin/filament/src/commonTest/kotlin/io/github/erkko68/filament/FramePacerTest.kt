package io.github.erkko68.filament

import io.github.erkko68.filament.FramePacer.Configuration
import io.github.erkko68.filament.FramePacer.FrameStatus
import io.github.erkko68.filament.FramePacer.HardwareTimeline
import io.github.erkko68.filament.FramePacer.VsyncTick
import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FramePacerTest : FilamentTestFixture() {
    private val ms = 1_000_000L
    private val period = 16_666_667L
    private val t0 = 1_000 * ms

    @Test
    fun testConfiguration() {
        val pacer = FramePacer.Builder().targetFrameRate(30f).latency(50 * ms).build(engine)
        assertEquals(Configuration(30f, 50 * ms), pacer.configuration)
        pacer.configuration = Configuration()
        assertEquals(Configuration(60f, 33_333_333), pacer.configuration)
        assertTrue(engine.destroy(pacer))

        val frames = FramePacer.Builder().latencyFrames(3).build(engine)
        assertTrue(abs(frames.configuration.latency - 50 * ms) < ms)
        engine.destroy(frames)
    }

    @Test
    fun testHalfRateSkipsEveryOtherVsync() {
        val pacer = FramePacer.Builder().targetFrameRate(30f).latency(50 * ms).build(engine)
        assertEquals(FrameStatus.ACCEPTED, pacer.setupFrame(VsyncTick(t0, period, t0)))
        assertEquals(30f, pacer.selectedFrameRate, 0.1f)
        assertTrue(pacer.isExactFrameRateAchieved)
        assertTrue(pacer.renderingDeadline < pacer.expectedPresentationTime)
        assertEquals(FrameStatus.SKIPPED_SPURIOUS, pacer.setupFrame(VsyncTick(t0 + period, period, t0 + period)))
        assertEquals(FrameStatus.ACCEPTED, pacer.setupFrame(VsyncTick(t0 + 2 * period, period, t0 + 2 * period)))
        engine.destroy(pacer)
    }

    @Test
    fun testSnapsToHardwareTimeline() {
        val pacer = FramePacer.Builder().targetFrameRate(60f).latency(50 * ms).build(engine)
        // The ideal presentation is t0 + 50ms; the closest timeline the display offers is 2ms early.
        val timelines = listOf(32 * ms, 48 * ms, 64 * ms).map { HardwareTimeline(t0 + it, t0 + it - period) }
        assertEquals(FrameStatus.ACCEPTED, pacer.setupFrame(VsyncTick(t0, period, t0, timelines)))
        assertEquals(t0 + 48 * ms, pacer.expectedPresentationTime)
        assertEquals(t0 + 48 * ms - period, pacer.renderingDeadline)
        assertEquals(48 * ms, pacer.effectiveLatency)
        assertEquals(FramePacer.PacingStatus.STEADY, pacer.pacingStatus)

        val renderer = engine.createRenderer()
        assertFalse(pacer.hasGpuFallenBehind(renderer))
        pacer.applyPresentationTime(renderer)
        pacer.setupExtraFrame()
        pacer.resetPacing()
        engine.destroy(renderer)
        engine.destroy(pacer)
    }
}
