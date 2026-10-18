package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ViewTest : FilamentTestFixture() {
    @Test
    fun testGridSize() {
        val view = engine.createView()
        view.gridSize = 2.5
        assertEquals(2.5, view.gridSize)
        // effectiveGridSize is only computed during rendering — just exercise the getter.
        assertTrue(view.effectiveGridSize >= 0.0)
        engine.destroy(view)
    }

    @Test
    fun testViewLifecycleAndProperties() {
        val view = engine.createView()
        assertTrue(engine.isValid(view))

        view.name = "TestView"
        assertEquals("TestView", view.name)

        view.viewport = Viewport(10, 20, 1024, 768)
        assertEquals(Viewport(10, 20, 1024, 768), view.viewport)
        assertEquals(1034, view.viewport.right())
        assertEquals(788, view.viewport.top())
        assertTrue(Viewport(0, 0, 0, 10).empty())

        view.blendMode = BlendMode.TRANSLUCENT

        val grading = ColorGrading.Builder().build(engine)
        view.colorGrading = grading
        assertEquals(grading, view.colorGrading)
        view.colorGrading = null
        assertNull(view.colorGrading)
        engine.destroy(grading)

        val color = Texture.Builder().width(8).height(8).format(Texture.InternalFormat.RGBA8)
            .usage(Texture.Usage.COLOR_ATTACHMENT).build(engine)
        val target = RenderTarget.Builder().texture(RenderTarget.AttachmentPoint.COLOR, color).build(engine)
        view.renderTarget = target
        assertEquals(target, view.renderTarget)
        view.renderTarget = null
        assertNull(view.renderTarget)
        engine.destroy(target)
        engine.destroy(color)
        assertEquals(BlendMode.TRANSLUCENT, view.blendMode)

        view.setVisibleLayers(0x3, 0x1)
        view.setLayerEnabled(0, true)
        assertEquals(0x1, view.visibleLayers and 0x3)
        view.setLayerEnabled(1, true)
        assertEquals(0x3, view.visibleLayers and 0x3)

        view.setChannelDepthClearEnabled(2, true)
        assertTrue(view.isChannelDepthClearEnabled(2))
        assertFalse(view.isChannelDepthClearEnabled(3))

        view.isPostProcessingEnabled = true
        assertTrue(view.isPostProcessingEnabled)

        view.dithering = Dithering.TEMPORAL
        assertEquals(Dithering.TEMPORAL, view.dithering)

        // minScale == maxScale so `enabled` survives without frame-time support (NOOP since 1.72.0).
        view.dynamicResolutionOptions = DynamicResolutionOptions().apply { enabled = true; minScale = floatArrayOf(1f, 1f); maxScale = floatArrayOf(1f, 1f) }
        assertTrue(view.dynamicResolutionOptions.enabled)

        // View Boolean Flags
        view.isFrustumCullingEnabled = true
        assertTrue(view.isFrustumCullingEnabled)
        view.isShadowingEnabled = true
        assertTrue(view.isShadowingEnabled)
        view.isScreenSpaceRefractionEnabled = true
        assertTrue(view.isScreenSpaceRefractionEnabled)
        view.isStencilBufferEnabled = true
        assertTrue(view.isStencilBufferEnabled)
        view.isFrontFaceWindingInverted = true
        assertTrue(view.isFrontFaceWindingInverted)
        view.isTransparentPickingEnabled = true
        assertTrue(view.isTransparentPickingEnabled)

        // Globals & Dynamic lighting
        view.setMaterialGlobal(0, floatArrayOf(1.5f, 2.5f, 3.5f, 4.5f))
        val global = view.getMaterialGlobal(0)
        assertEquals(1.5f, global[0])
        assertEquals(2.5f, global[1])
        assertEquals(3.5f, global[2])
        assertEquals(4.5f, global[3])

        view.setDynamicLightingOptions(0.5f, 50f)
        assertTrue(view.fogEntity >= 0)

        // Never rendered — the visible-renderable cache is invalid, so -1.
        assertEquals(-1, view.visibleRenderableCount)

        view.antiAliasing = AntiAliasing.FXAA
        assertEquals(AntiAliasing.FXAA, view.antiAliasing)

        // Camera, Scene, etc attachments
        assertNull(view.scene)
        val scene = engine.createScene()
        view.scene = scene
        assertSame(scene, view.scene)

        assertFalse(view.hasCamera)
        val entity = EntityManager.get().create()
        val camera = engine.createCamera(entity)
        view.camera = camera
        assertTrue(view.hasCamera)
        assertSame(camera, view.camera)
        view.clearFrameHistory(engine)

        // Cleanup
        engine.destroyCameraComponent(entity)
        EntityManager.get().destroy(entity)
        engine.destroy(scene)
        engine.destroy(view)
    }
}
