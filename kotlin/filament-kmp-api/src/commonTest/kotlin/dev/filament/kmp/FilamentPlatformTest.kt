package dev.filament.kmp

import kotlin.test.Test
import kotlin.test.assertTrue

class FilamentPlatformTest {
    @Test
    fun platformNameIsNotBlank() {
        assertTrue(FilamentPlatform.name.isNotBlank())
    }
}

