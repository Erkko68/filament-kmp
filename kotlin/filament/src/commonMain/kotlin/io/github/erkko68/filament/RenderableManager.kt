package io.github.erkko68.filament

import io.github.erkko68.filament.capi.*
import io.github.erkko68.filament.interop.*

/**
 * Factory and manager for Renderables, which are entities that can be drawn.
 *
 * Renderables are bundles of primitives, each with its own geometry and material. All
 * primitives in a renderable share rendering attributes such as shadow casting, skinning,
 * morphing, layer masks, and priority.
 *
 * **Creating renderables:**
 *
 * ```
 * val entity = entityManager.create()
 * RenderableManager.Builder(1)          // 1 primitive
 *     .boundingBox(Box(floatArrayOf(0f, 0f, 0f), floatArrayOf(1f, 1f, 1f)))
 *     .material(0, materialInstance)
 *     .geometry(0, PrimitiveType.TRIANGLES, vertexBuffer, indexBuffer, 0, 6)
 *     .receiveShadows(true)
 *     .castShadows(true)
 *     .build(engine, entity)
 *
 * scene.addEntity(entity)
 * ```
 *
 * **Modifying renderables:**
 * To modify an existing renderable, use RenderableManager to get a temporary handle called an
 * Instance. The instance is used to get/set rendering state. Instances are ephemeral; store
 * Entity IDs, not instances.
 *
 * **Related components:**
 * - Use TransformManager to associate a 4x4 transform with an entity
 * - Use LightManager to add lights
 * - Use RenderableManager to add geometry and materials
 *
 * @see RenderableManager.Builder
 * @see TransformManager
 * @see LightManager
 */
class RenderableManager @InternalFilamentApi constructor(internal val nativeHandle: NativePointer) {
    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /** Primitive topology types. */
    enum class PrimitiveType { POINTS, LINES, LINE_STRIP, TRIANGLES, TRIANGLE_STRIP }

    /**
     * A bone transform as a unit quaternion and a translation, the compact alternative to a 4x4 matrix.
     */
    class Bone {
        /** Rotation as a unit quaternion in (x, y, z, w) order. Default: identity. */
        var unitQuaternion: FloatArray = floatArrayOf(0f, 0f, 0f, 1f)
        /** Translation (x, y, z). Default: zero. */
        var translation: FloatArray = FloatArray(3)
    }

    /**
     * Adds renderable components to entities using a builder pattern.
     *
     * @param count the number of primitives that will be supplied to the builder
     */
    class Builder(count: Int) {
        private val nativeBuilder = FilaRenderableManagerBuilder_create(count)
        // The C++ builder keeps bone/weight pointers until build(), so the copies live until then.
        private val scope = InteropScope()

        /** Outcome of [build]. */
        enum class Result { Error, Success }

        /** Type of geometry for a Renderable. */
        enum class GeometryType {
            /** Dynamic geometry has no restriction */
            DYNAMIC,
            /** Bounds and world space transform are immutable */
            STATIC_BOUNDS,
            /** Skinning/morphing not allowed and Vertex/IndexBuffer immutables */
            STATIC
        }

        companion object {
            /** The channel renderables use by default. */
            const val DEFAULT_CHANNEL: Int = 2
        }

        /**
         * Specifies the geometry data for a primitive with explicit min/max indices.
         *
         * @param index zero-based index of the primitive, must be less than the count passed to Builder constructor
         * @param type specifies the topology of the primitive (e.g., PrimitiveType.TRIANGLES)
         * @param vertices specifies the vertex buffer, which in turn specifies a set of attributes
         * @param indices specifies the index buffer (either u16 or u32)
         * @param offset specifies where in the index buffer to start reading (expressed as a number of indices)
         * @param minIndex specifies the minimum index contained in the index buffer
         * @param maxIndex specifies the maximum index contained in the index buffer
         * @param count number of indices to read (for triangles, this should be a multiple of 3)
         * @return Builder reference for chaining calls.
         */
        fun geometry(index: Int, type: PrimitiveType, vertices: VertexBuffer, indices: IndexBuffer, offset: Int, minIndex: Int, maxIndex: Int, count: Int): Builder = apply {
            FilaRenderableManagerBuilder_geometry_IndexBuffer_size_t_size_t_size_t_size_t(nativeBuilder, index, type.toNative(), vertices.nativeHandle, indices.nativeHandle, offset, minIndex, maxIndex, count)
        }
        /**
         * Specifies the geometry data for a primitive with offset and count.
         *
         * @param offset specifies where in the index buffer to start reading (expressed as a number of indices)
         * @param count number of indices to read (for triangles, this should be a multiple of 3)
         * @return Builder reference for chaining calls.
         */
        fun geometry(index: Int, type: PrimitiveType, vertices: VertexBuffer, indices: IndexBuffer, offset: Int, count: Int): Builder = apply {
            FilaRenderableManagerBuilder_geometry_IndexBuffer_size_t_size_t(nativeBuilder, index, type.toNative(), vertices.nativeHandle, indices.nativeHandle, offset, count)
        }
        /**
         * Specifies the geometry data for a primitive, using the whole index buffer.
         *
         * Typically, each primitive is specified with a pair of daisy-chained calls: geometry(...) and
         * material(...).
         *
         * @return Builder reference for chaining calls.
         */
        fun geometry(index: Int, type: PrimitiveType, vertices: VertexBuffer, indices: IndexBuffer): Builder = apply {
            FilaRenderableManagerBuilder_geometry_IndexBuffer(nativeBuilder, index, type.toNative(), vertices.nativeHandle, indices.nativeHandle)
        }
        /**
         * Specifies the geometry data for a non-indexed primitive: offset / count refer to vertices.
         *
         * Attribute-less rendering: the vertex shader can generate positions procedurally from
         * gl_VertexIndex / gl_VertexID, with a VertexBuffer that has bufferCount == 0. This requires
         * FEATURE_LEVEL_1 or higher and is incompatible with skinning and morphing.
         *
         * @param offset specifies where in the vertex buffer to start reading (expressed as a number of vertices)
         * @param count number of vertices to read (for triangles, this should be a multiple of 3)
         * @return Builder reference for chaining calls.
         */
        fun geometry(index: Int, type: PrimitiveType, vertices: VertexBuffer, offset: Int, count: Int): Builder = apply {
            FilaRenderableManagerBuilder_geometry_size_t_size_t(nativeBuilder, index, type.toNative(), vertices.nativeHandle, offset, count)
        }
        /**
         * Specifies the geometry data for a non-indexed primitive using all vertices.
         *
         * @return Builder reference for chaining calls.
         */
        fun geometry(index: Int, type: PrimitiveType, vertices: VertexBuffer): Builder = apply {
            FilaRenderableManagerBuilder_geometry(nativeBuilder, index, type.toNative(), vertices.nativeHandle)
        }

        /**
         * Specify the type of geometry for this renderable.
         *
         * DYNAMIC geometry has no restriction, STATIC_BOUNDS geometry means that both the bounds
         * and the world-space transform of the renderable are immutable.
         * STATIC geometry has the same restrictions as STATIC_BOUNDS, but in addition disallows
         * skinning, morphing and changing the VertexBuffer or IndexBuffer in any way.
         *
         * @param type type of geometry.
         * @return Builder reference for chaining calls.
         */
        fun geometryType(type: GeometryType): Builder = apply {
            FilaRenderableManagerBuilder_geometryType(nativeBuilder, type.ordinal)
        }
        /**
         * Binds a material instance to the specified primitive.
         *
         * If no material is specified for a given primitive, Filament will fall back to a basic
         * default material. The MaterialInstance's material must have a feature level equal or lower
         * to the engine's selected feature level.
         *
         * @param index zero-based index of the primitive, must be less than the count passed to
         * Builder constructor
         * @param materialInstance the material to bind
         * @return Builder reference for chaining calls.
         */
        fun material(index: Int, materialInstance: MaterialInstance): Builder = apply {
            FilaRenderableManagerBuilder_material(nativeBuilder, index, materialInstance.nativeHandle)
        }
        /**
         * The axis-aligned bounding box of the renderable.
         *
         * This is an object-space AABB used for frustum culling. For skinning and morphing, this
         * should encompass all possible vertex positions. It is mandatory unless culling is
         * disabled for the renderable.
         *
         * @param axisAlignedBoundingBox axis-aligned bounding box
         * @return Builder reference for chaining calls.
         */
        fun boundingBox(axisAlignedBoundingBox: Box): Builder = apply {
            axisAlignedBoundingBox.useNative { FilaRenderableManagerBuilder_boundingBox(nativeBuilder, it) }
        }
        /**
         * Sets bits in a visibility mask. By default, this is 0x1.
         *
         * This feature provides a simple mechanism for hiding and showing groups of renderables
         * in a Scene. See View.setVisibleLayers().
         *
         * For example, to set bit 1 and reset bits 0 and 2 while leaving all other bits unaffected,
         * do: builder.layerMask(7, 2).
         *
         * @param select the set of bits to affect
         * @param values the replacement values for the affected bits
         * @return Builder reference for chaining calls.
         */
        fun layerMask(select: Int, values: Int): Builder = apply { FilaRenderableManagerBuilder_layerMask(nativeBuilder, select, values) }
        /**
         * Provides coarse-grained control over draw order.
         *
         * The priority is applied separately for opaque and translucent objects, that is, opaque
         * objects are always drawn before translucent objects regardless of the priority. Priority is
         * orthogonal to [layerMask], which merely controls visibility.
         *
         * @param priority clamped to the range [0..7], defaults to 4; 7 is lowest priority
         *                 (rendered last).
         * @return Builder reference for chaining calls.
         */
        fun priority(priority: Int): Builder = apply { FilaRenderableManagerBuilder_priority(nativeBuilder, priority) }
        /**
         * Set the channel this renderable is associated to. There can be 8 channels.
         * All renderables in a given channel are rendered together, regardless of anything else.
         * They are sorted as usual within a channel.
         *
         * Channels 0 and 1 may not have render primitives using a material with refractionType
         * set to screenspace.
         *
         * @param channel clamped to the range [0..7], defaults to [DEFAULT_CHANNEL].
         * @return Builder reference for chaining calls.
         */
        fun channel(channel: Int): Builder = apply { FilaRenderableManagerBuilder_channel(nativeBuilder, channel) }
        /**
         * Controls frustum culling, true by default.
         *
         * Note: Do not confuse frustum culling with backface culling. The latter is controlled via
         * the material.
         *
         * @param enable whether frustum culling is enabled
         * @return Builder reference for chaining calls.
         */
        fun culling(enable: Boolean): Builder = apply { FilaRenderableManagerBuilder_culling(nativeBuilder, enable) }
        /**
         * Enables or disables a light channel. Light channel 0 is enabled by default.
         *
         * @param channel Light channel to enable or disable, between 0 and 7.
         * @param enable Whether to enable or disable the light channel.
         * @return Builder reference for chaining calls.
         */
        fun lightChannel(channel: Int, enable: Boolean = true): Builder = apply { FilaRenderableManagerBuilder_lightChannel(nativeBuilder, channel, enable) }
        /**
         * Controls if this renderable casts shadows, false by default.
         *
         * If the View's shadow type is set to ShadowType.VSM, castShadows should only be disabled
         * if either receiveShadows is also disabled, or the object is guaranteed to not cast shadows
         * on itself or other objects (for example, a ground plane).
         *
         * @param enable whether shadow casting is enabled
         * @return Builder reference for chaining calls.
         */
        fun castShadows(enable: Boolean): Builder = apply { FilaRenderableManagerBuilder_castShadows(nativeBuilder, enable) }
        /**
         * Controls if this renderable receives shadows, true by default.
         *
         * @param enable whether shadow receiving is enabled
         * @return Builder reference for chaining calls.
         */
        fun receiveShadows(enable: Boolean): Builder = apply { FilaRenderableManagerBuilder_receiveShadows(nativeBuilder, enable) }
        /**
         * Controls if this renderable uses screen-space contact shadows. This is more
         * expensive but can improve the quality of shadows, especially in large scenes.
         * (off by default).
         *
         * @param enable whether screen-space contact shadows are enabled
         * @return Builder reference for chaining calls.
         */
        fun screenSpaceContactShadows(enable: Boolean): Builder = apply { FilaRenderableManagerBuilder_screenSpaceContactShadows(nativeBuilder, enable) }
        /**
         * Allows bones to be swapped out and shared using SkinningBuffer.
         *
         * If skinning buffer mode is enabled, clients must call setSkinningBuffer() rather than
         * setBones(). This allows sharing of data between renderables.
         *
         * @param enabled If true, enables buffer object mode. False by default.
         * @return Builder reference for chaining calls.
         */
        fun enableSkinningBuffers(enabled: Boolean = true): Builder = apply {
            FilaRenderableManagerBuilder_enableSkinningBuffers(nativeBuilder, enabled)
        }
        /**
         * Controls if this renderable is affected by the large-scale fog.
         *
         * @param enabled If true, enables large-scale fog on this object. Disables it otherwise.
         *                True by default.
         * @return Builder reference for chaining calls.
         */
        fun fog(enabled: Boolean = true): Builder = apply { FilaRenderableManagerBuilder_fog(nativeBuilder, enabled) }
        /**
         * Enables GPU vertex skinning from a region of a SkinningBuffer.
         *
         * @param skinningBuffer the SkinningBuffer to use
         * @param count the number of bones to use (up to 256)
         * @param offset offset in the SkinningBuffer
         * @return Builder reference for chaining calls.
         */
        fun skinning(skinningBuffer: SkinningBuffer, count: Int, offset: Int): Builder = apply {
            FilaRenderableManagerBuilder_skinning_SkinningBuffer_size_t_size_t(nativeBuilder, skinningBuffer.nativeHandle, count, offset)
        }
        /**
         * Enables GPU vertex skinning for up to 255 bones with initial bone transforms.
         *
         * Skinning Buffer mode must be disabled. Each vertex can be affected by up to 4 bones
         * simultaneously. The attached VertexBuffer must provide data in the BONE_INDICES slot
         * (uvec4) and the BONE_WEIGHTS slot (float4).
         *
         * @param boneCount the number of bone transforms (up to 255)
         * @param transforms the initial 4x4 transforms, 16 floats per bone
         * @return Builder reference for chaining calls.
         */
        fun skinning(boneCount: Int, transforms: FloatArray): Builder = apply {
            FilaRenderableManagerBuilder_skinning_size_t_mat4f(nativeBuilder, boneCount, scope.toInterop(transforms))
        }
        /**
         * Enables GPU vertex skinning for up to 255 bones with initial bone transforms.
         *
         * @param boneCount the number of bone transforms (up to 255)
         * @param bones the initial set of transforms (one for each bone)
         * @return Builder reference for chaining calls.
         */
        fun skinning(boneCount: Int, bones: Array<Bone>): Builder = apply {
            FilaRenderableManagerBuilder_skinning_size_t_Bone(nativeBuilder, boneCount, scope.toInterop(bones.toFloats()))
        }
        /**
         * Enables GPU vertex skinning for up to 255 bones, 0 by default.
         *
         * @param boneCount 0 to disable, otherwise the number of bone transforms (up to 255)
         * @return Builder reference for chaining calls.
         */
        fun skinning(boneCount: Int): Builder = apply { FilaRenderableManagerBuilder_skinning_size_t(nativeBuilder, boneCount) }
        /**
         * Defines bone indices and weights pairs for vertex skinning, for primitives with more
         * than 4 bones per vertex.
         *
         * @param primitiveIndex zero-based index of the primitive
         * @param indicesAndWeights (bone index, weight) pairs, 2 floats per pair
         * @param count number of pairs
         * @param bonesPerVertex number of bones per vertex
         * @return Builder reference for chaining calls.
         */
        fun boneIndicesAndWeights(primitiveIndex: Int, indicesAndWeights: FloatArray, count: Int, bonesPerVertex: Int): Builder = apply {
            FilaRenderableManagerBuilder_boneIndicesAndWeights_float2_size_t_size_t(nativeBuilder, primitiveIndex, scope.toInterop(indicesAndWeights), count, bonesPerVertex)
        }
        /**
         * Controls if the renderable has legacy vertex morphing targets, zero by default.
         *
         * For legacy morphing, the attached VertexBuffer must provide data in the
         * appropriate VertexAttribute slots (MORPH_POSITION_0 etc). Legacy morphing only
         * supports up to 4 morph targets.
         *
         * @param targetCount the number of morph targets
         * @return Builder reference for chaining calls.
         */
        fun morphing(targetCount: Int): Builder = apply { FilaRenderableManagerBuilder_morphing_size_t(nativeBuilder, targetCount) }
        /**
         * Controls if the renderable has vertex morphing targets, zero by default.
         *
         * For standard morphing, a MorphTargetBuffer must be provided. See also
         * [RenderableManager.setMorphWeights], which can be called on a per-frame basis
         * to advance the animation.
         *
         * @param morphTargetBuffer the morph target buffer
         * @return Builder reference for chaining calls.
         */
        fun morphing(morphTargetBuffer: MorphTargetBuffer): Builder = apply {
            FilaRenderableManagerBuilder_morphing_MorphTargetBuffer(nativeBuilder, morphTargetBuffer.nativeHandle)
        }
        /**
         * Specifies the morph target buffer offset for a primitive.
         *
         * @param level the level of detail (lod), only 0 can be specified
         * @param primitiveIndex zero-based index of the primitive
         * @param offset specifies where in the morph target buffer to start reading (expressed as a number of vertices)
         * @return Builder reference for chaining calls.
         */
        fun morphing(level: Int, primitiveIndex: Int, offset: Int): Builder = apply {
            FilaRenderableManagerBuilder_morphing_uint8_t_size_t_size_t(nativeBuilder, level, primitiveIndex, offset)
        }
        /**
         * Sets the drawing order for blended primitives. The drawing order is either global or
         * local (default) to this Renderable. In either case, the Renderable priority takes
         * precedence.
         *
         * @param primitiveIndex the primitive of interest
         * @param blendOrder draw order number (0 by default). Only the lowest 15 bits are used.
         * @return Builder reference for chaining calls.
         */
        fun blendOrder(primitiveIndex: Int, blendOrder: Int): Builder = apply {
            FilaRenderableManagerBuilder_blendOrder(nativeBuilder, primitiveIndex, blendOrder)
        }
        /**
         * Sets whether the blend order is global or local to this Renderable (by default).
         *
         * @param primitiveIndex the primitive of interest
         * @param enabled true for global, false for local blend ordering.
         * @return Builder reference for chaining calls.
         */
        fun globalBlendOrderEnabled(primitiveIndex: Int, enabled: Boolean): Builder = apply {
            FilaRenderableManagerBuilder_globalBlendOrderEnabled(nativeBuilder, primitiveIndex, enabled)
        }
        /**
         * Specifies the number of draw instances of this renderable. The default is 1 instance and
         * the maximum number of instances allowed is 32767. 0 is invalid.
         *
         * All instances are culled using the same bounding box. The material must set its instanced
         * parameter to true in order to use getInstanceIndex() in the shaders.
         *
         * @param instanceCount the number of instances silently clamped between 1 and 32767.
         * @return Builder reference for chaining calls.
         */
        fun instances(instanceCount: Int): Builder = apply { FilaRenderableManagerBuilder_instances(nativeBuilder, instanceCount) }
        /**
         * Adds the Renderable component to an entity.
         *
         * If this component already exists on the given entity and the construction is successful,
         * it is first destroyed as if destroy(entity) was called. In case of error,
         * the existing component is unmodified.
         *
         * @param engine Reference to the filament Engine to associate this Renderable with.
         * @param entity Entity to add the Renderable component to.
         * @return [Result.Success] if the component was created
         */
        fun build(engine: Engine, entity: Entity): Result {
            val result = FilaRenderableManagerBuilder_build(nativeBuilder, engine.nativeHandle, entity)
            FilaRenderableManagerBuilder_destroy(nativeBuilder)
            scope.release()
            return if (result == 0) Result.Success else Result.Error
        }
    }

    /**
     * Checks if the given entity already has a renderable component.
     *
     * @param e the entity to check
     * @return true if the entity has a renderable component
     */
    fun hasComponent(e: Entity): Boolean = FilaRenderableManager_hasComponent(nativeHandle, e)
    /**
     * Gets a temporary handle that can be used to access the renderable state.
     *
     * @param e the entity to get the instance for
     * @return Non-zero handle if the entity has a renderable component, 0 otherwise
     */
    fun getInstance(e: Entity): EntityInstance = FilaRenderableManager_getInstance(nativeHandle, e)
    /** The number of renderable components. */
    val componentCount: Int get() = FilaRenderableManager_getComponentCount(nativeHandle)
    /** Returns true if there are no renderable components. */
    fun empty(): Boolean = FilaRenderableManager_empty(nativeHandle)
    /** Returns the Entity of the component from its Instance. */
    fun getEntity(i: EntityInstance): Entity = FilaRenderableManager_getEntity(nativeHandle, i)
    /** All entities with a renderable component, in no particular order. */
    val entities: IntArray get() = readInts(FilaRenderableManager_getEntities(nativeHandle), componentCount)
    /**
     * Destroys the renderable component in the given entity.
     *
     * @param e the entity whose renderable component is to be destroyed
     */
    fun destroy(e: Entity) = FilaRenderableManager_destroy(nativeHandle, e)

    /**
     * Changes the bounding box used for frustum culling.
     * The renderable must not have staticGeometry enabled.
     *
     * @param instance Instance of the component obtained from getInstance()
     * @param aabb the new axis-aligned bounding box
     */
    fun setAxisAlignedBoundingBox(instance: EntityInstance, aabb: Box) {
        aabb.useNative { FilaRenderableManager_setAxisAlignedBoundingBox(nativeHandle, instance, it) }
    }
    /**
     * Gets the bounding box used for frustum culling.
     *
     * @param instance Instance of the component obtained from getInstance()
     * @return the axis-aligned bounding box
     */
    fun getAxisAlignedBoundingBox(instance: EntityInstance): Box = box { FilaRenderableManager_getAxisAlignedBoundingBox(nativeHandle, instance, it) }

    /**
     * Changes the visibility bits.
     *
     * @param instance Instance of the component obtained from getInstance()
     * @param select the set of bits to affect
     * @param values the replacement values for the affected bits
     */
    fun setLayerMask(instance: EntityInstance, select: Int, values: Int) = FilaRenderableManager_setLayerMask(nativeHandle, instance, select, values)
    /** Get the visibility bits. */
    fun getLayerMask(instance: EntityInstance): Int = FilaRenderableManager_getLayerMask(nativeHandle, instance)
    /**
     * Changes the coarse-level draw ordering.
     *
     * @param instance Instance of the component obtained from getInstance()
     * @param priority the new priority clamped to [0..7]
     */
    fun setPriority(instance: EntityInstance, priority: Int) = FilaRenderableManager_setPriority(nativeHandle, instance, priority)
    /** Get the coarse-level draw ordering. */
    fun getPriority(instance: EntityInstance): Int = FilaRenderableManager_getPriority(nativeHandle, instance)
    /**
     * Changes the channel a renderable is associated to.
     *
     * @param instance Instance of the component obtained from getInstance()
     * @param channel the new channel value clamped to [0..7]
     */
    fun setChannel(instance: EntityInstance, channel: Int) = FilaRenderableManager_setChannel(nativeHandle, instance, channel)
    /** Get the channel a renderable is associated to. */
    fun getChannel(instance: EntityInstance): Int = FilaRenderableManager_getChannel(nativeHandle, instance)
    /** Changes whether or not frustum culling is on. */
    fun setCulling(instance: EntityInstance, enable: Boolean) = FilaRenderableManager_setCulling(nativeHandle, instance, enable)
    /** Get whether or not frustum culling is on. */
    fun isCullingEnabled(instance: EntityInstance): Boolean = FilaRenderableManager_isCullingEnabled(nativeHandle, instance)
    /** Changes whether or not the large-scale fog is applied to this renderable. */
    fun setFogEnabled(instance: EntityInstance, enable: Boolean) = FilaRenderableManager_setFogEnabled(nativeHandle, instance, enable)
    /** Returns whether large-scale fog is enabled for this renderable. */
    fun getFogEnabled(instance: EntityInstance): Boolean = FilaRenderableManager_getFogEnabled(nativeHandle, instance)
    /**
     * Enables or disables a light channel for this renderable.
     *
     * @param channel light channel to enable or disable, between 0 and 7
     * @param enable whether to enable the light channel
     */
    fun setLightChannel(instance: EntityInstance, channel: Int, enable: Boolean) = FilaRenderableManager_setLightChannel(nativeHandle, instance, channel, enable)
    /** Returns whether a light channel is enabled on this renderable. */
    fun getLightChannel(instance: EntityInstance, channel: Int): Boolean = FilaRenderableManager_getLightChannel(nativeHandle, instance, channel)
    /** Changes whether or not the renderable casts shadows. */
    fun setCastShadows(instance: EntityInstance, enable: Boolean) = FilaRenderableManager_setCastShadows(nativeHandle, instance, enable)
    /** Changes whether or not the renderable can receive shadows. */
    fun setReceiveShadows(instance: EntityInstance, enable: Boolean) = FilaRenderableManager_setReceiveShadows(nativeHandle, instance, enable)
    /** Changes whether or not the renderable can use screen-space contact shadows. */
    fun setScreenSpaceContactShadows(instance: EntityInstance, enable: Boolean) = FilaRenderableManager_setScreenSpaceContactShadows(nativeHandle, instance, enable)
    /** Checks if the renderable can cast shadows. */
    fun isShadowCaster(instance: EntityInstance): Boolean = FilaRenderableManager_isShadowCaster(nativeHandle, instance)
    /** Checks if the renderable can receive shadows. */
    fun isShadowReceiver(instance: EntityInstance): Boolean = FilaRenderableManager_isShadowReceiver(nativeHandle, instance)
    /** Checks if the renderable can use screen-space contact shadows. */
    fun isScreenSpaceContactShadowsEnabled(instance: EntityInstance): Boolean = FilaRenderableManager_isScreenSpaceContactShadowsEnabled(nativeHandle, instance)

    /**
     * Updates the bone transforms in the range [offset, offset + boneCount).
     * The bones must be pre-allocated using Builder.skinning().
     *
     * @param transforms the bone transforms
     * @param boneCount the number of bones to set
     * @param offset the index of the first bone to set
     */
    fun setBones(instance: EntityInstance, transforms: Array<Bone>, boneCount: Int = transforms.size, offset: Int = 0) {
        transforms.toFloats().usePinned { FilaRenderableManager_setBones_Bone_size_t_size_t(nativeHandle, instance, it, boneCount, offset) }
    }
    /**
     * Updates the bone transforms in the range [offset, offset + boneCount).
     * The bones must be pre-allocated using Builder.skinning().
     *
     * @param transforms 4x4 bone transforms, 16 floats per bone
     * @param boneCount the number of bones to set
     * @param offset the index of the first bone to set
     */
    fun setBones(instance: EntityInstance, transforms: FloatArray, boneCount: Int = transforms.size / 16, offset: Int = 0) {
        transforms.usePinned { FilaRenderableManager_setBones_mat4f_size_t_size_t(nativeHandle, instance, it, boneCount, offset) }
    }
    /**
     * Associates a region of a SkinningBuffer to a renderable instance.
     *
     * Note: due to hardware limitations offset + 256 must be smaller or equal to
     * skinningBuffer.getBoneCount()
     *
     * @param skinningBuffer skinning buffer to associate to the instance
     * @param count Size of the region in bones, must be smaller or equal to 256.
     * @param offset Start offset of the region in bones
     */
    fun setSkinningBuffer(instance: EntityInstance, skinningBuffer: SkinningBuffer, count: Int, offset: Int) {
        FilaRenderableManager_setSkinningBuffer(nativeHandle, instance, skinningBuffer.nativeHandle, count, offset)
    }
    /**
     * Updates the vertex morphing weights on a renderable, all zeroes by default.
     *
     * The renderable must be built with morphing enabled, see Builder.morphing(). In legacy
     * morphing mode, only the first 4 weights are considered.
     *
     * @param weights morph target weights to set
     * @param offset index of the first morph target weight to set
     */
    fun setMorphWeights(instance: EntityInstance, weights: FloatArray, offset: Int = 0) {
        weights.usePinned { FilaRenderableManager_setMorphWeights(nativeHandle, instance, it, weights.size, offset) }
    }
    /**
     * Associates a MorphTargetBuffer offset to the given primitive.
     *
     * @param level the level of detail (lod), only 0 can be specified
     * @param primitiveIndex the primitive of interest
     * @param offset specifies where in the morph target buffer to start reading (expressed as a number of vertices)
     */
    fun setMorphTargetBufferOffsetAt(instance: EntityInstance, level: Int, primitiveIndex: Int, offset: Int) {
        FilaRenderableManager_setMorphTargetBufferOffsetAt(nativeHandle, instance, level, primitiveIndex, offset)
    }
    /** Get the MorphTargetBuffer of the given renderable, or null if it has none. */
    fun getMorphTargetBuffer(instance: EntityInstance): MorphTargetBuffer? =
        FilaRenderableManager_getMorphTargetBuffer(nativeHandle, instance).takeIf { it != NullPointer }?.let { MorphTargetBuffer(it) }
    /** Get the number of morph targets in the given entity. */
    fun getMorphTargetCount(instance: EntityInstance): Int = FilaRenderableManager_getMorphTargetCount(nativeHandle, instance)
    /** Get the number of primitives in the given entity. */
    fun getPrimitiveCount(instance: EntityInstance): Int = FilaRenderableManager_getPrimitiveCount(nativeHandle, instance)
    /** Get the number of instances in the given entity. */
    fun getInstanceCount(instance: EntityInstance): Int = FilaRenderableManager_getInstanceCount(nativeHandle, instance)

    /**
     * Changes a material instance on a primitive.
     *
     * @param primitiveIndex the primitive of interest
     * @param materialInstance the material instance to bind
     */
    fun setMaterialInstanceAt(instance: EntityInstance, primitiveIndex: Int, materialInstance: MaterialInstance) {
        FilaRenderableManager_setMaterialInstanceAt(nativeHandle, instance, primitiveIndex, materialInstance.nativeHandle)
    }
    /** Clears the material instance for a primitive (revert to default). */
    fun clearMaterialInstanceAt(instance: EntityInstance, primitiveIndex: Int) {
        FilaRenderableManager_clearMaterialInstanceAt(nativeHandle, instance, primitiveIndex)
    }
    /**
     * Gets a material instance on a primitive.
     *
     * @return the material instance, or null for default
     */
    fun getMaterialInstanceAt(instance: EntityInstance, primitiveIndex: Int): MaterialInstance? {
        val handle = FilaRenderableManager_getMaterialInstanceAt(nativeHandle, instance, primitiveIndex)
        return if (handle != NullPointer) MaterialInstance(handle) else null
    }

    /**
     * Changes the geometry for a primitive.
     *
     * @param primitiveIndex the primitive of interest
     * @param type primitive type
     * @param vertices vertex buffer for this primitive
     * @param indices index buffer for this primitive
     * @param offset index offset in the index buffer
     * @param count number of indices to render
     */
    fun setGeometryAt(instance: EntityInstance, primitiveIndex: Int, type: PrimitiveType, vertices: VertexBuffer, indices: IndexBuffer, offset: Int, count: Int) =
        FilaRenderableManager_setGeometryAt_IndexBuffer_size_t_size_t(nativeHandle, instance, primitiveIndex, type.toNative(), vertices.nativeHandle, indices.nativeHandle, offset, count)
    /**
     * Changes the geometry for a non-indexed primitive.
     *
     * @param offset vertex offset in the vertex buffer
     * @param count number of vertices to render
     */
    fun setGeometryAt(instance: EntityInstance, primitiveIndex: Int, type: PrimitiveType, vertices: VertexBuffer, offset: Int, count: Int) =
        FilaRenderableManager_setGeometryAt_size_t_size_t(nativeHandle, instance, primitiveIndex, type.toNative(), vertices.nativeHandle, offset, count)

    /**
     * Sets the drawing order for blended primitives.
     *
     * @param order draw order number. Only the lowest 15 bits are used.
     */
    fun setBlendOrderAt(instance: EntityInstance, primitiveIndex: Int, order: Int) =
        FilaRenderableManager_setBlendOrderAt(nativeHandle, instance, primitiveIndex, order)
    /** Gets the drawing order for blended primitives. */
    fun getBlendOrderAt(instance: EntityInstance, primitiveIndex: Int): Int = FilaRenderableManager_getBlendOrderAt(nativeHandle, instance, primitiveIndex)
    /** Sets whether the blend order is global or local to this Renderable. */
    fun setGlobalBlendOrderEnabledAt(instance: EntityInstance, primitiveIndex: Int, enabled: Boolean) =
        FilaRenderableManager_setGlobalBlendOrderEnabledAt(nativeHandle, instance, primitiveIndex, enabled)
    /** Gets whether the blend order is global or local to this Renderable. */
    fun isGlobalBlendOrderEnabledAt(instance: EntityInstance, primitiveIndex: Int): Boolean =
        FilaRenderableManager_isGlobalBlendOrderEnabledAt(nativeHandle, instance, primitiveIndex)
    /** Retrieves the set of enabled attribute slots in the given primitive's VertexBuffer. */
    fun getEnabledAttributesAt(instance: EntityInstance, primitiveIndex: Int): Set<VertexBuffer.VertexAttribute> =
        attributeBitsetToSet(FilaRenderableManager_getEnabledAttributesAt(nativeHandle, instance, primitiveIndex))
}

// PrimitiveType skips upstream's unused LINE_LOOP (2).
private fun RenderableManager.PrimitiveType.toNative(): Int = when (this) {
    RenderableManager.PrimitiveType.POINTS -> 0
    RenderableManager.PrimitiveType.LINES -> 1
    RenderableManager.PrimitiveType.LINE_STRIP -> 3
    RenderableManager.PrimitiveType.TRIANGLES -> 4
    RenderableManager.PrimitiveType.TRIANGLE_STRIP -> 5
}

// filament::Box's layout: center then halfExtent.

// RenderableManager::Bone's layout: quatf, float3, reserved float.
internal fun Array<RenderableManager.Bone>.toFloats() = FloatArray(size * 8).also { out ->
    forEachIndexed { i, b ->
        b.unitQuaternion.copyInto(out, i * 8, 0, 4)
        b.translation.copyInto(out, i * 8 + 4, 0, 3)
    }
}

/** Converts a native attribute bitset into the corresponding set of [VertexBuffer.VertexAttribute]. */
internal fun attributeBitsetToSet(bits: Int): Set<VertexBuffer.VertexAttribute> =
    VertexBuffer.VertexAttribute.entries.filterTo(mutableSetOf()) { (bits shr it.value) and 1 == 1 }
