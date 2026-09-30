package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertNull

/**
 * A bad `.filamat` payload would C++-throw out of the wasm, which `catch (Throwable)` can't see on js.
 * `Material.Builder.build` sniffs the payload first and returns null instead.
 */
class MaterialBuildErrorTest : FilamentTestFixture() {
    @Test
    fun badPayloadBuildsNull() {
        assertNull(Material.Builder().payload(ByteArray(64) { 0xFF.toByte() }).build(engine))
    }
}
