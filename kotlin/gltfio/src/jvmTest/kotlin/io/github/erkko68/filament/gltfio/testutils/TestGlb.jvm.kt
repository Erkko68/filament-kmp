package io.github.erkko68.filament.gltfio.testutils

actual object TestGlb {
    actual fun getDuckGlbBytes(): ByteArray {
        val inputStream = TestGlb::class.java.classLoader.getResourceAsStream("Duck.glb")
            ?: TestGlb::class.java.getResourceAsStream("/Duck.glb")
            ?: throw IllegalStateException("Could not find Duck.glb in classpath resources")
        return inputStream.use { it.readBytes() }
    }
}
