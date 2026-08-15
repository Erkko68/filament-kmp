package io.github.erkko68.filament.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import io.github.erkko68.filament.RenderTarget
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.Viewport
import io.github.erkko68.filament.compose.internal.FilamentRenderLoop
import io.github.erkko68.filament.compose.scene.CameraState
import io.github.erkko68.filament.compose.scene.PostProcessing
import io.github.erkko68.filament.compose.scene.applyTo
import io.github.erkko68.filament.compose.scene.rememberCameraState

/**
 * Renders a [FilamentScene] off-screen through its own camera into a [Texture] that you can feed
 * back into a material — the building block for mini-maps, security-camera monitors, portals and
 * thumbnails. Returns the color attachment as a sampleable [Texture] (null until valid).
 *
 * The off-screen view owns its own `View`, `Camera` and `Renderer` and redraws every frame via
 * `Renderer.renderStandaloneView`; it is independent of any on-screen [FilamentView]. Bind the
 * returned texture like any other:
 *
 * ```kotlin
 * val scene = rememberFilamentScene { /* world */ }
 * val mapCam = rememberCameraState(initialEye = Position(0f, 40f, 0f), initialTarget = Position(0f))
 * val mapTex = rememberRenderTargetTexture(scene, mapCam, width = 256, height = 256)
 *
 * val screen = rememberMaterialInstance(screenMaterial, mapTex) {
 *     mapTex?.let { setParameter("screen", it, TextureSampler()) }
 * }
 * Plane(material = screen)          // a screen showing the mini-map
 * ```
 *
 * Post-processing is off by default (`postProcessing = null`): a DEPTH attachment is used, and
 * Filament ignores depth attachments when post-processing is on. Pass a
 * [PostProcessing][io.github.erkko68.filament.compose.scene.PostProcessing] value — the same
 * type [FilamentView] takes — only if you don't depend on the depth buffer.
 *
 * @param scene The scene to render off-screen. Supplies the engine.
 * @param cameraState Hoisted camera for the off-screen view. Defaults to a fresh state.
 * @param width Texture width in pixels.
 * @param height Texture height in pixels.
 * @param postProcessing Post-processing configuration for the off-screen view, or null to skip
 *   the post-processing pass entirely (the default — see note above).
 * @return The color texture being rendered into, or null for a non-positive size.
 */
@Composable
fun rememberRenderTargetTexture(
    scene: FilamentScene,
    cameraState: CameraState = rememberCameraState(),
    width: Int = 512,
    height: Int = 512,
    postProcessing: PostProcessing? = null,
): Texture? {
    val engine = scene.engine
    if (width <= 0 || height <= 0) return null

    val color = remember(engine, width, height) {
        runCatching {
            Texture.Builder()
                .width(width).height(height).levels(1)
                .sampler(Texture.Sampler.SAMPLER_2D)
                .format(Texture.InternalFormat.RGBA8)
                .usage(Texture.Usage.COLOR_ATTACHMENT or Texture.Usage.SAMPLEABLE)
                .build(engine)
        }.getOrNull()
    } ?: return null

    val depth = remember(engine, width, height) {
        runCatching {
            Texture.Builder()
                .width(width).height(height).levels(1)
                .sampler(Texture.Sampler.SAMPLER_2D)
                .format(Texture.InternalFormat.DEPTH24)
                .usage(Texture.Usage.DEPTH_ATTACHMENT)
                .build(engine)
        }.getOrNull()
    }

    val target = remember(engine, color, depth) {
        runCatching {
            RenderTarget.Builder()
                .texture(RenderTarget.AttachmentPoint.COLOR, color)
                .apply {
                    if (depth != null) {
                        texture(RenderTarget.AttachmentPoint.DEPTH, depth)
                    }
                }
                .build(engine)
        }.getOrNull()
    } ?: return null

    val view     = remember(engine) { engine.createView() }
    val camera   = remember(engine) { engine.createCamera() }
    val renderer = remember(engine) { engine.createRenderer() }

    // Wire the off-screen view. Re-runs only when an input actually changes.
    remember(view, scene.scene, camera, target, width, height) {
        view.scene = scene.scene
        view.camera = camera
        view.viewport = Viewport(0, 0, width, height)
        view.renderTarget = target
    }

    // Same value semantics as FilamentView: the allocated ColorGrading (if any) is destroyed on
    // dispose / before re-apply. null skips the post-processing pass entirely.
    DisposableEffect(view, postProcessing, engine) {
        val colorGrading = if (postProcessing != null) {
            postProcessing.applyTo(view, engine)
        } else {
            view.isPostProcessingEnabled = false
            null
        }
        onDispose { colorGrading?.let { engine.destroyColorGrading(it) } }
    }

    // Push the camera state every time it changes; reads register recomposition subscriptions.
    val aspect = width.toDouble() / height.toDouble()
    val snapshot = cameraState.snapshot()
    SideEffect {
        cameraState.aspect = aspect
        snapshot.applyTo(camera, aspect)
    }

    DisposableEffect(cameraState, camera) {
        cameraState.attach(camera)
        onDispose { cameraState.detach(camera) }
    }

    DisposableEffect(engine, color, depth, target, view, camera, renderer) {
        onDispose {
            engine.destroyRenderer(renderer)
            engine.destroyView(view)
            engine.destroyCamera(camera)
            engine.destroyRenderTarget(target)
            engine.destroyTexture(color)
            depth?.let { engine.destroyTexture(it) }
        }
    }

    FilamentRenderLoop { renderer.renderStandaloneView(view) }

    return color
}
