@file:OptIn(ExperimentalForeignApi::class)

package io.github.erkko68.filament.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.scene.GltfInstance
import io.github.erkko68.filament.compose.scene.rememberGltfAsset
import io.github.erkko68.filament.compose.testutils.TestGlb
import io.github.erkko68.filament.testsupport.TestEnv
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSDate
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSRunLoop
import platform.Foundation.dateWithTimeIntervalSinceNow
import platform.Foundation.runUntilDate
import platform.UIKit.UIApplicationDidBecomeActiveNotification
import platform.UIKit.UIApplicationDidEnterBackgroundNotification
import platform.UIKit.UIApplicationDidReceiveMemoryWarningNotification
import platform.UIKit.UIApplicationWillEnterForegroundNotification
import platform.UIKit.UIApplicationWillResignActiveNotification
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * What UIKit does to a view on its own: backgrounding, rotating, hiding the window, swapping the controller.
 * None of it goes through the composition, and a call into a destroyed Filament object is a native crash, so
 * finishing is the assertion.
 */
class WindowLifecycleTest {
    private var frames = 0

    @Composable
    private fun World(transparent: Boolean, renderingEnabled: Boolean) {
        FilamentSceneView(Modifier.fillMaxSize(), transparent = transparent, renderingEnabled = renderingEnabled) {
            DirectionalLight()
            GltfInstance(rememberGltfAsset { TestGlb.getDuckGlbBytes() })
            OnFrame { frames++ }
        }
    }

    private fun controller(transparent: Boolean, renderingEnabled: Boolean = true): UIViewController =
        // A test binary has no Info.plist to check.
        ComposeUIViewController(configure = { enforceStrictPlistSanityCheck = false }) { World(transparent, renderingEnabled) }

    private fun pump(seconds: Double = 0.3) = NSRunLoop.currentRunLoop.runUntilDate(NSDate.dateWithTimeIntervalSinceNow(seconds))

    private fun post(name: String?) = NSNotificationCenter.defaultCenter.postNotificationName(name!!, null)

    private fun UIWindow.resize(width: Double, height: Double) = setFrame(CGRectMake(0.0, 0.0, width, height))

    private fun bothSurfaces(renderingEnabled: Boolean = true, body: (UIWindow, transparent: Boolean) -> Unit) {
        if (!TestEnv.gpuBackendAvailable) return
        for (transparent in listOf(false, true)) {
            val window = UIWindow(frame = CGRectMake(0.0, 0.0, 390.0, 844.0))
            window.rootViewController = controller(transparent, renderingEnabled)
            window.hidden = false
            pump(0.5)
            body(window, transparent)
            // The frame loop must have survived whatever the window went through.
            val before = frames
            pump(0.5)
            assertTrue(frames > before, "frames should still run (transparent=$transparent)")
            window.hidden = true
            window.rootViewController = null
            pump()
        }
    }

    @Test
    fun backgroundedAndForegrounded() = bothSurfaces { window, _ ->
        repeat(3) {
            post(UIApplicationWillResignActiveNotification)
            post(UIApplicationDidEnterBackgroundNotification)
            // The system lays the app out in the other orientation for its switcher snapshot.
            window.resize(844.0, 390.0)
            pump()
            window.resize(390.0, 844.0)
            post(UIApplicationDidReceiveMemoryWarningNotification)
            pump()
            post(UIApplicationWillEnterForegroundNotification)
            post(UIApplicationDidBecomeActiveNotification)
            pump()
        }
    }

    @Test
    fun rotatedInAStorm() = bothSurfaces { window, _ ->
        repeat(40) { i ->
            when (i % 4) {
                0 -> window.resize(844.0, 390.0)
                1 -> window.resize(390.0, 844.0)
                2 -> window.resize(0.0, 0.0)
                else -> window.resize(200.0 + i * 7, 300.0)
            }
            pump(if (i % 5 == 0) 0.2 else 0.01)
        }
        window.resize(390.0, 844.0)
    }

    @Test
    fun hiddenAndResizedWhileHidden() = bothSurfaces { window, _ ->
        repeat(4) {
            window.hidden = true
            window.resize(300.0 + 40 * it, 500.0)
            pump()
            window.hidden = false
            pump()
        }
    }

    @Test
    fun pausedAcrossWindowEvents() = bothSurfaces(renderingEnabled = false) { window, _ ->
        repeat(3) {
            post(UIApplicationDidEnterBackgroundNotification)
            window.resize(844.0, 390.0)
            pump()
            post(UIApplicationWillEnterForegroundNotification)
            post(UIApplicationDidBecomeActiveNotification)
            window.resize(390.0, 844.0)
            pump()
        }
    }

    @Test
    fun controllerSwappedAtEveryStage() = bothSurfaces { window, transparent ->
        // Before the first layout, mid-load, and rendering.
        for (delay in listOf(0.0, 0.03, 0.12, 0.5, 0.0, 0.03)) {
            window.rootViewController = controller(transparent)
            pump(delay)
        }
    }

    @Test
    fun viewPulledOutOfItsWindowAndPutBack() = bothSurfaces { window, _ ->
        val root = window.rootViewController!!.view
        repeat(3) {
            root.removeFromSuperview()
            pump()
            window.addSubview(root)
            window.resize(390.0 - 20 * it, 844.0)
            pump()
        }
    }

    @Test
    fun controllerDroppedWhileBackgrounded() {
        if (!TestEnv.gpuBackendAvailable) return
        for (transparent in listOf(false, true)) {
            val window = UIWindow(frame = CGRectMake(0.0, 0.0, 390.0, 844.0))
            window.rootViewController = controller(transparent)
            window.hidden = false
            pump(0.5)
            post(UIApplicationDidEnterBackgroundNotification)
            pump()
            window.rootViewController = null
            window.resize(844.0, 390.0)
            pump()
            post(UIApplicationWillEnterForegroundNotification)
            pump()
        }
    }
}
