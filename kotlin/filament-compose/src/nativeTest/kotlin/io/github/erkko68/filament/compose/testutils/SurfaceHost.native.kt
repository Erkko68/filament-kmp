@file:OptIn(ExperimentalForeignApi::class)

package io.github.erkko68.filament.compose.testutils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.window.ComposeUIViewController
import io.github.erkko68.filament.testsupport.TestEnv
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSDate
import platform.Foundation.NSRunLoop
import platform.Foundation.dateWithTimeIntervalSinceNow
import platform.Foundation.runUntilDate
import platform.UIKit.UIWindow
import kotlin.test.assertTrue

// A real window: Compose's test host has no UIKit container, so a UIKitView cannot be composed in it. Compose
// applies an interop view's frame and its removal only when the frame that changed them is presented, which
// is what lets UIKit lay a surface out after its View is destroyed.
actual fun surfaceTest(frames: Int, content: @Composable (frame: Int) -> Unit) {
    if (!TestEnv.gpuBackendAvailable) return
    var seen = 0
    val window = UIWindow(frame = CGRectMake(0.0, 0.0, 390.0, 844.0))
    // A test binary has no Info.plist to check.
    window.rootViewController = ComposeUIViewController(configure = { enforceStrictPlistSanityCheck = false }) {
        var frame by remember { mutableIntStateOf(0) }
        LaunchedEffect(Unit) { while (true) withFrameNanos { frame++; seen = frame } }
        if (frame <= frames) content(frame)
    }
    window.hidden = false
    repeat(600) { // 30 s at most
        if (seen <= frames + 2) pumpRunLoop(0.05)
    }
    window.hidden = true
    window.rootViewController = null
    pumpRunLoop(0.2)
    assertTrue(seen > frames, "the window should have run $frames frames, ran $seen")
}

private fun pumpRunLoop(seconds: Double) = NSRunLoop.currentRunLoop.runUntilDate(NSDate.dateWithTimeIntervalSinceNow(seconds))

actual typealias IgnoreWithoutViewInterop = kotlin.test.Ignore
