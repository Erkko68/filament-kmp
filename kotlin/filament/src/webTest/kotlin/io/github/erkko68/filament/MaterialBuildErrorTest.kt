package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertFailsWith

/**
 * A bad `.filamat` payload C++-throws out of embind; on js that surfaces as a raw thrown number
 * that `catch (Throwable)` can't see. Guards the `catchingJsThrows` mapping in
 * `Material.Builder.build` that turns it into a catchable Kotlin exception.
 */
class MaterialBuildErrorTest : FilamentTestFixture() {
    @Test
    fun badPayloadThrowsCatchableKotlinException() {
        assertFailsWith<IllegalArgumentException> {
            Material.Builder().payload(ByteArray(64) { 0xFF.toByte() }).build(engine)
        }
    }
}
