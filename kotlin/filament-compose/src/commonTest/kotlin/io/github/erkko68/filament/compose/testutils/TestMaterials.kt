package io.github.erkko68.filament.compose.testutils

/**
 * Compiled `.filamat` bytes for Tier-B tests that need a real material/material-instance to build a
 * renderable. Only the JVM target reads the bundled `emissive.filamat`; the other targets return an
 * empty array, so the gated Tier-B tests (`bytes.isEmpty() -> return`) skip there exactly like the
 * core `RenderingTestFixture` suites.
 */
expect object TestMaterials {
    fun getEmissiveMaterialBytes(): ByteArray
}
