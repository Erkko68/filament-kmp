@file:OptIn(ExperimentalForeignApi::class)

package io.github.erkko68.filament.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.testsupport.TestEnv
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSDate
import platform.Foundation.NSRunLoop
import platform.Foundation.dateWithTimeIntervalSinceNow
import platform.Foundation.runUntilDate
import platform.UIKit.UIWindow
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Surfaces leaving a real UIKit host. Compose applies an interop view's frame and its removal only when the
 * frame that changed them is presented, so UIKit can lay a surface out after its `View` is destroyed. A wrong
 * order is a native crash: finishing is the assertion.
 */
class IosSurfaceLifecycleTest {

    @Test
    fun aSurfaceLeavingTheFrameAfterAResize() = hosted { frame ->
        if (frame % 20 != 19) Scene(Modifier.size((100 + 10 * (frame % 20)).dp))
    }

    @Test
    fun aSurfaceComposedForOneFrame() = hosted { frame ->
        if (frame % 12 == 0) Scene(Modifier.fillMaxSize())
    }

    @Composable
    private fun Scene(modifier: Modifier) = FilamentSceneView(modifier) { DirectionalLight() }

    /** Shows [content] in a window for [frames] frames, handing it the frame number. */
    private fun hosted(frames: Int = 120, content: @Composable (frame: Int) -> Unit) {
        if (!TestEnv.gpuBackendAvailable) return
        var seen = 0
        val window = UIWindow(frame = CGRectMake(0.0, 0.0, 390.0, 844.0))
        window.rootViewController = ComposeUIViewController(configure = { enforceStrictPlistSanityCheck = false }) { // a test binary has no Info.plist
            var frame by remember { mutableIntStateOf(0) }
            LaunchedEffect(Unit) { while (true) withFrameNanos { frame++; seen = frame } }
            content(frame)
        }
        window.hidden = false
        repeat(600) { // 30 s at most
            if (seen < frames) NSRunLoop.currentRunLoop.runUntilDate(NSDate.dateWithTimeIntervalSinceNow(0.05))
        }
        window.hidden = true
        window.rootViewController = null
        NSRunLoop.currentRunLoop.runUntilDate(NSDate.dateWithTimeIntervalSinceNow(0.2))
        assertTrue(seen >= frames, "the host should have run $frames frames, ran $seen")
    }
}
