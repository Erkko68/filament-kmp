package io.github.erkko68.filament.compose

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Entity

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

/**
 * Receiver for the `onCreate` callback of entity-producing scene composables —
 * [Group][io.github.erkko68.filament.compose.scene.Group] and the primitives (`Cube`, `Sphere`,
 * `Plane`, `Cylinder`, `Mesh`). Exposes the created [entity] together with the [engine], so
 * one-time setup (manager operations, pick registries) needs no extra plumbing — the same
 * scope-receiver shape as `GltfInstance`'s richer
 * [GltfInstanceScope][io.github.erkko68.filament.compose.scene.GltfInstanceScope].
 */
interface EntityScope {
    /** The entity this composable created. */
    val entity: Entity

    /** The Filament engine, for manager operations on [entity]. */
    val engine: Engine
}

internal class EntityScopeImpl(
    override val entity: Entity,
    override val engine: Engine,
) : EntityScope
