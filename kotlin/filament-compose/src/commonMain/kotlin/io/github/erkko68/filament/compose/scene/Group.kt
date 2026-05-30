package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import io.github.erkko68.filament.compose.FilamentSceneScope
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.compose.internal.transformMatrix
import io.github.erkko68.filament.utils.Quaternion

/**
 * Provides the current parent entity for nested scene composables. Null at the top level
 * (no parent — leaf entities are placed in world space). Leaf composables (primitives,
 * GltfInstance) read this and reparent their own entity via `TransformManager.setParent`
 * when it's non-null, so their `position`/`rotation`/`scale` become local to the parent.
 */
internal val LocalParentEntity = compositionLocalOf<Int?> { null }

/**
 * Groups child scene composables under a single transform. Everything declared inside
 * [content] is parented to a hidden transform entity, so moving/rotating/scaling the Group
 * moves the whole assembly as a unit. Children's own `position`/`rotation`/`scale` become
 * local to the group, and groups themselves nest cleanly inside other groups.
 *
 * ```kotlin
 * Group(position = Position(2f, 0f, 0f), rotation = bodyRotation) {
 *     Cube(material = body, size = 1.5f)
 *     Cylinder(material = wheel, position = Position( 0.6f, -0.4f, 0f), radius = 0.2f, height = 0.1f)
 *     Cylinder(material = wheel, position = Position(-0.6f, -0.4f, 0f), radius = 0.2f, height = 0.1f)
 * }
 * ```
 *
 * Note: `Light` is not yet parentable — Filament's lights use a position baked into the
 * LightManager component rather than a transform, so they ignore the group's transform.
 *
 * @param position  World-space position of the group's [pivot] (or local-space if this Group
 *   is itself nested inside another Group).
 * @param rotation  Rotation applied to the whole group.
 * @param scale     Scale applied to the whole group.
 * @param pivot     Mesh-space pivot point that rotation/scale revolve around.
 * @param onCreate  Receives the group's transform entity ID once it's created.
 */
@Composable
fun FilamentSceneScope.Group(
    position: Position = Position(0f),
    rotation: Quaternion = Quaternion(),
    scale: Scale = Scale(1f),
    pivot: Position = Position(0f),
    onCreate: (entity: Int) -> Unit = {},
    content: @Composable FilamentSceneScope.() -> Unit,
) {
    val engine = LocalFilamentEngine.current
    val outerParent = LocalParentEntity.current

    val groupEntity = remember { engine.getEntityManager().create() }

    DisposableEffect(groupEntity) {
        val tm = engine.getTransformManager()
        tm.create(groupEntity)
        onCreate(groupEntity)
        onDispose {
            tm.destroy(groupEntity)
            engine.getEntityManager().destroy(groupEntity)
        }
    }

    DisposableEffect(groupEntity, position, rotation, scale, pivot) {
        val tm = engine.getTransformManager()
        tm.setTransform(tm.getInstance(groupEntity), transformMatrix(position, rotation, scale, pivot))
        onDispose {}
    }

    // Nested groups: this group is itself a child of the outer one.
    DisposableEffect(groupEntity, outerParent) {
        if (outerParent != null) {
            val tm = engine.getTransformManager()
            tm.setParent(tm.getInstance(groupEntity), tm.getInstance(outerParent))
        }
        onDispose {}
    }

    CompositionLocalProvider(LocalParentEntity provides groupEntity) {
        content()
    }
}
