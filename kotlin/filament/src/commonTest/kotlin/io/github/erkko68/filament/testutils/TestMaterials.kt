package io.github.erkko68.filament.testutils

/**
 * Test materials, available on every platform. The bytes are embedded at build
 * time from src/commonTest/materials by :kotlin:filament:generateEmbeddedMaterials,
 * so no per-platform resource IO is needed (see [EmbeddedMaterials]).
 */
object TestMaterials {
    /** Unlit-style emissive material — visible without any light in the scene. */
    fun getEmissiveMaterialBytes(): ByteArray = EmbeddedMaterials.emissive

    /**
     * Lit material (baseColor/metallic/roughness) that receives and casts shadows,
     * with vsm variants kept — see test_lit.mat. Used by the Tier C frame tests.
     */
    fun getLitMaterialBytes(): ByteArray = EmbeddedMaterials.test_lit

    /** Unlit material declaring bool/int/float specialization constants — see constants.mat. */
    fun getConstantsMaterialBytes(): ByteArray = EmbeddedMaterials.constants
}
