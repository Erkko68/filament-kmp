package io.github.erkko68.filament

/**
 * Factory and manager for renderables — entities that can be drawn.
 *
 * Renderables are bundles of primitives, each with its own geometry and material.
 * All primitives in a renderable share rendering attributes (shadows, skinning, morphing, etc).
 *
 * To modify a renderable, obtain a temporary handle (instance) via [getInstance],
 * then use it to query/update state. Store entities, not instances — instances are ephemeral.
 *
 * **Usage:** Create with [Builder], add to Scene, then update per-frame with instance methods.
 */
expect class RenderableManager {
    /** Primitive topology types. */
    enum class PrimitiveType { POINTS, LINES, LINE_STRIP, TRIANGLES, TRIANGLE_STRIP }

    /** Geometry immutability constraints. */
    enum class GeometryType {
        /** No restrictions; vertex/index buffers and bounds can change. */
        DYNAMIC,
        /** Bounds and world transform are immutable (optimizable). */
        STATIC_BOUNDS,
        /** Like STATIC_BOUNDS + no skinning/morphing allowed; buffers immutable. */
        STATIC
    }

    /**
     * Builder for renderable components.
     *
     * Specify primitives (geometry + material), then call [build] to attach to entity.
     * Store entities, not builders; discard builders after build().
     *
     * @param count Number of primitives in this renderable
     */
    class Builder(count: Int) {
        /** Set indexed geometry (most common). */
        fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer): Builder
        /** Set indexed geometry with offset and count. */
        fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer, offset: Int, count: Int): Builder
        /** Set indexed geometry with explicit min/max indices for optimization. */
        fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer, offset: Int, minIndex: Int, maxIndex: Int, count: Int): Builder
        /** Set non-indexed geometry (vertices only). */
        fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, offset: Int, count: Int): Builder
        /** Set non-indexed geometry with all vertices. */
        fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer): Builder

        /** Specify geometry mutability constraints. Default: DYNAMIC. */
        fun geometryType(type: GeometryType): Builder
        /** Bind a material instance to a primitive. Falls back to default material if not set. */
        fun material(index: Int, materialInstance: MaterialInstance): Builder
        /** Set blend order for translucent primitives (lowest 15 bits used). */
        fun blendOrder(index: Int, blendOrder: Int): Builder
        /** Enable global blend order (shared across renderables) vs. local (per-renderable). */
        fun globalBlendOrderEnabled(index: Int, enabled: Boolean): Builder
        /** Set axis-aligned bounding box for frustum culling (required unless culling disabled). */
        fun boundingBox(box: Box): Builder
        /** Set visibility layer mask. Default: 0x1 (layer 0 visible). Works with View.setVisibleLayers. */
        fun layerMask(select: Int, value: Int): Builder
        /** Set coarse draw order [0-7]; 7 = last. Opaque always before translucent. Default: 4. */
        fun priority(priority: Int): Builder
        /** Set rendering channel [0-7]; renderables in same channel render together. Default: 2. */
        fun channel(channel: Int): Builder
        /** Enable/disable frustum culling. Default: true. */
        fun culling(enabled: Boolean): Builder
        /** Enable shadow casting. Default: false. */
        fun castShadows(enabled: Boolean): Builder
        /** Enable shadow receiving. Default: true. */
        fun receiveShadows(enabled: Boolean): Builder
        /** Enable screen-space contact shadows (expensive). Default: false. */
        fun screenSpaceContactShadows(enabled: Boolean): Builder
        /** Enable GPU vertex skinning (up to 255 bones). VertexBuffer must have BONE_INDICES/BONE_WEIGHTS. */
        fun skinning(boneCount: Int): Builder
        /** Enable GPU vertex skinning with initial bone transforms. */
        fun skinning(boneCount: Int, bones: FloatArray): Builder
        /** Enable GPU skinning using a shared SkinningBuffer. */
        fun skinning(skinningBuffer: SkinningBuffer, boneCount: Int, offset: Int): Builder
        /** Enable SkinningBuffer mode (required to use setSkinningBuffer at runtime). */
        fun enableSkinningBuffers(enabled: Boolean): Builder
        /** Enable legacy vertex morphing (up to 4 targets). Must be enabled in material. */
        fun morphing(targetCount: Int): Builder
        /** Enable standard vertex morphing using a MorphTargetBuffer. */
        fun morphing(morphTargetBuffer: MorphTargetBuffer): Builder
        /** Enable large-scale fog on this renderable. Default: true. */
        fun fog(enabled: Boolean): Builder
        /** Enable/disable a light channel [0-7]. Channel 0 enabled by default. */
        fun lightChannel(channel: Int, enable: Boolean): Builder
        /** Set instance count (default 1, max 32767). Material must have instanced=true. */
        fun instances(instanceCount: Int): Builder
        /** Build and attach renderable component to entity. */
        fun build(engine: Engine, entity: Entity)
    }

    /** Check if entity has a renderable component. */
    fun hasComponent(entity: Entity): Boolean
    /** Get instance handle for accessing renderable state. */
    fun getInstance(entity: Entity): EntityInstance
    /** Destroy the renderable component on an entity. */
    fun destroy(entity: Entity)

    /** Update the AABB used for frustum culling. Disallowed if STATIC geometry. */
    fun setAxisAlignedBoundingBox(instance: EntityInstance, box: Box)
    /** Get the AABB used for frustum culling. */
    fun getAxisAlignedBoundingBox(instance: EntityInstance, outBox: Box?): Box

    /** Update visibility layer mask. @param select Layers to affect; @param value New values. */
    fun setLayerMask(instance: EntityInstance, select: Int, value: Int)
    /** Update coarse draw priority [0-7]; 7 = last. */
    fun setPriority(instance: EntityInstance, priority: Int)
    /** Get coarse draw priority. */
    fun getPriority(instance: EntityInstance): Int
    /** Update rendering channel [0-7]. */
    fun setChannel(instance: EntityInstance, channel: Int)
    /** Get rendering channel. */
    fun getChannel(instance: EntityInstance): Int
    /** Enable/disable frustum culling. */
    fun setCulling(instance: EntityInstance, enabled: Boolean)
    /** Check if frustum culling is enabled. */
    fun isCullingEnabled(instance: EntityInstance): Boolean
    /** Enable/disable large-scale fog. */
    fun setFogEnabled(instance: EntityInstance, enabled: Boolean)
    /** Check if large-scale fog is enabled. */
    fun getFogEnabled(instance: EntityInstance): Boolean
    /** Enable/disable shadow casting from this renderable. */
    fun setCastShadows(instance: EntityInstance, enabled: Boolean)
    /** Enable/disable shadow receiving on this renderable. */
    fun setReceiveShadows(instance: EntityInstance, enabled: Boolean)
    /** Enable/disable screen-space contact shadows. */
    fun setScreenSpaceContactShadows(instance: EntityInstance, enabled: Boolean)
    /** Check if this renderable casts shadows. */
    fun isShadowCaster(instance: EntityInstance): Boolean
    /** Check if this renderable receives shadows. */
    fun isShadowReceiver(instance: EntityInstance): Boolean
    /** Check if screen-space contact shadows are enabled. */
    fun isScreenSpaceContactShadowsEnabled(instance: EntityInstance): Boolean

    /** Get number of primitives in this renderable. */
    fun getPrimitiveCount(instance: EntityInstance): Int
    /** Get number of instances (from Builder.instances). */
    fun getInstanceCount(instance: EntityInstance): Int

    /** Set material instance for a primitive. */
    fun setMaterialInstanceAt(instance: EntityInstance, primitiveIndex: Int, materialInstance: MaterialInstance)
    /** Get material instance for a primitive, or null for default. */
    fun getMaterialInstanceAt(instance: EntityInstance, primitiveIndex: Int): MaterialInstance?

    /** Update indexed geometry for a primitive. */
    fun setGeometryAt(instance: EntityInstance, primitiveIndex: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer, offset: Int, count: Int)
    /** Update non-indexed geometry for a primitive. */
    fun setGeometryAt(instance: EntityInstance, primitiveIndex: Int, type: PrimitiveType, vb: VertexBuffer, offset: Int, count: Int)

    /** Set blend order for a primitive. */
    fun setBlendOrderAt(instance: EntityInstance, primitiveIndex: Int, blendOrder: Int)
    /** Get blend order for a primitive. */
    fun getBlendOrderAt(instance: EntityInstance, primitiveIndex: Int): Int
    /** Enable/disable global blend order (vs. local per-renderable) for a primitive. */
    fun setGlobalBlendOrderEnabledAt(instance: EntityInstance, primitiveIndex: Int, enabled: Boolean)
    /** Check if global blend order is enabled for a primitive. */
    fun isGlobalBlendOrderEnabledAt(instance: EntityInstance, primitiveIndex: Int): Boolean

    /** Enable/disable a light channel [0-7] for this renderable. */
    fun setLightChannel(instance: EntityInstance, channel: Int, enable: Boolean)
    /** Check if a light channel is enabled for this renderable. */
    fun getLightChannel(instance: EntityInstance, channel: Int): Boolean

    /** Get number of morph targets in this renderable. */
    fun getMorphTargetCount(instance: EntityInstance): Int
    /** Associate a region of a SkinningBuffer to this renderable (offset + count <= 256). */
    fun setSkinningBuffer(instance: EntityInstance, skinningBuffer: SkinningBuffer, count: Int, offset: Int)
    /** Update vertex morphing weights (all zeros by default). */
    fun setMorphWeights(instance: EntityInstance, weights: FloatArray, offset: Int = 0)
    /** Associate a MorphTargetBuffer region to a primitive. */
    fun setMorphTargetBufferOffsetAt(instance: EntityInstance, level: Int, primitiveIndex: Int, offset: Int)

    /** Update bone transforms as 4×4 matrices. Pre-allocated via Builder.skinning(). */
    fun setBonesAsMatrices(instance: EntityInstance, matrices: FloatArray, boneCount: Int, offset: Int)
    /** Update bone transforms as quaternion+translation pairs. */
    fun setBonesAsQuaternions(instance: EntityInstance, quaternions: FloatArray, boneCount: Int, offset: Int)
    /** Clear material instance for a primitive (revert to default). */
    fun clearMaterialInstanceAt(instance: EntityInstance, primitiveIndex: Int)
}
