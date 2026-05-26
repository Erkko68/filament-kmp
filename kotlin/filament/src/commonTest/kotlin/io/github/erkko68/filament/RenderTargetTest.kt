package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RenderTargetTest : FilamentTestFixture() {
    @Test
    fun testRenderTargetLifecycle() {
        val tex = Texture.Builder()
            .width(64)
            .height(64)
            .depth(1)
            .samples(1)
            .sampler(Texture.Sampler.SAMPLER_2D)
            .levels(1)
            .usage(Texture.Usage.COLOR_ATTACHMENT)
            .format(Texture.InternalFormat.RGBA8)
            .build(engine)

        val target = RenderTarget.Builder()
            .texture(RenderTarget.AttachmentPoint.COLOR, tex)
            .mipLevel(RenderTarget.AttachmentPoint.COLOR, 0)
            .face(RenderTarget.AttachmentPoint.COLOR, Texture.CubemapFace.POSITIVE_X)
            .layer(RenderTarget.AttachmentPoint.COLOR, 0)
            .build(engine)

        assertNotNull(target)
        assertTrue(engine.isValidRenderTarget(target))

        val retrieved = target.getTexture(RenderTarget.AttachmentPoint.COLOR)
        if (retrieved != null) {
            assertEquals(tex.getWidth(), retrieved.getWidth())
            assertEquals(tex.getFormat(), retrieved.getFormat())
        }
        assertEquals(0, target.getMipLevel(RenderTarget.AttachmentPoint.COLOR))
        assertEquals(Texture.CubemapFace.POSITIVE_X, target.getFace(RenderTarget.AttachmentPoint.COLOR))
        assertEquals(0, target.getLayer(RenderTarget.AttachmentPoint.COLOR))

        engine.destroyRenderTarget(target)
        engine.destroyTexture(tex)
    }
}
