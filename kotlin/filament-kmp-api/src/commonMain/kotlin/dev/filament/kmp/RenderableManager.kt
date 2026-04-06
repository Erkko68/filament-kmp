package dev.filament.kmp

expect class RenderableManager {
    enum class PrimitiveType { POINTS, LINES, TRIANGLES, NONE }
    enum class GeometryType { STATIC, DYNAMIC }

    class Builder(count: Int) {
        fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer): Builder
        fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer, offset: Int, count: Int): Builder
        fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer, offset: Int, minIndex: Int, maxIndex: Int, count: Int): Builder
        
        fun geometryType(type: GeometryType): Builder
        fun material(index: Int, materialInstance: MaterialInstance): Builder
        fun blendOrder(index: Int, blendOrder: Int): Builder
        fun globalBlendOrderEnabled(index: Int, enabled: Boolean): Builder
        fun boundingBox(box: Box): Builder
        fun layerMask(select: Int, value: Int): Builder
        fun priority(priority: Int): Builder
        fun channel(channel: Int): Builder
        fun culling(enabled: Boolean): Builder
        fun castShadows(enabled: Boolean): Builder
        fun receiveShadows(enabled: Boolean): Builder
        fun screenSpaceContactShadows(enabled: Boolean): Builder
        fun skinning(boneCount: Int): Builder
        fun skinning(boneCount: Int, bones: FloatArray): Builder
        fun morphing(targetCount: Int): Builder
        fun fog(enabled: Boolean): Builder
        fun lightChannel(channel: Int, enable: Boolean): Builder
        fun instances(instanceCount: Int): Builder
        fun build(engine: Engine, entity: Entity)
    }

    fun hasComponent(entity: Entity): Boolean
    fun getInstance(entity: Entity): EntityInstance
    fun destroy(entity: Entity)
    
    fun setAxisAlignedBoundingBox(instance: EntityInstance, box: Box)
    fun getAxisAlignedBoundingBox(instance: EntityInstance, outBox: Box?): Box
    
    fun setLayerMask(instance: EntityInstance, select: Int, value: Int)
    fun setPriority(instance: EntityInstance, priority: Int)
    fun getPriority(instance: EntityInstance): Int
    fun setChannel(instance: EntityInstance, channel: Int)
    fun getChannel(instance: EntityInstance): Int
    fun setCulling(instance: EntityInstance, enabled: Boolean)
    fun isCullingEnabled(instance: EntityInstance): Boolean
    fun setFogEnabled(instance: EntityInstance, enabled: Boolean)
    fun getFogEnabled(instance: EntityInstance): Boolean
    fun setCastShadows(instance: EntityInstance, enabled: Boolean)
    fun setReceiveShadows(instance: EntityInstance, enabled: Boolean)
    fun setScreenSpaceContactShadows(instance: EntityInstance, enabled: Boolean)
    fun isShadowCaster(instance: EntityInstance): Boolean
    fun isShadowReceiver(instance: EntityInstance): Boolean
    fun isScreenSpaceContactShadowsEnabled(instance: EntityInstance): Boolean
    
    fun getPrimitiveCount(instance: EntityInstance): Int
    fun getInstanceCount(instance: EntityInstance): Int
    
    fun setMaterialInstanceAt(instance: EntityInstance, primitiveIndex: Int, materialInstance: MaterialInstance)
    fun getMaterialInstanceAt(instance: EntityInstance, primitiveIndex: Int): MaterialInstance?
    
    fun setGeometryAt(instance: EntityInstance, primitiveIndex: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer, offset: Int, count: Int)
    
    fun setBlendOrderAt(instance: EntityInstance, primitiveIndex: Int, blendOrder: Int)
    fun getBlendOrderAt(instance: EntityInstance, primitiveIndex: Int): Int
    fun setGlobalBlendOrderEnabledAt(instance: EntityInstance, primitiveIndex: Int, enabled: Boolean)
    fun isGlobalBlendOrderEnabledAt(instance: EntityInstance, primitiveIndex: Int): Boolean
    
    fun setLightChannel(instance: EntityInstance, channel: Int, enable: Boolean)
    fun getLightChannel(instance: EntityInstance, channel: Int): Boolean
}
