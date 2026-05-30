package io.github.erkko68.filament.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.compose.scene.CameraState
import io.github.erkko68.filament.compose.scene.IndirectLightState
import io.github.erkko68.filament.compose.scene.PostProcessing
import io.github.erkko68.filament.compose.scene.SkyboxState
import io.github.erkko68.filament.compose.scene.rememberCameraState

/**
 * All-in-one entry point for the common single-view case: declares a scene and renders it
 * through one viewport in a single call. Equivalent to a [rememberFilamentScene] feeding one
 * [FilamentView] — reach for those directly when you need multiple views over one scene, or to
 * hoist the scene as a value.
 *
 * [content] is the scene declaration (lights, models, primitives); the viewport's look is
 * configured by value via [cameraState] and [postProcessing].
 *
 * ```kotlin
 * FilamentSceneView(
 *     modifier = Modifier.fillMaxSize(),
 *     cameraState = cam,
 *     skyboxState = sky,
 *     postProcessing = PostProcessing(bloom = Bloom(strength = 0.2f)),
 * ) {
 *     Light(type = LightManager.Type.SUN, ...)
 *     GltfInstance(asset = duck)
 * }
 * ```
 *
 * @param engine Engine backing the scene. Defaults to a dedicated engine created and destroyed
 *   with this composable. Pass a [rememberFilamentEngine] value to share an engine.
 * @param cameraState Hoisted camera state. The default constructs a new state.
 * @param viewState Hoisted handle exposing the live `View`/`Renderer` and `pick()`.
 * @param skyboxState Optional hoisted skybox state. Null = no skybox (the default).
 * @param indirectLightState Optional hoisted IBL state. Null = no IBL (the default).
 * @param postProcessing Per-view post-processing and render-quality configuration.
 * @param frustumCullingEnabled Skip rendering of objects outside the camera frustum.
 * @param shadowingEnabled Allow lights to cast and receive shadows.
 * @param screenSpaceRefractionEnabled Enable screen-space refraction for refractive materials.
 * @param stencilBufferEnabled Allocate a stencil buffer (required for stencil-based effects).
 * @param content Scene composables ([io.github.erkko68.filament.compose.scene.Light],
 *   `GltfInstance`, `Group`, primitives, …).
 */
@Composable
fun FilamentSceneView(
    modifier: Modifier = Modifier,
    engine: Engine = rememberFilamentEngine(),
    cameraState: CameraState = rememberCameraState(),
    viewState: FilamentViewState = rememberFilamentViewState(),
    skyboxState: SkyboxState? = null,
    indirectLightState: IndirectLightState? = null,
    postProcessing: PostProcessing = PostProcessing(),
    frustumCullingEnabled: Boolean = true,
    shadowingEnabled: Boolean = true,
    screenSpaceRefractionEnabled: Boolean = false,
    stencilBufferEnabled: Boolean = false,
    content: @Composable FilamentSceneScope.() -> Unit,
) {
    val scene = rememberFilamentScene(
        engine = engine,
        skyboxState = skyboxState,
        indirectLightState = indirectLightState,
        content = content,
    )
    FilamentView(
        scene = scene,
        modifier = modifier,
        cameraState = cameraState,
        viewState = viewState,
        postProcessing = postProcessing,
        frustumCullingEnabled = frustumCullingEnabled,
        shadowingEnabled = shadowingEnabled,
        screenSpaceRefractionEnabled = screenSpaceRefractionEnabled,
        stencilBufferEnabled = stencilBufferEnabled,
    )
}
