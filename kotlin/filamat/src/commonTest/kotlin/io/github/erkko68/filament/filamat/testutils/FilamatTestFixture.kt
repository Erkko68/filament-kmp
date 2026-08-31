package io.github.erkko68.filament.filamat.testutils

import io.github.erkko68.filament.filamat.Filamat
import io.github.erkko68.filament.filamat.MaterialBuilder
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

open class FilamatTestFixture {
    @BeforeTest
    fun setUp() {
        Filamat.init()
        Filamat.init()
    }

    @AfterTest
    fun tearDown() {
        Filamat.shutdown()
    }
}
