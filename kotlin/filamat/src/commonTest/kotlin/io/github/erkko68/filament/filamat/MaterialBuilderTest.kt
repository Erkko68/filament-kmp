package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.VertexBuffer.VertexAttribute
import io.github.erkko68.filament.filamat.testutils.FilamatTestFixture
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

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
    fun testTargetApiBranches() {
        // Exercise every TargetApi -> native bitmask branch.
        for (api in MaterialBuilder.TargetApi.entries) {
            val builder = MaterialBuilder().targetApi(api)
            assertNotNull(builder)
        }
    }

    @Test
    fun testMaterialBuilderChainingAndBuild() {
        val pkg = MaterialBuilder()
                .name("TestMaterial")
                .materialDomain(MaterialBuilder.MaterialDomain.SURFACE)
                .shading(MaterialBuilder.Shading.UNLIT)
                .interpolation(MaterialBuilder.Interpolation.SMOOTH)
                .parameter("myUniform", MaterialBuilder.UniformType.FLOAT)
                .parameter("myPrecise", MaterialBuilder.UniformType.FLOAT2, MaterialBuilder.ParameterPrecision.HIGH)
                .parameter("myArray", 4, MaterialBuilder.UniformType.FLOAT4)
                .parameter("myPreciseArray", 2, MaterialBuilder.UniformType.FLOAT4, MaterialBuilder.ParameterPrecision.MEDIUM)
                .parameter("myTex", MaterialBuilder.SamplerType.SAMPLER_2D)
                .parameter("myFragmentTex", MaterialBuilder.SamplerType.SAMPLER_2D, stages = setOf(MaterialBuilder.ShaderStage.FRAGMENT))
                .constant("myIntConstant", MaterialBuilder.ConstantType.INT, 3)
                .constant("myFloatConstant", MaterialBuilder.ConstantType.FLOAT, 0.5f)
                .constant("myBoolConstant", MaterialBuilder.ConstantType.BOOL, true)
                .variable(MaterialBuilder.Variable.CUSTOM0, "myVar")
                .variable(MaterialBuilder.Variable.CUSTOM1, "myPreciseVar", MaterialBuilder.ParameterPrecision.HIGH)
                .shaderDefine("MY_DEFINE", "1")
                .quality(MaterialBuilder.ShaderQuality.HIGH)
                .linearFog(false)
                .materialSource("material { name : TestMaterial }")
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
                .coloredPenumbra(true)
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

        // Package.invalidPackage() is non-null, so assertNotNull alone passes even when
        // MaterialBuilder::init() never ran and the compile was skipped.
        assertNotNull(pkg)
        assertTrue(pkg.isValid, "material did not compile — was MaterialBuilder.init() called?")
        assertTrue(pkg.data.isNotEmpty(), "valid package with no data")
    }

    @Test
    fun testBrokenShaderBuildsInvalidPackage() {
        val pkg = MaterialBuilder()
            .name("Broken")
            .shading(MaterialBuilder.Shading.UNLIT)
            .material("void material(inout MaterialInputs m) { this is not glsl }")
            .build()
        assertFalse(pkg.isValid, "a shader that doesn't compile must give an invalid package")
    }

    @Test
    fun testAttributeDatabase() {
        val attributes = MaterialBuilder.getAttributeDatabase()
        val uv0 = attributes.single { it.location == VertexAttribute.UV0 }
        assertEquals("uv0", uv0.name)
        assertEquals(MaterialBuilder.UniformType.FLOAT2, uv0.type)
        assertEquals("mesh_uv0", uv0.attributeName)
        assertEquals("HAS_ATTRIBUTE_UV0", uv0.defineName)
    }
}
