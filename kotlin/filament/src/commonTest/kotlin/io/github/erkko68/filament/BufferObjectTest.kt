package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BufferObjectTest : FilamentTestFixture() {
    @Test
    fun testBufferObjectLifecycle() {
        val buffer = BufferObject.Builder()
            .size(1024)
            .bindingType(BufferObject.BindingType.VERTEX)
            .build(engine)

        assertNotNull(buffer)

        assertEquals(1024, buffer.byteCount)

        val data = byteArrayOf(1, 2, 3, 4)
        buffer.setBuffer(engine, data)
        buffer.setBuffer(engine, data, 0, 4)
        
        var callbackFired = false
        buffer.setBuffer(engine, data, 0, 4) {
            callbackFired = true
        }
    }
}
