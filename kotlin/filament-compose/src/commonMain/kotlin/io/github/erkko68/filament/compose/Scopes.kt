package io.github.erkko68.filament.compose

/**
 * Restricts the implicit-receiver search inside nested scene declarations. See
 * [FilamentSceneScope].
 */
@DslMarker
annotation class FilamentDsl

/**
 * Receiver for the content lambda of [rememberFilamentScene]. Scene-content composables
 * ([io.github.erkko68.filament.compose.scene.Light], `GltfInstance`, `Group`, primitives,
 * [FilamentEffect], …) are extensions on this scope, so they only compile inside a scene
 * declaration.
 */
@FilamentDsl
interface FilamentSceneScope

internal object FilamentSceneScopeInstance : FilamentSceneScope
