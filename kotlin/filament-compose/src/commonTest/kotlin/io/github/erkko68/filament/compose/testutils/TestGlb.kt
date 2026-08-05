package io.github.erkko68.filament.compose.testutils

/**
 * Test glb assets, base64-embedded into a generated commonTest source at build time from
 * src/commonTest/glb by the :kotlin:filament-compose:generateEmbeddedGlb task, so no per-platform
 * resource IO is needed (see [EmbeddedGlb]). Mirrors :kotlin:gltfio's TestGlb.
 */
object TestGlb {
    /**
     * A single cube with morph targets and an animation driving them — the smallest asset that
     * exercises `GltfInstance`'s morph-weight path.
     */
    fun getAnimatedMorphCubeGlbBytes(): ByteArray = EmbeddedGlb.AnimatedMorphCube
}
