package io.github.erkko68.filament.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.compose.internal.FilamentSurface
import io.github.erkko68.filament.compose.scene.CameraConfig
import io.github.erkko68.filament.compose.scene.Exposure
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.gltfio.AssetLoader
import io.github.erkko68.filament.gltfio.Gltfio
import io.github.erkko68.filament.gltfio.UbershaderProvider
import io.github.erkko68.filament.utils.Float2
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.Position

/**
 * A Filament rendering surface with a declarative scene DSL.
 *
 * @param engine Optional shared engine. If null, a dedicated engine is created and destroyed
 *   with this composable. Pass a [rememberFilamentEngine] value to share an engine across views.
 * @param postProcessingEnabled Master switch for all post-processing effects. Disabling this
 *   also disables tone-mapping, bloom, AO, etc. regardless of individual effect composables.
 * @param frustumCullingEnabled Skip rendering of objects outside the camera frustum. Disable
 *   only if you have a custom culling strategy.
 * @param shadowingEnabled Allow lights to cast and receive shadows. Disabling globally is a
 *   quick performance win when shadows are not needed.
 * @param screenSpaceRefractionEnabled Enable screen-space refraction for transparent materials
 *   that declare refraction. Has a GPU cost even for scenes without refractive objects.
 * @param stencilBufferEnabled Allocate a stencil buffer. Required for stencil-based effects
 *   such as outlines or masks. Off by default to save memory bandwidth.
 * @param content Scene composables: [scene.Skybox], [scene.Light], [scene.Camera],
 *   [scene.GltfModel], etc. Use [FilamentEffect] to access raw Filament objects.
 */
@Composable
fun FilamentView(
    modifier: Modifier = Modifier,
    engine: Engine? = null,
    postProcessingEnabled: Boolean = true,
    frustumCullingEnabled: Boolean = true,
    shadowingEnabled: Boolean = true,
    screenSpaceRefractionEnabled: Boolean = false,
    stencilBufferEnabled: Boolean = false,
    content: @Composable () -> Unit = {},
) {
    Gltfio.init()
    val activeEngine = engine ?: rememberFilamentEngine()

    val renderer = remember(activeEngine) { activeEngine.createRenderer() }
    val scene    = remember(activeEngine) { activeEngine.createScene() }
    val view     = remember(activeEngine) { activeEngine.createView() }
    val camera   = remember(activeEngine) { activeEngine.createCamera() }

    val defaultConfig = remember {
        CameraConfig(
            eye        = Position(0f, 1f, 10f),
            target     = Position(0f, 0f, 0f),
            up         = Direction(0f, 1f, 0f),
            projection = Projection.Perspective(),
            exposure   = Exposure(),
            shift      = Float2(0f, 0f),
            scaling    = Float2(1f, 1f),
        )
    }
    var cameraConfig by remember { mutableStateOf(defaultConfig) }

    remember(view, scene, camera, postProcessingEnabled, frustumCullingEnabled, shadowingEnabled, screenSpaceRefractionEnabled, stencilBufferEnabled) {
        view.scene = scene
        view.camera = camera
        view.isPostProcessingEnabled = postProcessingEnabled
        view.isFrustumCullingEnabled = frustumCullingEnabled
        view.isShadowingEnabled = shadowingEnabled
        view.isScreenSpaceRefractionEnabled = screenSpaceRefractionEnabled
        view.isStencilBufferEnabled = stencilBufferEnabled
        cameraConfig.applyTo(camera, 1.0)
    }

    val aspectRef       = remember { doubleArrayOf(1.0) }
    val cameraConfigRef = remember { Array<CameraConfig>(1) { cameraConfig } }
    SideEffect { cameraConfigRef[0] = cameraConfig }

    LaunchedEffect(cameraConfig) {
        cameraConfig.applyTo(camera, aspectRef[0])
    }

    val onResize: (Double) -> Unit = remember(camera) {
        { aspect ->
            aspectRef[0] = aspect
            cameraConfigRef[0].applyTo(camera, aspect)
        }
    }

    val gltfMaterials = remember(activeEngine) { UbershaderProvider(activeEngine) }
    val assetLoader   = remember(activeEngine) {
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
        LocalFilamentEngine         provides activeEngine,
        LocalFilamentScene          provides scene,
        LocalFilamentCamera         provides camera,
        LocalFilamentView           provides view,
        LocalFilamentRenderer       provides renderer,
        LocalAssetLoader            provides assetLoader,
        LocalCameraConfig           provides cameraConfig,
        LocalCameraConfigController provides { cameraConfig = it },
    ) {
        content()
        FilamentSurface(
            modifier = modifier,
            engine   = activeEngine,
            renderer = renderer,
            view     = view,
            onResize = onResize,
        )
    }
}
