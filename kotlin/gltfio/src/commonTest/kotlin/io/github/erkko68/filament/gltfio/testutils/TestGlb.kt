package io.github.erkko68.filament.gltfio.testutils

/**
 * Test glTF assets, available on every platform. The bytes are embedded at build
 * time from src/commonTest/glb by the :kotlin:gltfio:generateEmbeddedGlb task, so
 * no per-platform resource IO is needed (see [EmbeddedGlb]).
 */
object TestGlb {
    fun getDuckGlbBytes(): ByteArray = EmbeddedGlb.Duck
    fun getBoxAnimatedGlbBytes(): ByteArray = EmbeddedGlb.BoxAnimated
    fun getFoxGlbBytes(): ByteArray = EmbeddedGlb.Fox
    fun getAnimatedMorphCubeGlbBytes(): ByteArray = EmbeddedGlb.AnimatedMorphCube
    fun getMaterialVariantsGlbBytes(): ByteArray = EmbeddedGlb.MaterialVariants
}
