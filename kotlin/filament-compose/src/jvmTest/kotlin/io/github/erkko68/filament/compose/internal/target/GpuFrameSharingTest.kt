package io.github.erkko68.filament.compose.internal.target

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.View
import io.github.erkko68.filament.compose.ExperimentalGpuToGpuFrameSharing
import io.github.erkko68.filament.compose.FilamentComposeDesktop
import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
import org.jetbrains.skia.Image
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * The safety net around experimental GPU-to-GPU frame sharing: whatever goes wrong on the GPU path, at creation or
 * in the middle of a frame, the view keeps rendering through CPU readback. No GPU needed: the targets are fakes.
 */
@OptIn(ExperimentalGpuToGpuFrameSharing::class)
class GpuFrameSharingTest : ComposeTestFixture() {
    private val realErr = System.err
    private val err = ByteArrayOutputStream()

    // GpuFrameSharing latches a failure for the whole session, which here is the whole test JVM.
    private fun resetSession() {
        GpuFrameSharing::class.java.getDeclaredField("failed").apply { isAccessible = true }.setBoolean(GpuFrameSharing, false)
        (GpuFrameSharing::class.java.getDeclaredField("warnings").apply { isAccessible = true }.get(GpuFrameSharing) as MutableSet<*>).clear()
    }

    @BeforeTest
    fun optIn() {
        resetSession()
        FilamentComposeDesktop.isGpuToGpuFrameSharingEnabled = true
        System.setErr(PrintStream(err, true))
    }

    @AfterTest
    fun optOut() {
        System.setErr(realErr)
        FilamentComposeDesktop.isGpuToGpuFrameSharingEnabled = false
        resetSession()
    }

    private class FakeTarget(override val bottomUp: Boolean, private val failsAtFrame: Int = Int.MAX_VALUE, private val failsToClose: Boolean = false) : OffscreenTarget {
        var frames = 0
        var closed = 0
        override fun renderFrame(renderer: Renderer, view: View, frameTimeNanos: Long): Image? {
            frames++
            if (frames >= failsAtFrame) throw IllegalStateException("the GPU went away")
            return null
        }
        override fun close() {
            closed++
            if (failsToClose) throw IllegalStateException("and won't close")
        }
    }

    @Test
    fun sharingIsOptInPerSessionAndPerEngine() {
        FilamentComposeDesktop.isGpuToGpuFrameSharingEnabled = false
        assertFalse(GpuFrameSharing.enabled)
        FilamentComposeDesktop.isGpuToGpuFrameSharingEnabled = true
        assertTrue(GpuFrameSharing.enabled)

        // Only engines created for it share frames; one the app made itself keeps reading back.
        assertFalse(GpuFrameSharing.enabledFor(engine))
        GpuFrameSharing.optIn(engine)
        assertTrue(GpuFrameSharing.enabledFor(engine))
        FilamentComposeDesktop.isGpuToGpuFrameSharingEnabled = false
        assertFalse(GpuFrameSharing.enabledFor(engine))
    }

    @Test
    fun anUnavailablePathWarnsOnceAndStaysOn() {
        assertEquals("gpu", GpuFrameSharing.guard("testing", null, engine, { "gpu" }, { "readback" }))
        repeat(2) {
            assertEquals("readback", GpuFrameSharing.guard("testing", null, engine, { unavailable("software rendering") }, { "readback" }))
        }
        assertTrue(GpuFrameSharing.enabled, "an unsupported setup isn't a failure")
        val printed = err.toString()
        assertEquals(1, Regex("using CPU readback: software rendering").findAll(printed).count(), "warned once, got: $printed")
    }

    @Test
    fun aFailureIsReportedAndTurnsSharingOffForTheSession() {
        GpuFrameSharing.optIn(engine)
        val failures = listOf<() -> String>({ throw IllegalStateException("boom") }, { throw UnsatisfiedLinkError("no such symbol") })
        for (failure in failures) {
            resetSession()
            err.reset()
            assertEquals("readback", GpuFrameSharing.guard("rendering a frame", null, engine, failure, { "readback" }))
            assertFalse(GpuFrameSharing.enabled)
            assertFalse(GpuFrameSharing.enabledFor(engine))
            val report = err.toString()
            for (expected in listOf("failed while rendering a frame", "issues/new", "Filament backend", "NOOP", "at ")) {
                assertTrue(expected in report, "the report should contain \"$expected\":\n$report")
            }
        }
    }

    /** Without a window Compose could be sharing with, every OS's GPU path bows out and the view reads back. */
    @Test
    fun targetsAreReadbackUnlessTheGpuPathIsThere() {
        OffscreenTarget(engine, null, 4, 4, transparent = false).use { assertIs<ReadbackOffscreenTarget>(it, "engine not opted in") }
        GpuFrameSharing.optIn(engine)
        OffscreenTarget(engine, null, 4, 4, transparent = true).use { assertIs<ReadbackOffscreenTarget>(it, "no GPU path without a window") }
        assertTrue(GpuFrameSharing.enabled, "bowing out is not a failure")
        assertTrue("using CPU readback" in err.toString())
    }

    @Test
    fun aTargetFailingMidFrameIsReplacedByReadback() {
        val renderer = engine.createRenderer()
        val view = engine.createView()
        val gpu = FakeTarget(bottomUp = true, failsAtFrame = 3)
        val readbacks = mutableListOf<FakeTarget>()
        val target = FallbackOffscreenTarget(gpu, null, engine) { FakeTarget(bottomUp = false).also { readbacks += it } }

        repeat(2) { target.renderFrame(renderer, view, 0L) }
        assertEquals(2, gpu.frames)
        assertTrue(target.bottomUp)
        assertTrue(readbacks.isEmpty(), "no readback target while the GPU path works")

        // The frame that fails is still rendered, by the readback that takes over.
        target.renderFrame(renderer, view, 0L)
        val readback = readbacks.single()
        assertEquals(1, readback.frames)
        assertEquals(1, gpu.closed)
        assertFalse(target.bottomUp, "row order follows the target now in use")
        assertFalse(GpuFrameSharing.enabled)

        repeat(3) { target.renderFrame(renderer, view, 0L) }
        assertEquals(3, gpu.frames, "the failed target is never used again")
        assertEquals(4, readback.frames)

        target.close()
        assertEquals(1, readback.closed)
        assertEquals(1, gpu.closed, "the failed target was closed when it was replaced, not again")
        engine.destroy(view)
        engine.destroy(renderer)
    }

    /** One view failing turns sharing off for the session: the others switch over on their next frame, unprompted. */
    @Test
    fun otherViewsFollowAFailureAndASecondFailureToCloseIsSurvived() {
        val renderer = engine.createRenderer()
        val view = engine.createView()
        val failing = FakeTarget(bottomUp = true, failsAtFrame = 1, failsToClose = true)
        val healthy = FakeTarget(bottomUp = true)
        val first = FallbackOffscreenTarget(failing, null, engine) { FakeTarget(bottomUp = false) }
        var healthyReadback: FakeTarget? = null
        val second = FallbackOffscreenTarget(healthy, null, engine) { FakeTarget(bottomUp = false).also { healthyReadback = it } }

        second.renderFrame(renderer, view, 0L)
        assertEquals(1, healthy.frames)

        first.renderFrame(renderer, view, 0L) // fails, and fails to close: still replaced
        assertEquals(1, failing.closed)
        assertFalse(first.bottomUp)

        second.renderFrame(renderer, view, 0L)
        assertEquals(1, healthy.frames, "a working target is retired too once sharing is off")
        assertEquals(1, healthy.closed)
        assertEquals(1, healthyReadback?.frames)

        first.close()
        second.close()
        engine.destroy(view)
        engine.destroy(renderer)
    }
}
