package io.github.erkko68.filament.filamat

import kotlin.test.Test
import kotlin.test.assertTrue

class FilamatSmokeTest {
    @Test
    fun verifyFilamatApi() {
        Filamat.init()
        assertTrue(true, "Filamat API signatures resolved successfully.")
    }
}
