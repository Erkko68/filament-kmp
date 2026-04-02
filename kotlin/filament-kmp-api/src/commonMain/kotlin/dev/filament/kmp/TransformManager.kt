package dev.filament.kmp

/**
 * TransformManager is used to add transform components to entities.
 */
expect class TransformManager {
    fun hasComponent(@Entity entity: Int): Boolean

    @EntityInstance
    fun getInstance(@Entity entity: Int): Int

    @EntityInstance
    fun create(@Entity entity: Int): Int

    @EntityInstance
    fun create(@Entity entity: Int, @EntityInstance parent: Int, localTransform: FloatArray?): Int

    fun destroy(@Entity entity: Int)

    fun setParent(@EntityInstance i: Int, @EntityInstance newParent: Int)

    @Entity
    fun getParent(@EntityInstance i: Int): Int

    fun getChildCount(@EntityInstance i: Int): Int

    @Entity
    fun getChildren(@EntityInstance i: Int, outEntities: IntArray?): IntArray

    fun setTransform(@EntityInstance i: Int, localTransform: FloatArray)

    fun getTransform(@EntityInstance i: Int, outLocalTransform: FloatArray?): FloatArray

    fun getWorldTransform(@EntityInstance i: Int, outWorldTransform: FloatArray?): FloatArray

    fun getNativeObject(): Long
}

