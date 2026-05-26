package io.github.erkko68.filament.gltfio

import kotlin.test.Test
import kotlin.test.assertTrue

class GltfioSmokeTest {
    @Test
    fun verifyGltfioApi() {
        Gltfio.init()
        assertTrue(true, "Gltfio API signatures resolved successfully.")
    }
}
