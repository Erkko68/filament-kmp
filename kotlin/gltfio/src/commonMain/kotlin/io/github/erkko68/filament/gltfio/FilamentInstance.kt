package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Box

/**
 * FilamentInstance provides access to a hierarchy of entities instanced from a glTF asset.
 *
 * Every entity has a TransformManager component. Some entities also have Name or Renderable
 * components. Instances share material and geometry data with the parent FilamentAsset but
 * maintain independent entity hierarchies, transforms, and animation states.
 *
 * **Key features:**
 * - Independent entity tree for each instance
 * - Skeletal animation via getAnimator() (independent per-instance or shared from asset)
 * - Material variant switching via applyMaterialVariant()
 * - Skin/skeleton access for joint manipulation
 * - Independent bounding box and transform root
 *
 * **Animation ownership:**
 * Each instance has its own Animator, but it can also be obtained from the parent
 * FilamentAsset. Using the asset's animator means all instances share the same animation frame;
 * using instance animators allows independent control.
 *
 * @see FilamentAsset
 * @see Animator
 * @see AssetLoader
 */
expect class FilamentInstance {
    /**
     * Create a new FilamentInstance.
     *
     * Instances are normally created via AssetLoader.createInstance(asset) but can be
     * manually constructed for advanced use cases.
     */
    constructor()

    fun getRoot(): Int
    fun getEntities(): IntArray
    fun getEntityCount(): Int
    
    fun getAnimator(): Animator
    fun getBoundingBox(): Box
    
    fun getAsset(): FilamentAsset
    
    fun getSkinCount(): Int
    fun getSkinNames(): Array<String>
    fun attachSkin(skinIndex: Int, target: Int)
    fun detachSkin(skinIndex: Int, target: Int)
    fun getJointCountAt(skinIndex: Int): Int
    fun getJointsAt(skinIndex: Int): IntArray
    
    fun applyMaterialVariant(variantIndex: Int)
    fun getMaterialInstances(): Array<io.github.erkko68.filament.MaterialInstance>
    fun getMaterialVariantNames(): Array<String>
}
