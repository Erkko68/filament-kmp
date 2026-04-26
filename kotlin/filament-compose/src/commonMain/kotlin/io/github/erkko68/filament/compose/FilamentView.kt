package io.github.erkko68.filament.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.erkko68.filament.Camera
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.compose.internal.FilamentSurface
import io.github.erkko68.filament.gltfio.AssetLoader
import io.github.erkko68.filament.gltfio.UbershaderProvider

/**
 * A Filament rendering surface with a declarative scene DSL.
 *
 * @param engine Optional shared engine. If null, a dedicated engine is created and destroyed
 *   with this composable. Pass a [rememberFilamentEngine] value to share an engine across views.
 * @param content Scene composables: [ColorSkybox], [DirectionalLight], [PerspectiveCamera],
 *   [GltfModel], etc. Use [FilamentEffect] to access raw Filament objects.
 */
@Composable
fun FilamentView(
    modifier: Modifier = Modifier,
    engine: Engine? = null,
    content: @Composable () -> Unit = {},
) {
    val activeEngine = engine ?: rememberFilamentEngine()

    val renderer = remember(activeEngine) { activeEngine.createRenderer() }
    val scene = remember(activeEngine) { activeEngine.createScene() }
    val view = remember(activeEngine) { activeEngine.createView() }
    val camera = remember(activeEngine) { activeEngine.createCamera() }

    remember(view, scene, camera) {
        view.setScene(scene)
        view.setCamera(camera)
        // Defaults — overridden by PerspectiveCamera composable when present
        camera.setProjection(45.0, 1.0, 0.1, 100.0, Camera.Fov.VERTICAL)
        camera.lookAt(0.0, 1.0, 10.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0)
        camera.setExposure(16.0f, 1.0f / 125.0f, 100.0f)
        view.setPostProcessingEnabled(false)
    }

    // Shared gltf context — all GltfModel composables in this view share one AssetLoader
    // and UbershaderProvider so materials are deduplicated automatically.
    val gltfMaterials = remember(activeEngine) { UbershaderProvider(activeEngine) }
    val assetLoader = remember(activeEngine) {
        AssetLoader.create(activeEngine, gltfMaterials, activeEngine.getEntityManager())
    }

    DisposableEffect(activeEngine) {
        onDispose {
            AssetLoader.destroy(assetLoader)
            gltfMaterials.destroy()
            activeEngine.destroyRenderer(renderer)
            activeEngine.destroyScene(scene)
            activeEngine.destroyView(view)
            activeEngine.destroyCamera(camera)
        }
    }

    CompositionLocalProvider(
        LocalFilamentEngine provides activeEngine,
        LocalFilamentScene provides scene,
        LocalFilamentCamera provides camera,
        LocalFilamentView provides view,
        LocalFilamentRenderer provides renderer,
        LocalAssetLoader provides assetLoader,
    ) {
        content()
        FilamentSurface(
            modifier = modifier,
            engine = activeEngine,
            renderer = renderer,
            view = view,
            camera = camera,
        )
    }
}
