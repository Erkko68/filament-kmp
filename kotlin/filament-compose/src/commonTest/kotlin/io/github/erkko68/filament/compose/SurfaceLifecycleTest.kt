package io.github.erkko68.filament.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.testutils.surfaceTest
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

    @Test
    fun aSurfaceTogglingTransparency() = surfaceTest { frame ->
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

    @Composable
    private fun Scene(
        engine: Engine,
        modifier: Modifier = Modifier.fillMaxSize(),
        transparent: Boolean = false,
        renderingEnabled: Boolean = true,
    ) = FilamentSceneView(modifier, engine, transparent = transparent, renderingEnabled = renderingEnabled) { DirectionalLight() }
}
