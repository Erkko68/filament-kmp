package io.github.erkko68.filament.wasm

import kotlin.test.Test
import kotlin.test.assertEquals

class Int64Test {
    @Test
    fun roundTripsThroughBigInt() {
        for (v in listOf(0L, 1L, -1L, 0xFFFFFFFFL, Long.MAX_VALUE, Long.MIN_VALUE, -0x1_0000_0001L)) {
            assertEquals(v, v.toI64().toKotlinLong())
        }
    }
}
