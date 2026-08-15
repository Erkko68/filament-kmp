package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import io.github.erkko68.filament.Entity
import io.github.erkko68.filament.compose.EntityScope
import io.github.erkko68.filament.compose.EntityScopeImpl
import io.github.erkko68.filament.compose.FilamentSceneScope
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.compose.noFilamentEngine
import io.github.erkko68.filament.compose.internal.transformMatrix

/**
 * Provides the current parent entity for nested scene composables. Null at the top level
 * (no parent — leaf entities are placed in world space). Leaf composables (primitives,
 * GltfInstance) read this and reparent their own entity via `TransformManager.setParent`
 * when it's non-null, so their `position`/`rotation`/`scale` become local to the parent.
 */
// compositionLocalOf, not static (unlike LocalFilamentEngine): these two change as groups nest
// and as `visible` toggles at runtime, so recomposing only the readers is the cheaper trade.
internal val LocalParentEntity = compositionLocalOf<Entity?> { null }

/**
 * Whether the surrounding Group subtree is visible. Leaf composables (primitives, GltfInstance)
 * AND their own `visible` with this, so hiding a Group hides everything declared inside it. True
 * at the top level (no enclosing Group). Nested groups multiply through the provided value.
 */
internal val LocalGroupVisible = compositionLocalOf { true }

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
 * Note: a light inside a Group is parented like any other child, so the group's translation
 * moves it, and (by default) the typed lights re-aim their `direction` by the group's rotation
 * each frame (`followGroupRotation`). Pass `followGroupRotation = false` to keep a light's aim
 * fixed in world space while it still translates with the group.
 *
 * @param position  World-space position of the group's [pivot] (or local-space if this Group
 *   is itself nested inside another Group).
 * @param rotation  Rotation applied to the whole group.
 * @param scale     Scale applied to the whole group.
 * @param pivot     Mesh-space pivot point that rotation/scale revolve around.
 * @param visible   Whether the whole subtree is in the scene. False removes every child
 *   renderable (cheaply, keeping entities and state alive) — a show/hide toggle for the group.
 * @param onCreate  Runs once when the group's transform entity is created, with the entity and
 *   engine in scope ([EntityScope]).
 * @param content   Scene composables placed under this group's transform.
 */
@Composable
fun FilamentSceneScope.Group(
    position: Position = Position(0f),
    rotation: Rotation = Rotation.Identity,
    scale: Scale = Scale(1f),
    pivot: Position = Position(0f),
    visible: Boolean = true,
    onCreate: EntityScope.() -> Unit = {},
    content: @Composable FilamentSceneScope.() -> Unit,
) {
    val engine = LocalFilamentEngine.current ?: noFilamentEngine()
    val outerParent = LocalParentEntity.current

    val groupEntity = remember(engine) { engine.getEntityManager().create() }

    DisposableEffect(groupEntity) {
        val tm = engine.getTransformManager()
        tm.create(groupEntity)
        EntityScopeImpl(groupEntity, engine).onCreate()
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

    CompositionLocalProvider(
        LocalParentEntity provides groupEntity,
        LocalGroupVisible provides (LocalGroupVisible.current && visible),
    ) {
        content()
    }
}
