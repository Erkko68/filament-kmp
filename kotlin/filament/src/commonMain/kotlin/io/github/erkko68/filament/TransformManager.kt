package io.github.erkko68.filament

expect class TransformManager {
    fun hasComponent(entity: Entity): Boolean
    fun getInstance(entity: Entity): EntityInstance
    
    fun create(entity: Entity): EntityInstance
    fun create(entity: Entity, parent: EntityInstance, localTransform: FloatArray?): EntityInstance
    fun create(entity: Entity, parent: EntityInstance, localTransform: DoubleArray?): EntityInstance
    
    fun destroy(entity: Entity)
    
    fun setParent(instance: EntityInstance, newParent: EntityInstance)
    fun getParent(instance: EntityInstance): Entity
    
    fun getChildCount(instance: EntityInstance): Int
    fun getChildren(instance: EntityInstance, outEntities: IntArray?): IntArray
    
    fun setTransform(instance: EntityInstance, localTransform: FloatArray)
    fun setTransform(instance: EntityInstance, localTransform: DoubleArray)
    
    fun getTransform(instance: EntityInstance, outLocalTransform: FloatArray?): FloatArray
    fun getTransform(instance: EntityInstance, outLocalTransform: DoubleArray?): DoubleArray
    
    fun getWorldTransform(instance: EntityInstance, outWorldTransform: FloatArray?): FloatArray
    fun getWorldTransform(instance: EntityInstance, outWorldTransform: DoubleArray?): DoubleArray
    
    fun openLocalTransformTransaction()
    fun commitLocalTransformTransaction()
    
    fun setAccurateTranslationsEnabled(enable: Boolean)
    fun isAccurateTranslationsEnabled(): Boolean
}
