package io.github.erkko68.filament.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.compose.internal.FilamentSurface
import io.github.erkko68.filament.compose.scene.ApplyIndirectLight
import io.github.erkko68.filament.compose.scene.ApplySkybox
import io.github.erkko68.filament.compose.scene.CameraState
import io.github.erkko68.filament.compose.scene.IndirectLightState
import io.github.erkko68.filament.compose.scene.SkyboxState
import io.github.erkko68.filament.compose.scene.rememberCameraState

/**
 * A Filament rendering surface with a declarative scene DSL.
 *
 * For full programmatic control over the camera (e.g. reading the view matrix, driving
 * it from non-composable code), pass a [rememberCameraState] as [cameraState]. Otherwise
 * a default state is created and any inner [scene.Camera] composable will drive it.
 *
 * @param engine Optional shared engine. If null, a dedicated engine is created and destroyed
 *   with this composable. Pass a [rememberFilamentEngine] value to share an engine across views.
 * @param cameraState Hoisted camera state. The default constructs a new state per view.
 * @param skyboxState Optional hoisted skybox state. Null = no skybox (the default).
 * @param indirectLightState Optional hoisted IBL state. Null = no IBL (the default).
 * @param postProcessingEnabled Master switch for all post-processing effects.
 * @param frustumCullingEnabled Skip rendering of objects outside the camera frustum.
 * @param shadowingEnabled Allow lights to cast and receive shadows.
 * @param screenSpaceRefractionEnabled Enable screen-space refraction for refractive materials.
 * @param stencilBufferEnabled Allocate a stencil buffer (required for stencil-based effects).
 * @param content Scene composables: [scene.Light], [scene.GltfInstance], post-processing
 *   composables, etc. Use [FilamentEffect] to access raw Filament objects.
 */
@Composable
fun FilamentView(
    modifier: Modifier = Modifier,
    engine: Engine? = null,
    cameraState: CameraState = rememberCameraState(),
    skyboxState: SkyboxState? = null,
    indirectLightState: IndirectLightState? = null,
    postProcessingEnabled: Boolean = true,
    frustumCullingEnabled: Boolean = true,
    shadowingEnabled: Boolean = true,
    screenSpaceRefractionEnabled: Boolean = false,
    stencilBufferEnabled: Boolean = false,
    content: @Composable () -> Unit = {},
) {
    val activeEngine = engine ?: rememberFilamentEngine()

    val renderer = remember(activeEngine) { activeEngine.createRenderer() }
    val scene    = remember(activeEngine) { activeEngine.createScene() }
    val view     = remember(activeEngine) { activeEngine.createView() }
    val camera   = remember(activeEngine) { activeEngine.createCamera() }

    // Wire up scene/camera and view options. Re-runs only when the underlying objects
    // or option toggles change.
    remember(view, scene, camera, postProcessingEnabled, frustumCullingEnabled,
             shadowingEnabled, screenSpaceRefractionEnabled, stencilBufferEnabled) {
        view.scene = scene
        view.camera = camera
        view.isPostProcessingEnabled = postProcessingEnabled
        view.isFrustumCullingEnabled = frustumCullingEnabled
        view.isShadowingEnabled = shadowingEnabled
        view.isScreenSpaceRefractionEnabled = screenSpaceRefractionEnabled
        view.isStencilBufferEnabled = stencilBufferEnabled
    }

    // Attach the hoisted state to the underlying Filament Camera so that
    // CameraState.viewMatrix / projectionMatrix become readable.
    DisposableEffect(cameraState, camera) {
        cameraState.attachedCamera = camera
        onDispose { cameraState.attachedCamera = null }
    }

    // Snapshot the camera state during composition so the SideEffect re-applies it
    // whenever any field changes. Reads of the state's snapshot fields here register
    // recomposition subscriptions.
    val snapshot = cameraState.snapshot()
    SideEffect {
        snapshot.applyTo(camera, cameraState.aspect)
    }

    val onResize: (Double) -> Unit = remember(camera, cameraState) {
        { aspect ->
            cameraState.aspect = aspect
            cameraState.snapshot().applyTo(camera, aspect)
        }
    }

    DisposableEffect(activeEngine) {
        onDispose {
            activeEngine.destroyRenderer(renderer)
            activeEngine.destroyScene(scene)
            activeEngine.destroyView(view)
            activeEngine.destroyCamera(camera)
        }
    }

    CompositionLocalProvider(
        LocalFilamentEngine   provides activeEngine,
        LocalFilamentScene    provides scene,
        LocalFilamentCamera   provides camera,
        LocalFilamentView     provides view,
        LocalFilamentRenderer provides renderer,
    ) {
        if (skyboxState != null)        ApplySkybox(skyboxState, activeEngine, scene)
        if (indirectLightState != null) ApplyIndirectLight(indirectLightState, activeEngine, scene)
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
