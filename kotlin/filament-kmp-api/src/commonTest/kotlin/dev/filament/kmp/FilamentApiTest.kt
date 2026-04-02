package dev.filament.kmp

import kotlin.test.Test
import kotlin.test.assertTrue

class FilamentApiTest {
    @Test
    fun platformLabelIsAvailable() {
        assertTrue(FilamentApi.platformLabel().isNotBlank())
    }
}

