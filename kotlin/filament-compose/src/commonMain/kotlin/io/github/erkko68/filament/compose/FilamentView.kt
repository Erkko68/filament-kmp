package io.github.erkko68.filament.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.View.BlendMode
import io.github.erkko68.filament.compose.internal.FilamentSurface
import io.github.erkko68.filament.compose.scene.CameraState
import io.github.erkko68.filament.compose.scene.PostProcessing
import io.github.erkko68.filament.compose.scene.Shadows
import io.github.erkko68.filament.compose.scene.applyTo
import io.github.erkko68.filament.compose.scene.rememberCameraState
import io.github.erkko68.filament.ClearOptions

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
 * val scene = rememberFilamentScene { DirectionalLight(); GltfInstance(duck) }
 * Row {
 *     FilamentView(scene, Modifier.weight(1f), cameraState = cam1,
 *         postProcessing = PostProcessing(bloom = Bloom(strength = 0.2f)))
 *     FilamentView(scene, Modifier.weight(1f), cameraState = cam2)
 * }
 * ```
 *
 * @param scene The scene to render, from [rememberFilamentScene]. Supplies the shared engine.
 * @param modifier Modifier for the view's layout node.
 * @param cameraState Hoisted camera state. The default constructs a new state per view.
 * @param viewState Hoisted handle exposing the live `View`/`Renderer` and `pick()`.
 * @param postProcessing Per-view post-processing and render-quality configuration.
 * @param frustumCullingEnabled Skip rendering of objects outside the camera frustum.
 * @param shadows Shadow technique for the whole view ([Shadows.Pcf]/[Shadows.Pcfd]/[Shadows.Vsm]/[Shadows.Dpcf]/
 *   [Shadows.Pcss]), or `null` to disable shadowing entirely. Per-light shadow-map quality is set via
 *   each light's `shadow` ([io.github.erkko68.filament.compose.scene.ShadowConfig]).
 * @param screenSpaceRefractionEnabled Enable screen-space refraction for refractive materials.
 * @param stencilBufferEnabled Allocate a stencil buffer (required for stencil-based effects).
 * @param transparent Enable alpha transparency blending for the view surface.
 */
@Composable
fun FilamentView(
    scene: FilamentScene,
    modifier: Modifier = Modifier,
    cameraState: CameraState = rememberCameraState(),
    viewState: FilamentViewState = rememberFilamentViewState(),
    postProcessing: PostProcessing = PostProcessing(),
    frustumCullingEnabled: Boolean = true,
    shadows: Shadows? = Shadows.Pcf,
    screenSpaceRefractionEnabled: Boolean = false,
    stencilBufferEnabled: Boolean = false,
    transparent: Boolean = false,
) {
    val engine        = scene.engine
    val filamentScene = scene.scene

    val renderer = remember(engine) { engine.createRenderer() }
    val view     = remember(engine) { engine.createView() }
    val camera   = remember(engine) { engine.createCamera() }

    // Wire the scene/camera onto the view and apply the render flags. A keyed effect with a no-op
    // onDispose, not a `remember` block: mutating Filament objects is a side effect, and it belongs
    // after composition commits (same idiom as Group's transform and rememberMaterialInstance).
    DisposableEffect(view, filamentScene, camera, frustumCullingEnabled,
                     shadows, screenSpaceRefractionEnabled, stencilBufferEnabled, transparent) {
        view.scene = filamentScene
        view.camera = camera
        view.isFrustumCullingEnabled = frustumCullingEnabled
        shadows.applyTo(view)
        view.isScreenSpaceRefractionEnabled = screenSpaceRefractionEnabled
        view.isStencilBufferEnabled = stencilBufferEnabled
        view.blendMode = if (transparent) BlendMode.TRANSLUCENT else BlendMode.OPAQUE
        onDispose {}
    }

    // Transparent needs an explicit alpha-0 clear: the default (clear=false, discard=true) leaves
    // the swapchain's untouched pixels undefined, which shows up as opaque garbage/white.
    DisposableEffect(renderer, transparent) {
        renderer.clearOptions = ClearOptions(
            clearColor = doubleArrayOf(0.0, 0.0, 0.0, 0.0),
            clear = transparent,
        )
        onDispose {}
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
    // attach() throws if the state is already bound to another view — one CameraState per view.
    DisposableEffect(cameraState, camera) {
        cameraState.attach(camera)
        onDispose { cameraState.detach(camera) }
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
        transparent = transparent,
        onResize = onResize,
    )
}
