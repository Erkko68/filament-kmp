package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.VertexBuffer.VertexAttribute
import io.github.erkko68.filament.filamat.testutils.FilamatTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MaterialBuilderTest : FilamatTestFixture() {

    // Ordinal checks verify that the Kotlin enum layout matches the native C++ enum layout.
    // Wrong ordinals mean silent mismatched arguments across the JNI/WASM boundary.

    @Test
    fun testShadingEnumOrdinals() {
        assertEquals(0, MaterialBuilder.Shading.UNLIT.ordinal)
        assertEquals(1, MaterialBuilder.Shading.LIT.ordinal)
        assertEquals(2, MaterialBuilder.Shading.SUBSURFACE.ordinal)
        assertEquals(3, MaterialBuilder.Shading.CLOTH.ordinal)
        assertEquals(4, MaterialBuilder.Shading.SPECULAR_GLOSSINESS.ordinal)
    }

    @Test
    fun testInterpolationEnumOrdinals() {
        assertEquals(0, MaterialBuilder.Interpolation.SMOOTH.ordinal)
        assertEquals(1, MaterialBuilder.Interpolation.FLAT.ordinal)
    }

    @Test
    fun testBlendingModeEnumOrdinals() {
        assertEquals(0, MaterialBuilder.BlendingMode.OPAQUE.ordinal)
        assertEquals(1, MaterialBuilder.BlendingMode.TRANSPARENT.ordinal)
        assertEquals(2, MaterialBuilder.BlendingMode.ADD.ordinal)
        assertEquals(3, MaterialBuilder.BlendingMode.MASKED.ordinal)
        assertEquals(4, MaterialBuilder.BlendingMode.FADE.ordinal)
        assertEquals(5, MaterialBuilder.BlendingMode.MULTIPLY.ordinal)
        assertEquals(6, MaterialBuilder.BlendingMode.SCREEN.ordinal)
        assertEquals(7, MaterialBuilder.BlendingMode.CUSTOM.ordinal)
    }

    @Test
    fun testPlatformEnumOrdinals() {
        assertEquals(0, MaterialBuilder.Platform.DESKTOP.ordinal)
        assertEquals(1, MaterialBuilder.Platform.MOBILE.ordinal)
        assertEquals(2, MaterialBuilder.Platform.ALL.ordinal)
    }

    @Test
    fun testTargetApiEnumOrdinals() {
        assertEquals(0, MaterialBuilder.TargetApi.OPENGL.ordinal)
        assertEquals(1, MaterialBuilder.TargetApi.VULKAN.ordinal)
        assertEquals(2, MaterialBuilder.TargetApi.METAL.ordinal)
        assertEquals(3, MaterialBuilder.TargetApi.WEBGPU.ordinal)
        assertEquals(4, MaterialBuilder.TargetApi.ALL.ordinal)
    }

    @Test
    fun testOptimizationEnumOrdinals() {
        assertEquals(0, MaterialBuilder.Optimization.NONE.ordinal)
        assertEquals(1, MaterialBuilder.Optimization.PREPROCESSOR.ordinal)
        assertEquals(2, MaterialBuilder.Optimization.SIZE.ordinal)
        assertEquals(3, MaterialBuilder.Optimization.PERFORMANCE.ordinal)
    }

    @Test
    fun testCullingModeEnumOrdinals() {
        assertEquals(0, MaterialBuilder.CullingMode.NONE.ordinal)
        assertEquals(1, MaterialBuilder.CullingMode.FRONT.ordinal)
        assertEquals(2, MaterialBuilder.CullingMode.BACK.ordinal)
        assertEquals(3, MaterialBuilder.CullingMode.FRONT_AND_BACK.ordinal)
    }

    @Test
    fun testMaterialBuilderChainingAndBuild() {
        try {
            val pkg = MaterialBuilder()
                .name("TestMaterial")
                .materialDomain(MaterialBuilder.MaterialDomain.SURFACE)
                .shading(MaterialBuilder.Shading.UNLIT)
                .interpolation(MaterialBuilder.Interpolation.SMOOTH)
                .uniformParameter(MaterialBuilder.UniformType.FLOAT, "myUniform")
                .uniformParameter(MaterialBuilder.UniformType.FLOAT2, MaterialBuilder.ParameterPrecision.HIGH, "myPrecise")
                .uniformParameterArray(MaterialBuilder.UniformType.FLOAT4, 4, "myArray")
                .uniformParameterArray(MaterialBuilder.UniformType.FLOAT4, 2, MaterialBuilder.ParameterPrecision.MEDIUM, "myPreciseArray")
                .samplerParameter(MaterialBuilder.SamplerType.SAMPLER_2D, MaterialBuilder.SamplerFormat.FLOAT, MaterialBuilder.ParameterPrecision.DEFAULT, "myTex")
                .variable(MaterialBuilder.Variable.CUSTOM0, "myVar")
                .require(VertexAttribute.POSITION)
                .material("void material(inout MaterialInputs m) { prepareMaterial(m); }")
                .materialVertex("void materialVertex(inout MaterialVertexInputs m) {}")
                .blending(MaterialBuilder.BlendingMode.OPAQUE)
                .postLightingBlending(MaterialBuilder.BlendingMode.OPAQUE)
                .vertexDomain(MaterialBuilder.VertexDomain.OBJECT)
                .culling(MaterialBuilder.CullingMode.BACK)
                .colorWrite(true)
                .depthWrite(true)
                .depthCulling(true)
                .doubleSided(false)
                .maskThreshold(0.5f)
                .alphaToCoverage(false)
                .shadowMultiplier(true)
                .transparentShadow(true)
                .specularAntiAliasing(true)
                .specularAntiAliasingVariance(0.15f)
                .specularAntiAliasingThreshold(0.2f)
                .refractionMode(MaterialBuilder.RefractionMode.NONE)
                .reflectionMode(MaterialBuilder.ReflectionMode.DEFAULT)
                .refractionType(MaterialBuilder.RefractionType.SOLID)
                .clearCoatIorChange(true)
                .flipUV(true)
                .customSurfaceShading(false)
                .multiBounceAmbientOcclusion(true)
                .specularAmbientOcclusion(MaterialBuilder.SpecularAmbientOcclusion.SIMPLE)
                .transparencyMode(MaterialBuilder.TransparencyMode.DEFAULT)
                .platform(MaterialBuilder.Platform.ALL)
                .targetApi(MaterialBuilder.TargetApi.ALL)
                .optimization(MaterialBuilder.Optimization.NONE)
                .variantFilter(0)
                .useLegacyMorphing()
                .build()

            assertNotNull(pkg)
        } catch (e: UnsupportedOperationException) {
            // Expected on platforms where filamat compilation is not supported (e.g. JS).
        }
    }
}
