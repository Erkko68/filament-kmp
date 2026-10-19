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

    /** The 36 bytes of [triangleGltf]'s buffer: one triangle spanning (0,0,0)..(1,1,0). */
    val triangleBuffer = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -128, 63, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -128, 63, 0, 0, 0, 0)

    /** A .gltf whose one triangle lives in the buffer at [bufferUri]. */
    fun triangleGltf(bufferUri: String) = """{"asset":{"version":"2.0"},"scene":0,"scenes":[{"nodes":[0]}],"nodes":[{"mesh":0}],
        "meshes":[{"primitives":[{"attributes":{"POSITION":0}}]}],
        "accessors":[{"bufferView":0,"componentType":5126,"count":3,"type":"VEC3","min":[0,0,0],"max":[1,1,0]}],
        "bufferViews":[{"buffer":0,"byteLength":36}],
        "buffers":[{"uri":"$bufferUri","byteLength":36}]}""".encodeToByteArray()
}
