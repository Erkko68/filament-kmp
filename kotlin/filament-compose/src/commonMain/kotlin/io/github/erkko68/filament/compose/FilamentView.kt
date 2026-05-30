package io.github.erkko68.filament.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.erkko68.filament.compose.internal.FilamentSurface
import io.github.erkko68.filament.compose.scene.CameraState
import io.github.erkko68.filament.compose.scene.PostProcessing
import io.github.erkko68.filament.compose.scene.applyTo
import io.github.erkko68.filament.compose.scene.rememberCameraState

/**
 * A viewport onto a [FilamentScene]. Each `FilamentView` owns one Filament `View`, `Camera`,
 * `Renderer`, and platform surface, and renders the given [scene] through them. Place several
 * `FilamentView`s — anywhere in your layout — to render one scene through different cameras
 * and post-processing setups.
 *
 * `FilamentView` is a leaf: the world is declared in [rememberFilamentScene] and the look of
 * this viewport is configured by value ([cameraState], [postProcessing], the render flags).
 * For imperative access (picking, raw `View`/`Renderer`) pass a [rememberFilamentViewState].
 *
 * ```kotlin
 * val scene = rememberFilamentScene { Light(...); GltfInstance(duck) }
 * Row {
 *     FilamentView(scene, Modifier.weight(1f), cameraState = cam1,
 *         postProcessing = PostProcessing(bloom = Bloom(strength = 0.2f)))
 *     FilamentView(scene, Modifier.weight(1f), cameraState = cam2)
 * }
 * ```
 *
 * @param scene The scene to render, from [rememberFilamentScene]. Supplies the shared engine.
 * @param cameraState Hoisted camera state. The default constructs a new state per view.
 * @param viewState Hoisted handle exposing the live `View`/`Renderer` and `pick()`.
 * @param postProcessing Per-view post-processing and render-quality configuration.
 * @param frustumCullingEnabled Skip rendering of objects outside the camera frustum.
 * @param shadowingEnabled Allow lights to cast and receive shadows.
 * @param screenSpaceRefractionEnabled Enable screen-space refraction for refractive materials.
 * @param stencilBufferEnabled Allocate a stencil buffer (required for stencil-based effects).
 */
@Composable
fun FilamentView(
    scene: FilamentScene,
    modifier: Modifier = Modifier,
    cameraState: CameraState = rememberCameraState(),
    viewState: FilamentViewState = rememberFilamentViewState(),
    postProcessing: PostProcessing = PostProcessing(),
    frustumCullingEnabled: Boolean = true,
    shadowingEnabled: Boolean = true,
    screenSpaceRefractionEnabled: Boolean = false,
    stencilBufferEnabled: Boolean = false,
) {
    val engine        = scene.engine
    val filamentScene = scene.scene

    val renderer = remember(engine) { engine.createRenderer() }
    val view     = remember(engine) { engine.createView() }
    val camera   = remember(engine) { engine.createCamera() }

    // Wire the scene/camera onto the view and apply the render flags. Re-runs only when the
    // underlying objects or flags change.
    remember(view, filamentScene, camera, frustumCullingEnabled,
             shadowingEnabled, screenSpaceRefractionEnabled, stencilBufferEnabled) {
        view.scene = filamentScene
        view.camera = camera
        view.isFrustumCullingEnabled = frustumCullingEnabled
        view.isShadowingEnabled = shadowingEnabled
        view.isScreenSpaceRefractionEnabled = screenSpaceRefractionEnabled
        view.isStencilBufferEnabled = stencilBufferEnabled
    }

    // Apply post-processing as a value. Re-applies whenever the config changes; the allocated
    // ColorGrading (if any) is destroyed on dispose / before re-apply.
    DisposableEffect(view, postProcessing, engine) {
        val colorGrading = postProcessing.applyTo(view, engine)
        onDispose { colorGrading?.let { engine.destroyColorGrading(it) } }
    }

    // Expose the live View/Renderer through the hoisted handle.
    DisposableEffect(viewState, view, renderer) {
        viewState.attach(view, renderer)
        onDispose { viewState.detach() }
    }

    // Attach the hoisted camera state so CameraState.viewMatrix / projectionMatrix are readable.
    DisposableEffect(cameraState, camera) {
        cameraState.attachedCamera = camera
        onDispose { cameraState.attachedCamera = null }
    }

    // Snapshot the camera state during composition so the SideEffect re-applies it whenever
    // any field changes. Reads here register recomposition subscriptions.
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

    // The scene is owned by the FilamentScene handle, not the view.
    DisposableEffect(engine) {
        onDispose {
            engine.destroyRenderer(renderer)
            engine.destroyView(view)
            engine.destroyCamera(camera)
        }
    }

    FilamentSurface(
        modifier = modifier,
        engine   = engine,
        renderer = renderer,
        view     = view,
        onResize = onResize,
    )
}
