#include <stddef.h>

#include "filament/Engine.h"
#include "filament/Material.h"
#include "filament/MaterialInstance.h"
#include "filament/Texture.h"
#include "filament/TextureSampler.h"

// Verifies Material builder and instance creation APIs are consumable from C.
void test_headers_material(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaMaterialBuilder* builder = FilaMaterialBuilder_create();
    const unsigned char fakePackage[4] = {0, 0, 0, 0};
    FilaMaterialBuilder_constantInt(builder, "kInt", 1);
    FilaMaterialBuilder_constantFloat(builder, "kFloat", 1.0f);
    FilaMaterialBuilder_constantBool(builder, "kBool", true);
    FilaMaterialBuilder_sphericalHarmonicsBandCount(builder, 3u);
    FilaMaterialBuilder_shadowSamplingQuality(builder, FILA_MATERIAL_BUILDER_SHADOW_SAMPLING_QUALITY_HARD);
    FilaMaterialBuilder_uboBatching(builder, FILA_MATERIAL_UBO_BATCHING_MODE_DEFAULT);
    FilaMaterialBuilder_package(builder, fakePackage, sizeof(fakePackage));
    FilaMaterial* material = FilaMaterialBuilder_build(builder, engine);
    FilaMaterialInstance* materialInstance = FilaMaterial_createInstance(material);
    FilaMaterialInstance* namedInstance = FilaMaterial_createInstanceNamed(material, "instance-0");
    (void)FilaMaterialInstance_getName(materialInstance);
    (void)FilaMaterialInstance_duplicate(materialInstance, "dup-0");
    (void)FilaMaterial_getName(material);
    (void)FilaMaterial_hasParameter(material, "uFloat");
    (void)FilaMaterial_isSampler(material, "uAlbedo");
    (void)FilaMaterial_getParameterCount(material);
    (void)FILA_BACKEND_SUBPASS_TYPE_SUBPASS_INPUT;
    {
        FilaMaterialParameterInfo parameterInfos[2];
        (void)FilaMaterial_getParameters(material, parameterInfos, 2u);
    }
    (void)FilaMaterial_getShading(material);
    (void)FilaMaterial_getInterpolation(material);
    (void)FilaMaterial_getBlendingMode(material);
    (void)FilaMaterial_getVertexDomain(material);
    (void)FilaMaterial_getSupportedVariants(material);
    (void)FilaMaterial_getMaterialDomain(material);
    (void)FilaMaterial_getCullingMode(material);
    (void)FilaMaterial_getTransparencyMode(material);
    (void)FilaMaterial_isColorWriteEnabled(material);
    (void)FilaMaterial_isDepthWriteEnabled(material);
    (void)FilaMaterial_isDepthCullingEnabled(material);
    (void)FilaMaterial_isDoubleSided(material);
    (void)FilaMaterial_isAlphaToCoverageEnabled(material);
    (void)FilaMaterial_getMaskThreshold(material);
    (void)FilaMaterial_hasShadowMultiplier(material);
    (void)FilaMaterial_hasSpecularAntiAliasing(material);
    (void)FilaMaterial_getSpecularAntiAliasingVariance(material);
    (void)FilaMaterial_getSpecularAntiAliasingThreshold(material);
    (void)FilaMaterial_getRequiredAttributes(material);
    (void)FilaMaterial_getRefractionMode(material);
    (void)FilaMaterial_getRefractionType(material);
    (void)FilaMaterial_getReflectionMode(material);
    (void)FilaMaterial_getFeatureLevel(material);
    (void)FilaMaterial_getParameterTransformName(material, "uAlbedo");
    (void)FilaMaterial_getDefaultInstance(material);
    FilaMaterialInstance_setParameterFloat(materialInstance, "uFloat", 1.0f);
    FilaMaterialInstance_setParameterFloat2(materialInstance, "uFloat2", 1.0f, 2.0f);
    FilaMaterialInstance_setParameterFloat3(materialInstance, "uFloat3", 1.0f, 2.0f, 3.0f);
    FilaMaterialInstance_setParameterFloat4(materialInstance, "uFloat4", 1.0f, 2.0f, 3.0f, 4.0f);
    FilaMaterialInstance_setParameterInt(materialInstance, "uInt", -1);
    FilaMaterialInstance_setParameterInt2(materialInstance, "uInt2", -1, 2);
    FilaMaterialInstance_setParameterInt3(materialInstance, "uInt3", -1, 2, -3);
    FilaMaterialInstance_setParameterInt4(materialInstance, "uInt4", -1, 2, -3, 4);
    FilaMaterialInstance_setParameterUint(materialInstance, "uUint", 1u);
    FilaMaterialInstance_setParameterUint2(materialInstance, "uUint2", 1u, 2u);
    FilaMaterialInstance_setParameterUint3(materialInstance, "uUint3", 1u, 2u, 3u);
    FilaMaterialInstance_setParameterUint4(materialInstance, "uUint4", 1u, 2u, 3u, 4u);
    FilaMaterialInstance_setParameterBool(materialInstance, "uBool", true);
    FilaMaterialInstance_setParameterBool2(materialInstance, "uBool2", true, false);
    FilaMaterialInstance_setParameterBool3(materialInstance, "uBool3", true, false, true);
    FilaMaterialInstance_setParameterBool4(materialInstance, "uBool4", true, false, true, false);
    {
        float mat3Value[9] = {0.0f};
        float mat4Value[16] = {0.0f};
        float f2[2] = {0.0f};
        float f3[3] = {0.0f};
        float f4[4] = {0.0f};
        int32_t i2[2] = {0};
        int32_t i3[3] = {0};
        int32_t i4[4] = {0};
        uint32_t u2[2] = {0u};
        uint32_t u3[3] = {0u};
        uint32_t u4[4] = {0u};
        bool b2[2] = {false};
        bool b3[3] = {false};
        bool b4[4] = {false};
        float fx = 0.0f;
        int32_t ix = 0;
        uint32_t ux = 0u;
        bool bx = false;
        FilaMaterialInstance_setParameterMat3f(materialInstance, "uMat3", mat3Value);
        FilaMaterialInstance_setParameterMat4f(materialInstance, "uMat4", mat4Value);
        FilaMaterialInstance_setParameterRgb(materialInstance, "uRgb", FILA_RGB_TYPE_LINEAR, 1.0f, 1.0f, 1.0f);
        FilaMaterialInstance_setParameterRgba(materialInstance, "uRgba", FILA_RGBA_TYPE_LINEAR, 1.0f, 1.0f, 1.0f, 1.0f);
        (void)FilaMaterialInstance_getParameterFloat(materialInstance, "uFloat", &fx);
        (void)FilaMaterialInstance_getParameterFloat2(materialInstance, "uFloat2", f2);
        (void)FilaMaterialInstance_getParameterFloat3(materialInstance, "uFloat3", f3);
        (void)FilaMaterialInstance_getParameterFloat4(materialInstance, "uFloat4", f4);
        (void)FilaMaterialInstance_getParameterInt(materialInstance, "uInt", &ix);
        (void)FilaMaterialInstance_getParameterInt2(materialInstance, "uInt2", i2);
        (void)FilaMaterialInstance_getParameterInt3(materialInstance, "uInt3", i3);
        (void)FilaMaterialInstance_getParameterInt4(materialInstance, "uInt4", i4);
        (void)FilaMaterialInstance_getParameterUint(materialInstance, "uUint", &ux);
        (void)FilaMaterialInstance_getParameterUint2(materialInstance, "uUint2", u2);
        (void)FilaMaterialInstance_getParameterUint3(materialInstance, "uUint3", u3);
        (void)FilaMaterialInstance_getParameterUint4(materialInstance, "uUint4", u4);
        (void)FilaMaterialInstance_getParameterBool(materialInstance, "uBool", &bx);
        (void)FilaMaterialInstance_getParameterBool2(materialInstance, "uBool2", b2);
        (void)FilaMaterialInstance_getParameterBool3(materialInstance, "uBool3", b3);
        (void)FilaMaterialInstance_getParameterBool4(materialInstance, "uBool4", b4);
        (void)FilaMaterialInstance_getParameterMat3f(materialInstance, "uMat3", mat3Value);
        (void)FilaMaterialInstance_getParameterMat4f(materialInstance, "uMat4", mat4Value);
    }
    FilaTextureParams* sampler = FilaTextureParams_create();
    FilaMaterialInstance_setParameterTexture(materialInstance, "uAlbedo", (const FilaTexture*)0, sampler);
    FilaTextureParams_destroy(sampler);

    FilaMaterialInstance_setScissor(materialInstance, 0u, 0u, 128u, 128u);
    FilaMaterialInstance_unsetScissor(materialInstance);
    FilaMaterialInstance_setPolygonOffset(materialInstance, 1.0f, 1.0f);
    FilaMaterialInstance_setMaskThreshold(materialInstance, 0.5f);
    (void)FilaMaterialInstance_getMaskThreshold(materialInstance);
    FilaMaterialInstance_setSpecularAntiAliasingVariance(materialInstance, 0.2f);
    (void)FilaMaterialInstance_getSpecularAntiAliasingVariance(materialInstance);
    FilaMaterialInstance_setSpecularAntiAliasingThreshold(materialInstance, 0.3f);
    (void)FilaMaterialInstance_getSpecularAntiAliasingThreshold(materialInstance);
    FilaMaterialInstance_setDoubleSided(materialInstance, true);
    (void)FilaMaterialInstance_isDoubleSided(materialInstance);
    FilaMaterialInstance_setTransparencyMode(materialInstance, FILA_MATERIAL_TRANSPARENCY_MODE_DEFAULT);
    (void)FilaMaterialInstance_getTransparencyMode(materialInstance);
    FilaMaterialInstance_setCullingMode(materialInstance, FILA_BACKEND_CULLING_MODE_NONE);
    FilaMaterialInstance_setCullingModeSeparate(materialInstance, FILA_BACKEND_CULLING_MODE_FRONT, FILA_BACKEND_CULLING_MODE_BACK);
    (void)FilaMaterialInstance_getCullingMode(materialInstance);
    (void)FilaMaterialInstance_getShadowCullingMode(materialInstance);
    FilaMaterialInstance_setColorWrite(materialInstance, true);
    (void)FilaMaterialInstance_isColorWriteEnabled(materialInstance);
    FilaMaterialInstance_setDepthWrite(materialInstance, true);
    (void)FilaMaterialInstance_isDepthWriteEnabled(materialInstance);
    FilaMaterialInstance_setDepthCulling(materialInstance, true);
    (void)FilaMaterialInstance_isDepthCullingEnabled(materialInstance);
    FilaMaterialInstance_setDepthFunc(materialInstance, FILA_BACKEND_SAMPLER_COMPARE_FUNC_LE);
    (void)FilaMaterialInstance_getDepthFunc(materialInstance);
    FilaMaterialInstance_setStencilWrite(materialInstance, true);
    (void)FilaMaterialInstance_isStencilWriteEnabled(materialInstance);
    FilaMaterialInstance_setStencilCompareFunction(materialInstance, FILA_BACKEND_SAMPLER_COMPARE_FUNC_A, FILA_BACKEND_STENCIL_FACE_FRONT_AND_BACK);
    FilaMaterialInstance_setStencilOpStencilFail(materialInstance, FILA_BACKEND_STENCIL_OPERATION_KEEP, FILA_BACKEND_STENCIL_FACE_FRONT_AND_BACK);
    FilaMaterialInstance_setStencilOpDepthFail(materialInstance, FILA_BACKEND_STENCIL_OPERATION_KEEP, FILA_BACKEND_STENCIL_FACE_FRONT_AND_BACK);
    FilaMaterialInstance_setStencilOpDepthStencilPass(materialInstance, FILA_BACKEND_STENCIL_OPERATION_KEEP, FILA_BACKEND_STENCIL_FACE_FRONT_AND_BACK);
    FilaMaterialInstance_setStencilReferenceValue(materialInstance, 1u, FILA_BACKEND_STENCIL_FACE_FRONT_AND_BACK);
    FilaMaterialInstance_setStencilReadMask(materialInstance, 0xFFu, FILA_BACKEND_STENCIL_FACE_FRONT_AND_BACK);
    FilaMaterialInstance_setStencilWriteMask(materialInstance, 0xFFu, FILA_BACKEND_STENCIL_FACE_FRONT_AND_BACK);
    FilaMaterialInstance_commit(materialInstance, engine);
    FilaMaterialBuilder_destroy(builder);
    FilaEngine_destroyMaterialInstance(engine, namedInstance);
    FilaEngine_destroyMaterialInstance(engine, materialInstance);
    FilaEngine_destroyMaterial(engine, material);
}
