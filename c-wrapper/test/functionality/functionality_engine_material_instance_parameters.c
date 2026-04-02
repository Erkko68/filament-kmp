#include <stdio.h>

#include "filament/Engine.h"
#include "filament/Material.h"
#include "filament/MaterialInstance.h"
#include "filament/Texture.h"
#include "filament/TextureSampler.h"

int main(void) {
    printf("Running engine+material_instance_parameters functionality program...\n");

    FilaEngine* engine = FilaEngine_create();
    if (!engine) {
        printf("Engine creation failed\n");
        return 1;
    }

    {
        FilaMaterialBuilder* builder = FilaMaterialBuilder_create();
        FilaMaterialBuilder_constantInt(builder, "kInt", 1);
        FilaMaterialBuilder_constantFloat(builder, "kFloat", 1.0f);
        FilaMaterialBuilder_constantBool(builder, "kBool", true);
        FilaMaterialBuilder_sphericalHarmonicsBandCount(builder, 3u);
        FilaMaterialBuilder_shadowSamplingQuality(builder, FILA_MATERIAL_BUILDER_SHADOW_SAMPLING_QUALITY_LOW);
        FilaMaterialBuilder_uboBatching(builder, FILA_MATERIAL_UBO_BATCHING_MODE_DISABLED);
        FilaMaterialBuilder_destroy(builder);

        FilaMaterialBuilder_constantInt((FilaMaterialBuilder*)0, "kInt", 1);
        FilaMaterialBuilder_constantFloat((FilaMaterialBuilder*)0, "kFloat", 1.0f);
        FilaMaterialBuilder_constantBool((FilaMaterialBuilder*)0, "kBool", true);
        FilaMaterialBuilder_sphericalHarmonicsBandCount((FilaMaterialBuilder*)0, 3u);
        FilaMaterialBuilder_shadowSamplingQuality((FilaMaterialBuilder*)0, FILA_MATERIAL_BUILDER_SHADOW_SAMPLING_QUALITY_HARD);
        FilaMaterialBuilder_uboBatching((FilaMaterialBuilder*)0, FILA_MATERIAL_UBO_BATCHING_MODE_DEFAULT);
    }

    // Exercise null guards for parameter setters; this is a safe runtime path even
    // without an embedded valid material package.
    FilaMaterialInstance_setParameterFloat((FilaMaterialInstance*)0, "uFloat", 1.0f);
    FilaMaterialInstance_setParameterFloat2((FilaMaterialInstance*)0, "uFloat2", 1.0f, 2.0f);
    FilaMaterialInstance_setParameterFloat3((FilaMaterialInstance*)0, "uFloat3", 1.0f, 2.0f, 3.0f);
    FilaMaterialInstance_setParameterFloat4((FilaMaterialInstance*)0, "uFloat4", 1.0f, 2.0f, 3.0f, 4.0f);
    FilaMaterialInstance_setParameterInt((FilaMaterialInstance*)0, "uInt", -7);
    FilaMaterialInstance_setParameterInt2((FilaMaterialInstance*)0, "uInt2", -7, 8);
    FilaMaterialInstance_setParameterInt3((FilaMaterialInstance*)0, "uInt3", -7, 8, -9);
    FilaMaterialInstance_setParameterInt4((FilaMaterialInstance*)0, "uInt4", -7, 8, -9, 10);
    FilaMaterialInstance_setParameterUint((FilaMaterialInstance*)0, "uUint", 7u);
    FilaMaterialInstance_setParameterUint2((FilaMaterialInstance*)0, "uUint2", 7u, 8u);
    FilaMaterialInstance_setParameterUint3((FilaMaterialInstance*)0, "uUint3", 7u, 8u, 9u);
    FilaMaterialInstance_setParameterUint4((FilaMaterialInstance*)0, "uUint4", 7u, 8u, 9u, 10u);
    FilaMaterialInstance_setParameterBool((FilaMaterialInstance*)0, "uBool", true);
    FilaMaterialInstance_setParameterBool2((FilaMaterialInstance*)0, "uBool2", true, false);
    FilaMaterialInstance_setParameterBool3((FilaMaterialInstance*)0, "uBool3", true, false, true);
    FilaMaterialInstance_setParameterBool4((FilaMaterialInstance*)0, "uBool4", true, false, true, false);
    {
        float m3[9] = {0.0f};
        float m4[16] = {0.0f};
        FilaMaterialInstance_setParameterMat3f((FilaMaterialInstance*)0, "uMat3", m3);
        FilaMaterialInstance_setParameterMat4f((FilaMaterialInstance*)0, "uMat4", m4);
    }
    FilaMaterialInstance_setParameterRgb((FilaMaterialInstance*)0, "uRgb", FILA_RGB_TYPE_LINEAR, 1.0f, 0.5f, 0.25f);
    FilaMaterialInstance_setParameterRgba((FilaMaterialInstance*)0, "uRgba", FILA_RGBA_TYPE_LINEAR, 1.0f, 0.5f, 0.25f, 1.0f);

    FilaTextureParams* sampler = FilaTextureParams_create();
    FilaMaterialInstance_setParameterTexture(
        (FilaMaterialInstance*)0,
        "uSampler",
        (const FilaTexture*)0,
        sampler);
    FilaTextureParams_destroy(sampler);

    FilaMaterialInstance_setScissor((FilaMaterialInstance*)0, 0u, 0u, 64u, 64u);
    FilaMaterialInstance_unsetScissor((FilaMaterialInstance*)0);
    FilaMaterialInstance_setPolygonOffset((FilaMaterialInstance*)0, 1.0f, 1.0f);
    FilaMaterialInstance_setMaskThreshold((FilaMaterialInstance*)0, 0.5f);
    FilaMaterialInstance_setSpecularAntiAliasingVariance((FilaMaterialInstance*)0, 0.2f);
    FilaMaterialInstance_setSpecularAntiAliasingThreshold((FilaMaterialInstance*)0, 0.3f);
    FilaMaterialInstance_setDoubleSided((FilaMaterialInstance*)0, true);
    FilaMaterialInstance_setTransparencyMode((FilaMaterialInstance*)0, FILA_MATERIAL_TRANSPARENCY_MODE_DEFAULT);
    FilaMaterialInstance_setCullingMode((FilaMaterialInstance*)0, FILA_BACKEND_CULLING_MODE_NONE);
    FilaMaterialInstance_setCullingModeSeparate((FilaMaterialInstance*)0, FILA_BACKEND_CULLING_MODE_FRONT, FILA_BACKEND_CULLING_MODE_BACK);
    FilaMaterialInstance_setColorWrite((FilaMaterialInstance*)0, true);
    FilaMaterialInstance_setDepthWrite((FilaMaterialInstance*)0, true);
    FilaMaterialInstance_setDepthCulling((FilaMaterialInstance*)0, true);
    FilaMaterialInstance_setDepthFunc((FilaMaterialInstance*)0, FILA_BACKEND_SAMPLER_COMPARE_FUNC_LE);
    FilaMaterialInstance_setStencilWrite((FilaMaterialInstance*)0, true);
    FilaMaterialInstance_setStencilCompareFunction((FilaMaterialInstance*)0, FILA_BACKEND_SAMPLER_COMPARE_FUNC_A, FILA_BACKEND_STENCIL_FACE_FRONT_AND_BACK);
    FilaMaterialInstance_setStencilOpStencilFail((FilaMaterialInstance*)0, FILA_BACKEND_STENCIL_OPERATION_KEEP, FILA_BACKEND_STENCIL_FACE_FRONT_AND_BACK);
    FilaMaterialInstance_setStencilOpDepthFail((FilaMaterialInstance*)0, FILA_BACKEND_STENCIL_OPERATION_KEEP, FILA_BACKEND_STENCIL_FACE_FRONT_AND_BACK);
    FilaMaterialInstance_setStencilOpDepthStencilPass((FilaMaterialInstance*)0, FILA_BACKEND_STENCIL_OPERATION_KEEP, FILA_BACKEND_STENCIL_FACE_FRONT_AND_BACK);
    FilaMaterialInstance_setStencilReferenceValue((FilaMaterialInstance*)0, 1u, FILA_BACKEND_STENCIL_FACE_FRONT_AND_BACK);
    FilaMaterialInstance_setStencilReadMask((FilaMaterialInstance*)0, 0xFFu, FILA_BACKEND_STENCIL_FACE_FRONT_AND_BACK);
    FilaMaterialInstance_setStencilWriteMask((FilaMaterialInstance*)0, 0xFFu, FILA_BACKEND_STENCIL_FACE_FRONT_AND_BACK);
    FilaMaterialInstance_commit((const FilaMaterialInstance*)0, engine);

    if (FilaMaterialInstance_getMaskThreshold((const FilaMaterialInstance*)0) != 0.0f ||
            FilaMaterialInstance_getSpecularAntiAliasingVariance((const FilaMaterialInstance*)0) != 0.0f ||
            FilaMaterialInstance_getSpecularAntiAliasingThreshold((const FilaMaterialInstance*)0) != 0.0f ||
            FilaMaterialInstance_isDoubleSided((const FilaMaterialInstance*)0) ||
            FilaMaterialInstance_getTransparencyMode((const FilaMaterialInstance*)0) != FILA_MATERIAL_TRANSPARENCY_MODE_DEFAULT ||
            FilaMaterialInstance_getCullingMode((const FilaMaterialInstance*)0) != FILA_BACKEND_CULLING_MODE_NONE ||
            FilaMaterialInstance_getShadowCullingMode((const FilaMaterialInstance*)0) != FILA_BACKEND_CULLING_MODE_NONE ||
            FilaMaterialInstance_isColorWriteEnabled((const FilaMaterialInstance*)0) ||
            FilaMaterialInstance_isDepthWriteEnabled((const FilaMaterialInstance*)0) ||
            FilaMaterialInstance_isDepthCullingEnabled((const FilaMaterialInstance*)0) ||
            FilaMaterialInstance_getDepthFunc((const FilaMaterialInstance*)0) != FILA_BACKEND_SAMPLER_COMPARE_FUNC_LE ||
            FilaMaterialInstance_isStencilWriteEnabled((const FilaMaterialInstance*)0)) {
        printf("MaterialInstance state null-safety mismatch\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    (void)FilaMaterial_createInstanceNamed((const FilaMaterial*)0, "named");
    (void)FilaMaterialInstance_duplicate((const FilaMaterialInstance*)0, "dup");
    (void)FilaMaterialInstance_getName((const FilaMaterialInstance*)0);
    (void)FilaMaterial_getName((const FilaMaterial*)0);
    {
        size_t sourceLength = 17u;
        if (FilaMaterial_getSource((const FilaMaterial*)0, &sourceLength) != (const char*)0 || sourceLength != 0u) {
            printf("Material source null-safety mismatch\n");
            FilaEngine_destroy(&engine);
            return 1;
        }
    }
    (void)FilaMaterial_hasParameter((const FilaMaterial*)0, "uSampler");
    (void)FilaMaterial_isSampler((const FilaMaterial*)0, "uSampler");
    (void)FilaMaterial_getParameterCount((const FilaMaterial*)0);
    (void)FilaMaterial_getShading((const FilaMaterial*)0);
    (void)FilaMaterial_getInterpolation((const FilaMaterial*)0);
    (void)FilaMaterial_getBlendingMode((const FilaMaterial*)0);
    (void)FilaMaterial_getVertexDomain((const FilaMaterial*)0);
    (void)FilaMaterial_getSupportedVariants((const FilaMaterial*)0);
    (void)FilaMaterial_getMaterialDomain((const FilaMaterial*)0);
    (void)FilaMaterial_getCullingMode((const FilaMaterial*)0);
    (void)FilaMaterial_getTransparencyMode((const FilaMaterial*)0);
    (void)FilaMaterial_isColorWriteEnabled((const FilaMaterial*)0);
    (void)FilaMaterial_isDepthWriteEnabled((const FilaMaterial*)0);
    (void)FilaMaterial_isDepthCullingEnabled((const FilaMaterial*)0);
    (void)FilaMaterial_isDoubleSided((const FilaMaterial*)0);
    (void)FilaMaterial_isAlphaToCoverageEnabled((const FilaMaterial*)0);
    (void)FilaMaterial_getMaskThreshold((const FilaMaterial*)0);
    (void)FilaMaterial_hasShadowMultiplier((const FilaMaterial*)0);
    (void)FilaMaterial_hasSpecularAntiAliasing((const FilaMaterial*)0);
    (void)FilaMaterial_getSpecularAntiAliasingVariance((const FilaMaterial*)0);
    (void)FilaMaterial_getSpecularAntiAliasingThreshold((const FilaMaterial*)0);
    (void)FilaMaterial_getRequiredAttributes((const FilaMaterial*)0);
    (void)FilaMaterial_getRefractionMode((const FilaMaterial*)0);
    (void)FilaMaterial_getRefractionType((const FilaMaterial*)0);
    (void)FilaMaterial_getReflectionMode((const FilaMaterial*)0);
    (void)FilaMaterial_getFeatureLevel((const FilaMaterial*)0);
    (void)FilaMaterial_getParameterTransformName((const FilaMaterial*)0, "uSampler");
    (void)FilaMaterial_getDefaultInstance((FilaMaterial*)0);

    {
        float fx = 0.0f;
        float f2[2] = {0.0f};
        float f3[3] = {0.0f};
        float f4[4] = {0.0f};
        int32_t ix = 0;
        int32_t i2[2] = {0};
        int32_t i3[3] = {0};
        int32_t i4[4] = {0};
        uint32_t ux = 0u;
        uint32_t u2[2] = {0u};
        uint32_t u3[3] = {0u};
        uint32_t u4[4] = {0u};
        bool bx = false;
        bool b2[2] = {false};
        bool b3[3] = {false};
        bool b4[4] = {false};
        float m3[9] = {0.0f};
        float m4[16] = {0.0f};

        if (FilaMaterialInstance_getParameterFloat((const FilaMaterialInstance*)0, "uFloat", &fx) ||
                FilaMaterialInstance_getParameterFloat2((const FilaMaterialInstance*)0, "uFloat2", f2) ||
                FilaMaterialInstance_getParameterFloat3((const FilaMaterialInstance*)0, "uFloat3", f3) ||
                FilaMaterialInstance_getParameterFloat4((const FilaMaterialInstance*)0, "uFloat4", f4) ||
                FilaMaterialInstance_getParameterInt((const FilaMaterialInstance*)0, "uInt", &ix) ||
                FilaMaterialInstance_getParameterInt2((const FilaMaterialInstance*)0, "uInt2", i2) ||
                FilaMaterialInstance_getParameterInt3((const FilaMaterialInstance*)0, "uInt3", i3) ||
                FilaMaterialInstance_getParameterInt4((const FilaMaterialInstance*)0, "uInt4", i4) ||
                FilaMaterialInstance_getParameterUint((const FilaMaterialInstance*)0, "uUint", &ux) ||
                FilaMaterialInstance_getParameterUint2((const FilaMaterialInstance*)0, "uUint2", u2) ||
                FilaMaterialInstance_getParameterUint3((const FilaMaterialInstance*)0, "uUint3", u3) ||
                FilaMaterialInstance_getParameterUint4((const FilaMaterialInstance*)0, "uUint4", u4) ||
                FilaMaterialInstance_getParameterBool((const FilaMaterialInstance*)0, "uBool", &bx) ||
                FilaMaterialInstance_getParameterBool2((const FilaMaterialInstance*)0, "uBool2", b2) ||
                FilaMaterialInstance_getParameterBool3((const FilaMaterialInstance*)0, "uBool3", b3) ||
                FilaMaterialInstance_getParameterBool4((const FilaMaterialInstance*)0, "uBool4", b4) ||
                FilaMaterialInstance_getParameterMat3f((const FilaMaterialInstance*)0, "uMat3", m3) ||
                FilaMaterialInstance_getParameterMat4f((const FilaMaterialInstance*)0, "uMat4", m4)) {
            printf("MaterialInstance getParameter null-safety mismatch\n");
            FilaEngine_destroy(&engine);
            return 1;
        }
    }

    // Also verify getMaterial null handling remains stable.
    if (FilaMaterialInstance_getMaterial((FilaMaterialInstance*)0) != (const FilaMaterial*)0) {
        printf("MaterialInstance null material query mismatch\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    {
        const FilaMaterial* defaultMaterial = FilaEngine_getDefaultMaterial(engine);
        if (!defaultMaterial) {
            printf("Default material query failed\n");
            FilaEngine_destroy(&engine);
            return 1;
        }

        {
            size_t sourceLength = 0u;
            const char* source = FilaMaterial_getSource(defaultMaterial, &sourceLength);
            if (!source && sourceLength != 0u) {
                printf("Default material source metadata mismatch\n");
                FilaEngine_destroy(&engine);
                return 1;
            }
        }

        FilaMaterialInstance* defaultInstance = FilaMaterial_getDefaultInstance((FilaMaterial*)defaultMaterial);
        if (!defaultInstance) {
            printf("Default material instance query failed\n");
            FilaEngine_destroy(&engine);
            return 1;
        }

        if (FilaMaterialInstance_getMaterial(defaultInstance) != defaultMaterial) {
            printf("Default instance parent material mismatch\n");
            FilaEngine_destroy(&engine);
            return 1;
        }

        if (FilaMaterialInstance_getName(defaultInstance) == (const char*)0) {
            printf("Default instance name query failed\n");
            FilaEngine_destroy(&engine);
            return 1;
        }

        {
            FilaMaterialInstance* duplicated = FilaMaterialInstance_duplicate(defaultInstance, "default-dup");
            if (!duplicated) {
                printf("Default instance duplication failed\n");
                FilaEngine_destroy(&engine);
                return 1;
            }
            if (FilaMaterialInstance_getMaterial(duplicated) != defaultMaterial) {
                printf("Duplicated instance parent material mismatch\n");
                FilaEngine_destroy(&engine);
                return 1;
            }
            FilaEngine_destroyMaterialInstance(engine, duplicated);
        }
    }

    FilaEngine_destroy(&engine);

    printf("Engine+material_instance_parameters functionality program completed\n");
    return 0;
}
