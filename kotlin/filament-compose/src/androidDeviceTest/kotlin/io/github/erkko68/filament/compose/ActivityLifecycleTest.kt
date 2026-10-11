package io.github.erkko68.filament.compose

import android.app.Activity
import android.app.Application
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.scene.GltfInstance
import io.github.erkko68.filament.compose.scene.LinearColor
import io.github.erkko68.filament.compose.scene.SkyboxSource
import io.github.erkko68.filament.compose.scene.rememberGltfAsset
import io.github.erkko68.filament.compose.scene.rememberSkyboxState
import io.github.erkko68.filament.compose.testutils.TestGlb
import io.github.erkko68.filament.testsupport.TestEnv
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.fail

/**
 * What the activity does to a view on its own: stopping takes the surface away, a rotation rebuilds the whole
 * composition. A crash kills the run; a view that comes back blank fails the screenshot, on a real GPU.
 */
class ActivityLifecycleTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val app = instrumentation.targetContext.applicationContext as Application
    private var content: @Composable () -> Unit = {}

    // Sets the content on every instance, so a recreated activity gets it too.
    private val callbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            if (activity is ComponentActivity) activity.setContentView(ComposeView(activity).apply { setContent { content() } })
        }
        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityResumed(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {}
    }

    @BeforeTest
    fun register() = app.registerActivityLifecycleCallbacks(callbacks)

    @AfterTest
    fun unregister() = app.unregisterActivityLifecycleCallbacks(callbacks)

    @Composable
    private fun World(transparent: Boolean, renderingEnabled: Boolean = true) = FilamentSceneView(
        Modifier.fillMaxSize(),
        skyboxState = rememberSkyboxState(initialSource = SkyboxSource.Color(LinearColor(0f, 1f, 0f))),
        transparent = transparent,
        renderingEnabled = renderingEnabled,
    ) {
        DirectionalLight()
        GltfInstance(rememberGltfAsset { TestGlb.getDuckGlbBytes() })
    }

    private fun settle(ms: Long = 700) = Thread.sleep(ms)

    // Polls: a first frame can take seconds on a software-GPU emulator, but a view stuck blank stays blank.
    private fun assertShowing(what: String) {
        // Reading a Filament frame back segfaults CI's software-GPU emulator, so there it only has to survive.
        if (TestEnv.emulatedGpu) return settle(3_000)
        var pixel = 0
        val deadline = System.currentTimeMillis() + 15_000
        while (System.currentTimeMillis() < deadline) {
            settle(300)
            val shot = instrumentation.uiAutomation.takeScreenshot()
            pixel = shot.getPixel(shot.width / 2, shot.height / 4)
            if (Color.green(pixel) > 100 && Color.green(pixel) > Color.red(pixel) + 40) return
        }
        fail("$what: the view should show its green skybox, shows #${Integer.toHexString(pixel)}")
    }

    private fun bothSurfaces(renderingEnabled: Boolean = true, body: (ActivityScenario<ComponentActivity>, String) -> Unit) {
        for (transparent in listOf(false, true)) {
            content = { World(transparent, renderingEnabled) }
            ActivityScenario.launch(ComponentActivity::class.java).use { scenario ->
                val surface = if (transparent) "TextureView" else "SurfaceView"
                assertShowing("$surface at launch")
                body(scenario, surface)
            }
        }
    }

    @Test
    fun stoppedAndRestarted() = bothSurfaces { scenario, surface ->
        repeat(4) {
            scenario.moveToState(Lifecycle.State.CREATED)
            settle()
            scenario.moveToState(Lifecycle.State.RESUMED)
            assertShowing("$surface after restart ${it + 1}")
        }
    }

    @Test
    fun pausedAndStoppedAndRestarted() = bothSurfaces(renderingEnabled = false) { scenario, surface ->
        repeat(3) {
            scenario.moveToState(Lifecycle.State.CREATED)
            settle()
            scenario.moveToState(Lifecycle.State.RESUMED)
            assertShowing("paused $surface after restart ${it + 1}")
        }
    }

    @Test
    fun recreated() = bothSurfaces { scenario, surface ->
        repeat(3) {
            scenario.recreate()
            assertShowing("$surface after recreation ${it + 1}")
        }
    }

    @Test
    fun rotated() = bothSurfaces { scenario, surface ->
        for (orientation in listOf(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)) {
            scenario.onActivity { it.requestedOrientation = orientation }
            settle(1500)
            assertShowing("$surface rotated to $orientation")
        }
        scenario.onActivity { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT }
        settle(1500)
    }

    @Test
    fun finishedAtEveryStage() {
        for (transparent in listOf(false, true)) for (delay in listOf(0L, 30L, 120L, 600L)) {
            content = { World(transparent) }
            val scenario = ActivityScenario.launch(ComponentActivity::class.java)
            settle(delay) // before the surface exists, mid-load, and rendering
            scenario.close()
        }
    }

    @Test
    fun finishedWhileStopped() = bothSurfaces { scenario, _ ->
        scenario.moveToState(Lifecycle.State.CREATED)
        settle()
        scenario.moveToState(Lifecycle.State.DESTROYED)
    }
}
