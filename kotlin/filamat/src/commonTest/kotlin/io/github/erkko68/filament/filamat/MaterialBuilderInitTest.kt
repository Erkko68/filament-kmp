package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.filamat.testutils.FilamatTestFixture
import kotlin.test.Test

class MaterialBuilderInitTest : FilamatTestFixture() {
    @Test
    fun testInitIsIdempotent() {
        MaterialBuilder.init()
        MaterialBuilder.init()
        // Verifies that calling init() multiple times does not crash.
    }

    @Test
    fun testMaterialBuilderInitShutdownCycle() {
        MaterialBuilder.shutdown()
        MaterialBuilder.init()
        MaterialBuilder.shutdown()
        MaterialBuilder.init()
        // Verifies that init/shutdown can be called in sequence without crashing.
        // tearDown() in FilamatTestFixture calls shutdown() once more after this.
    }
}
