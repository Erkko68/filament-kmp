package dev.filament.kmp

import kotlin.test.Test
import kotlin.test.assertEquals

class EngineApiTest {
    @Test
    fun defaultConfigMatchesExpectedDefaults() {
        val config = EngineConfig()
        assertEquals(EngineStereoscopicType.NONE, config.stereoscopicType)
        assertEquals(1u, config.stereoscopicEyeCount)
    }
}

