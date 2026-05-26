package io.github.erkko68.filament.testutils

actual object TestMaterials {
    actual fun getEmissiveMaterialBytes(): ByteArray {
        val inputStream = TestMaterials::class.java.classLoader.getResourceAsStream("emissive.filamat")
            ?: TestMaterials::class.java.getResourceAsStream("/emissive.filamat")
            ?: throw IllegalStateException("Could not find emissive.filamat in classpath resources")
        return inputStream.use { it.readBytes() }
    }
}
