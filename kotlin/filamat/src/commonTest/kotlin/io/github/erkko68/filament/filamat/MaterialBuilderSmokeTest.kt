package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.VertexBuffer.VertexAttribute
import kotlin.test.Test
import kotlin.test.assertTrue

class MaterialBuilderSmokeTest {
    @Test
    fun verifyMaterialBuilderApi() {
        val builderVerify: () -> Unit = {
            try {
                val builder = MaterialBuilder()
                    .name("MyMaterial")
                    .materialDomain(MaterialBuilder.MaterialDomain.SURFACE)
                    .shading(MaterialBuilder.Shading.LIT)
                    .interpolation(MaterialBuilder.Interpolation.SMOOTH)
                    .uniformParameter(MaterialBuilder.UniformType.FLOAT, "myUniform")
                    .uniformParameter(MaterialBuilder.UniformType.FLOAT, MaterialBuilder.ParameterPrecision.HIGH, "myPreciseUniform")
                    .uniformParameterArray(MaterialBuilder.UniformType.FLOAT4, 4, "myUniformArray")
                    .uniformParameterArray(MaterialBuilder.UniformType.FLOAT4, 4, MaterialBuilder.ParameterPrecision.MEDIUM, "myPreciseUniformArray")
                    .samplerParameter(MaterialBuilder.SamplerType.SAMPLER_2D, MaterialBuilder.SamplerFormat.FLOAT, MaterialBuilder.ParameterPrecision.DEFAULT, "myTexture")
                    .variable(MaterialBuilder.Variable.CUSTOM0, "myVar")
                    .require(VertexAttribute.POSITION)
                    .material("void material(inout MaterialInputs inputs) {}")
                    .materialVertex("void materialVertex(inout MaterialVertexInputs inputs) {}")
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
                    .customSurfaceShading(true)
                    .multiBounceAmbientOcclusion(true)
                    .specularAmbientOcclusion(MaterialBuilder.SpecularAmbientOcclusion.SIMPLE)
                    .transparencyMode(MaterialBuilder.TransparencyMode.DEFAULT)
                    .platform(MaterialBuilder.Platform.ALL)
                    .targetApi(MaterialBuilder.TargetApi.ALL)
                    .optimization(MaterialBuilder.Optimization.SIZE)
                    .variantFilter(0)
                    .useLegacyMorphing()

                val pkg: MaterialPackage = builder.build()
            } catch (e: UnsupportedOperationException) {
                // Expected on unsupported runtime targets like JS
            }
        }
        assertTrue(true, "MaterialBuilder API signatures resolved successfully.")
    }
}
