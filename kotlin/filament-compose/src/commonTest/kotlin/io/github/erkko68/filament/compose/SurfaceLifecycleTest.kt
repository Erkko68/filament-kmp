package io.github.erkko68.filament.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.scene.LinearColor
import io.github.erkko68.filament.compose.scene.Skybox
import io.github.erkko68.filament.compose.testutils.skippedComposeTest
import io.github.erkko68.filament.compose.testutils.surfaceTest
import io.github.erkko68.filament.testsupport.TestEnv
import io.github.erkko68.filament.testsupport.TestTarget
import kotlin.test.Test

/**
 * Views coming and going on each platform's real surface: a SurfaceView or TextureView, a UIView, a canvas.
 * The platform calls back into a surface on its own schedule, and a call that lands after the Filament
 * objects are destroyed is a native crash, so finishing is the assertion.
 */
class SurfaceLifecycleTest {

    // ── Leaving ─────────────────────────────────────────────────────────────────────────────────

    // These two own their engine: a late call then hits a destroyed engine, which crashes instead of
    // quietly using freed memory.
    @Test
    fun aSurfaceComposedForOneFrame() = surfaceTest { frame ->
        if (frame % 12 == 0) FilamentSceneView(Modifier.fillMaxSize()) { DirectionalLight() }
    }

    @Test
    fun aSurfaceLeavingTheFrameAfterAResize() = surfaceTest { frame ->
        if (frame % 20 != 19) FilamentSceneView(Modifier.size((100 + 10 * (frame % 20)).dp)) { DirectionalLight() }
    }

    @Test
    fun aSurfaceLeavingAnEngineHoistedAboveIt() = surfaceTest { frame ->
        val engine = rememberFilamentEngine()
        if (frame % 6 < 3) Scene(engine)
    }

    @Test
    fun aPausedSurfaceLeaving() = surfaceTest { frame ->
        val engine = rememberFilamentEngine()
        if (frame % 8 < 4) Scene(engine, renderingEnabled = false)
    }

    // ── Rebuilt in place ────────────────────────────────────────────────────────────────────────

    @Test
    fun aViewSwappedForASceneViewInPlace() = surfaceTest { frame ->
        val engine = rememberFilamentEngine()
        val scene = rememberFilamentScene(engine) { DirectionalLight() }
        if (frame / 5 % 2 == 0) FilamentView(scene, Modifier.fillMaxSize()) else Scene(engine)
    }

    // The Android emulator's shader compiler rejects Filament's translucent FXAA program, which aborts the process.
    @Test
    fun aSurfaceTogglingTransparency() =
        if (TestEnv.target == TestTarget.ANDROID && TestEnv.emulatedGpu) skippedComposeTest() else surfaceTest { frame ->
            val engine = rememberFilamentEngine()
            Scene(engine, transparent = frame / 4 % 2 == 0)
        }

    @Test
    fun aViewMovingBetweenScenes() = surfaceTest { frame ->
        val engine = rememberFilamentEngine()
        val first = rememberFilamentScene(engine) { DirectionalLight() }
        val second = rememberFilamentScene(engine) {}
        FilamentView(if (frame / 5 % 2 == 0) first else second, Modifier.fillMaxSize())
    }

    // ── Several engines ─────────────────────────────────────────────────────────────────────────

    // The skybox gives each engine a program to compile: on web that fails on another engine's GL context.
    @Test
    fun anEngineReplacedInPlace() = surfaceTest { frame ->
        key(frame / 10) { Sky(Modifier.fillMaxSize()) }
    }

    @Test
    fun enginesSideBySide() = surfaceTest { frame ->
        Column {
            Sky(Modifier.weight(1f))
            if (frame % 20 < 10) Sky(Modifier.weight(1f))
        }
    }

    @Composable
    private fun Sky(modifier: Modifier) = FilamentSceneView(modifier) {
        Skybox(LinearColor(0.1f, 0.125f, 0.15f))
        DirectionalLight()
    }

    @Composable
    private fun Scene(
        engine: Engine,
        modifier: Modifier = Modifier.fillMaxSize(),
        transparent: Boolean = false,
        renderingEnabled: Boolean = true,
    ) = FilamentSceneView(modifier, engine, transparent = transparent, renderingEnabled = renderingEnabled) { DirectionalLight() }
}
