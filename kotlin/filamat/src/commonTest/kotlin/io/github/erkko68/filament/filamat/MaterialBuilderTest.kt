package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Material
import io.github.erkko68.filament.MaterialDomain
import io.github.erkko68.filament.RefractionMode
import io.github.erkko68.filament.RefractionType
import io.github.erkko68.filament.VertexBuffer.VertexAttribute
import io.github.erkko68.filament.filamat.testutils.FilamatTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** What the builder is told must be what Filament reads back from the compiled package. */
class MaterialBuilderTest : FilamatTestFixture() {
    private fun unlit() = MaterialBuilder()
        .shading(MaterialBuilder.Shading.UNLIT)
        .material("void material(inout MaterialInputs m) { prepareMaterial(m); }")

    @Test
    fun litSettingsReachTheMaterial() {
        val source = "material { name : Settings }"
        val material = MaterialBuilder()
            .name("Settings")
            .shading(MaterialBuilder.Shading.LIT)
            .interpolation(MaterialBuilder.Interpolation.FLAT)
            .blending(MaterialBuilder.BlendingMode.MASKED)
            .maskThreshold(0.3f)
            .culling(MaterialBuilder.CullingMode.FRONT)
            .doubleSided(true)
            .colorWrite(false)
            .depthWrite(false)
            .depthCulling(false)
            .alphaToCoverage(true)
            .specularAntiAliasing(true)
            .specularAntiAliasingVariance(0.15f)
            .specularAntiAliasingThreshold(0.2f)
            .refractionMode(MaterialBuilder.RefractionMode.CUBEMAP)
            .refractionType(MaterialBuilder.RefractionType.THIN)
            .vertexDomain(MaterialBuilder.VertexDomain.WORLD)
            .featureLevel(Engine.FeatureLevel.FEATURE_LEVEL_1)
            .require(VertexAttribute.UV0)
            .materialSource(source)
            .material("void material(inout MaterialInputs m) { prepareMaterial(m); }")
            .load()

        assertEquals("Settings", material.name)
        assertEquals(Material.Shading.LIT, material.shading)
        assertEquals(Material.Interpolation.FLAT, material.interpolation)
        assertEquals(Material.BlendingMode.MASKED, material.blendingMode)
        assertEquals(0.3f, material.maskThreshold, 1e-6f)
        assertEquals(Material.CullingMode.FRONT, material.cullingMode)
        assertTrue(material.isDoubleSided)
        assertFalse(material.isColorWriteEnabled)
        assertFalse(material.isDepthWriteEnabled)
        assertFalse(material.isDepthCullingEnabled)
        assertTrue(material.isAlphaToCoverageEnabled)
        assertTrue(material.hasSpecularAntiAliasing)
        assertEquals(0.15f, material.specularAntiAliasingVariance, 1e-6f)
        assertEquals(0.2f, material.specularAntiAliasingThreshold, 1e-6f)
        assertEquals(RefractionMode.CUBEMAP, material.refractionMode)
        assertEquals(RefractionType.THIN, material.refractionType)
        assertEquals(Material.VertexDomain.WORLD, material.vertexDomain)
        assertEquals(Engine.FeatureLevel.FEATURE_LEVEL_1, material.featureLevel)
        assertTrue(VertexAttribute.UV0 in material.requiredAttributes)
        assertEquals(source, material.source)
    }

    @Test
    fun parametersReachTheMaterial() {
        val material = unlit()
            .parameter("scalar", MaterialBuilder.UniformType.FLOAT)
            .parameter("accurate", MaterialBuilder.UniformType.FLOAT2, MaterialBuilder.ParameterPrecision.HIGH)
            .parameter("array", 4, MaterialBuilder.UniformType.FLOAT4)
            .parameter("tex", MaterialBuilder.SamplerType.SAMPLER_2D)
            .parameter("fragmentTex", MaterialBuilder.SamplerType.SAMPLER_CUBEMAP, stages = setOf(MaterialBuilder.ShaderStage.FRAGMENT))
            .load()
        val byName = material.parameters.associateBy { it.name }

        assertEquals(setOf("scalar", "accurate", "array", "tex", "fragmentTex"), byName.keys)
        assertEquals(5, material.parameterCount)
        assertEquals(Material.ParameterType.FLOAT, byName.getValue("scalar").type)
        assertEquals(Material.Precision.HIGH, byName.getValue("accurate").precision)
        assertEquals(4, byName.getValue("array").count)
        assertEquals(Material.ParameterType.FLOAT4, byName.getValue("array").type)
        assertTrue(material.isSampler("tex"))
        assertEquals(Material.SamplerType.SAMPLER_CUBEMAP, byName.getValue("fragmentTex").samplerType)
        assertFalse(material.hasParameter("missing"))
    }

    /** Specialization constants can be overridden by name when the material is loaded (Filament rejects unknown names). */
    @Test
    fun constantsCanBeSpecialized() {
        unlit()
            .constant("count", MaterialBuilder.ConstantType.INT, 3)
            .constant("scale", MaterialBuilder.ConstantType.FLOAT, 0.5f)
            .constant("enabled", MaterialBuilder.ConstantType.BOOL, true)
            .load { constant("count", 7).constant("scale", 2f).constant("enabled", false) }
    }

    @Test
    fun customBlendingAndDomainsReachTheMaterial() {
        val custom = unlit()
            .blending(MaterialBuilder.BlendingMode.CUSTOM)
            .customBlendFunctions(
                MaterialBuilder.BlendFunction.SRC_ALPHA, MaterialBuilder.BlendFunction.ONE,
                MaterialBuilder.BlendFunction.ONE_MINUS_SRC_ALPHA, MaterialBuilder.BlendFunction.ZERO,
            )
            .load()
        val postProcess = MaterialBuilder()
            .materialDomain(MaterialBuilder.MaterialDomain.POST_PROCESS)
            .enableFramebufferFetch()
            .output(
                MaterialBuilder.VariableQualifier.OUT, MaterialBuilder.OutputTarget.COLOR,
                MaterialBuilder.ParameterPrecision.DEFAULT, MaterialBuilder.OutputType.FLOAT4, "color",
            )
            .material("void postProcess(inout PostProcessInputs p) { p.color = vec4(1.0); }")
            .load()

        assertEquals(Material.BlendingMode.CUSTOM, custom.blendingMode)
        assertEquals(MaterialDomain.POST_PROCESS, postProcess.materialDomain)
    }

    @Test
    fun computeMaterialsCompile() {
        MaterialBuilder()
            .materialDomain(MaterialBuilder.MaterialDomain.COMPUTE)
            .groupSize(8, 8, 1)
            .material("void compute() {}")
            .compile()
    }

    /** Compiler options that change how it builds, not what it builds: each still yields a valid package. */
    @Test
    fun compilerOptionsStillCompile() {
        unlit()
            .setApiLevel(1)
            .instanced(true)
            .shadowFarAttenuation(true)
            .vertexDomainDeviceJittered(true)
            .useDefaultDepthVariant()
            .stereoscopicType(Engine.StereoscopicType.INSTANCED)
            .stereoscopicEyeCount(2)
            .workarounds(MaterialBuilder.Workarounds.NONE)
            .noSamplerValidation(true)
            .includeEssl1(false)
            .compilationParameters("")
            .shaderDefine("MY_DEFINE", "1")
            .optimization(MaterialBuilder.Optimization.NONE)
            .generateDebugInfo(true)
            .useLegacyMorphing()
            .compile()
        val apis = MaterialBuilder.TargetApi.entries.associateWith { unlit().targetApi(it).platform(MaterialBuilder.Platform.ALL).build().isValid }
        // WGSL output exists only where the prebuilt filamat links Tint (Linux); elsewhere it is an invalid package.
        // Either way the call returns: on Linux it used to end the process on an unresolved Tint symbol.
        assertEquals(MaterialBuilder.TargetApi.entries.filter { it != MaterialBuilder.TargetApi.WEBGPU }.associateWith { true },
                     apis - MaterialBuilder.TargetApi.WEBGPU)
    }

    @Test
    fun aBrokenShaderBuildsAnInvalidPackage() {
        val pkg = unlit().material("void material(inout MaterialInputs m) { this is not glsl }").build()
        assertFalse(pkg.isValid)
        assertEquals(0, pkg.size)
    }

    /** The compiler works again after shutting down and starting up. */
    @Test
    fun theCompilerSurvivesInitShutdownCycles() {
        MaterialBuilder.init()
        MaterialBuilder.shutdown()
        MaterialBuilder.shutdown()
        MaterialBuilder.init()
        unlit().compile()
    }

    @Test
    fun theAttributeDatabaseDescribesUv0() {
        val uv0 = MaterialBuilder.getAttributeDatabase().single { it.location == VertexAttribute.UV0 }
        assertEquals("uv0", uv0.name)
        assertEquals(MaterialBuilder.UniformType.FLOAT2, uv0.type)
        assertEquals("mesh_uv0", uv0.attributeName)
        assertEquals("HAS_ATTRIBUTE_UV0", uv0.defineName)
    }
}
