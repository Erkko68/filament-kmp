package io.github.erkko68.filament.compose

import androidx.compose.runtime.compositionLocalOf
import io.github.erkko68.filament.Camera
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.Scene
import io.github.erkko68.filament.View
import io.github.erkko68.filament.compose.scene.CameraConfig
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.Exposure
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.gltfio.AssetLoader
import io.github.erkko68.filament.utils.Float2

val LocalFilamentEngine = compositionLocalOf<Engine> {
    error("No FilamentEngine in scope — wrap with FilamentView { }")
}

val LocalFilamentScene = compositionLocalOf<Scene> {
    error("No FilamentScene in scope — wrap with FilamentView { }")
}

val LocalFilamentCamera = compositionLocalOf<Camera> {
    error("No FilamentCamera in scope — wrap with FilamentView { }")
}

internal val LocalCameraConfig = compositionLocalOf {
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

internal val LocalCameraConfigController = compositionLocalOf<(CameraConfig) -> Unit> {
    { _ -> }
}

/** Exposed for advanced use via [FilamentEffect]. Not needed for scene DSL. */
val LocalFilamentView = compositionLocalOf<View> {
    error("No FilamentView in scope — wrap with FilamentView { }")
}

/** Exposed for advanced use via [FilamentEffect]. Not needed for scene DSL. */
val LocalFilamentRenderer = compositionLocalOf<Renderer> {
    error("No FilamentRenderer in scope — wrap with FilamentView { }")
}

/** Internal: shared AssetLoader provided by FilamentView to all GltfModel composables. */
internal val LocalAssetLoader = compositionLocalOf<AssetLoader> {
    error("No AssetLoader in scope — wrap with FilamentView { }")
}

