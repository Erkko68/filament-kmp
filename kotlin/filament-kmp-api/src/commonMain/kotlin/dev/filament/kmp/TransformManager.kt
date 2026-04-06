package dev.filament.kmp

/**
 * `TransformManager` is used to add transform components to entities.
 *
 * <p>A transform component gives an entity a position and orientation in space in the coordinate
 * space of its parent transform. The `TransformManager` takes care of computing the
 * world-space transform of each component (i.e. its transform relative to the root).</p>
 *
 * <h1>Creation and destruction</h1>
 *
 * A transform component is created using [TransformManager.create] and destroyed by calling
 * [TransformManager.destroy].
 *
 * <pre>
 *  val engine = Engine.create()
 *  val entityManager = EntityManager.get()
 *  val objectEntity = entityManager.create()
 *
 *  val tcm = engine.getTransformManager()
 *
 *  // create the transform component
 *  tcm.create(objectEntity)
 *
 *  // set its transform
 *  val transform = ... // transform to set
 *  val i = tcm.getInstance(objectEntity)
 *  tcm.setTransform(i, transform)
 *
 *  // destroy the transform component
 *  tcm.destroy(objectEntity)
 * </pre>
 */
expect class TransformManager {

    fun hasComponent(entity: Int): Boolean
    fun getInstance(entity: Int): Int

    fun setAccurateTranslationsEnabled(enable: Boolean)
    fun isAccurateTranslationsEnabled(): Boolean

    fun create(entity: Int): Int
    fun create(entity: Int, parent: Int, localTransform: FloatArray?): Int
    fun create(entity: Int, parent: Int, localTransform: DoubleArray?): Int

    fun destroy(entity: Int)

    fun setParent(instance: Int, newParent: Int)
    fun getParent(instance: Int): Int

    fun getChildCount(instance: Int): Int
    fun getChildren(instance: Int, outEntities: IntArray?): IntArray

    fun setTransform(instance: Int, localTransform: FloatArray)
    fun setTransform(instance: Int, localTransform: DoubleArray)

    fun getTransform(instance: Int, outLocalTransform: FloatArray?): FloatArray
    fun getTransform(instance: Int, outLocalTransform: DoubleArray?): DoubleArray

    fun getWorldTransform(instance: Int, outWorldTransform: FloatArray?): FloatArray
    fun getWorldTransform(instance: Int, outWorldTransform: DoubleArray?): DoubleArray

    fun openLocalTransformTransaction()
    fun commitLocalTransformTransaction()
}
