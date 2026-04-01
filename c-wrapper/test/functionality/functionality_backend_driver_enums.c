#include <stdio.h>
#include <string.h>

#include "backend/DriverEnums.h"

int main(void) {
    printf("Running backend_driver_enums functionality program...\n");

    if (FilaBackendSwapChainConfig_transparent() == 0u ||
            FilaBackendSwapChainConfig_readable() == 0u ||
            FilaBackendSwapChainConfig_srgbColorSpace() == 0u) {
        printf("SwapChain config constants unexpectedly zero\n");
        return 1;
    }
    if (FilaBackendSwapChainConfig_hasStencilBuffer() !=
            FilaBackendSwapChainHasStencilBufferAlias()) {
        printf("SwapChain stencil alias mismatch\n");
        return 1;
    }

    if (strcmp(FilaBackendBackend_toString(FILA_BACKEND_VULKAN), "Vulkan") != 0 ||
            strcmp(FilaBackendBackend_toString((FilaBackendBackend)255), "UNKNOWN") != 0 ||
            strcmp(FilaBackendShaderLanguage_toString(FILA_BACKEND_SHADER_LANGUAGE_WGSL), "WGSL") != 0 ||
            strcmp(FilaBackendShaderLanguage_toString((FilaBackendShaderLanguage)255), "UNKNOWN") != 0 ||
            strcmp(FilaBackendShaderModel_toString(FILA_BACKEND_SHADER_MODEL_DESKTOP), "desktop") != 0 ||
            strcmp(FilaBackendShaderModel_toString((FilaBackendShaderModel)255), "UNKNOWN") != 0 ||
            strcmp(FilaBackendSamplerType_toString(FILA_BACKEND_SAMPLER_TYPE_CUBEMAP), "SAMPLER_CUBEMAP") != 0 ||
            strcmp(FilaBackendSamplerType_toString((FilaBackendSamplerType)255), "UNKNOWN") != 0 ||
            strcmp(FilaBackendSamplerFormat_toString(FILA_BACKEND_SAMPLER_FORMAT_FLOAT), "FLOAT") != 0 ||
            strcmp(FilaBackendSamplerFormat_toString((FilaBackendSamplerFormat)255), "UNKNOWN") != 0) {
        printf("DriverEnums string helper mismatch\n");
        return 1;
    }

    if (FilaBackendLimits_getMaxVertexAttributeCount() == 0u ||
            FilaBackendLimits_getMaxSamplerCount() == 0u ||
            FilaBackendLimits_getMaxVertexBufferCount() == 0u ||
            FilaBackendLimits_getMaxDescriptorCount() == 0u ||
            FilaBackendLimits_getMaxPushConstantCount() == 0u) {
        printf("Backend limits unexpectedly zero\n");
        return 1;
    }

    if (FilaBackendLimits_getConfigUniformBindingCount() == 0u ||
            FilaBackendLimits_getConfigSamplerBindingCount() == 0u) {
        printf("Backend binding limits unexpectedly zero\n");
        return 1;
    }

    if (FilaBackendLimits_getExternalSamplerDataIndexUnused() != 0xFFu) {
        printf("External sampler unused sentinel mismatch\n");
        return 1;
    }

    size_t vertexCaps = 0u;
    size_t fragmentCaps = 0u;
    if (!FilaBackendFeatureLevel_getCaps(FILA_BACKEND_FEATURE_LEVEL_1, &vertexCaps, &fragmentCaps)) {
        printf("Feature level caps query failed for level 1\n");
        return 1;
    }
    if (vertexCaps == 0u || fragmentCaps == 0u) {
        printf("Feature level 1 caps unexpectedly zero\n");
        return 1;
    }

    if (FilaBackendFeatureLevel_getCaps((FilaBackendFeatureLevel)9, &vertexCaps, &fragmentCaps)) {
        printf("Feature level caps unexpectedly succeeded for invalid level\n");
        return 1;
    }
    if (FilaBackendFeatureLevel_getCaps(FILA_BACKEND_FEATURE_LEVEL_2, NULL, &fragmentCaps)) {
        printf("Feature level caps unexpectedly succeeded with null output\n");
        return 1;
    }

    if (FilaBackendFence_getWaitForEverTimeout() == 0u) {
        printf("Fence forever timeout unexpectedly zero\n");
        return 1;
    }

    if (!FilaBackendTargetBufferFlags_has(FILA_BACKEND_TARGET_BUFFER_ALL,
                FILA_BACKEND_TARGET_BUFFER_DEPTH) ||
            FilaBackendTargetBufferFlags_has(FILA_BACKEND_TARGET_BUFFER_COLOR0,
                FILA_BACKEND_TARGET_BUFFER_STENCIL) ||
            !FilaBackendTextureUsage_has(FILA_BACKEND_TEXTURE_USAGE_DEFAULT,
                FILA_BACKEND_TEXTURE_USAGE_SAMPLEABLE) ||
            FilaBackendTextureUsage_has(FILA_BACKEND_TEXTURE_USAGE_COLOR_ATTACHMENT,
                FILA_BACKEND_TEXTURE_USAGE_SAMPLEABLE) ||
            !FilaBackendStencilFace_has(FILA_BACKEND_STENCIL_FACE_FRONT_AND_BACK,
                FILA_BACKEND_STENCIL_FACE_BACK) ||
            FilaBackendStencilFace_has(FILA_BACKEND_STENCIL_FACE_FRONT,
                FILA_BACKEND_STENCIL_FACE_BACK) ||
            !FilaBackendBufferUsage_has(FILA_BACKEND_BUFFER_USAGE_SHARED_WRITE_BIT,
                FILA_BACKEND_BUFFER_USAGE_SHARED_WRITE_BIT) ||
            FilaBackendBufferUsage_has(FILA_BACKEND_BUFFER_USAGE_STATIC,
                FILA_BACKEND_BUFFER_USAGE_DYNAMIC_BIT) ||
            !FilaBackendMapBufferAccessFlags_has(
                FILA_BACKEND_MAP_BUFFER_ACCESS_WRITE_BIT |
                    FILA_BACKEND_MAP_BUFFER_ACCESS_INVALIDATE_RANGE_BIT,
                FILA_BACKEND_MAP_BUFFER_ACCESS_INVALIDATE_RANGE_BIT) ||
            FilaBackendMapBufferAccessFlags_has(FILA_BACKEND_MAP_BUFFER_ACCESS_WRITE_BIT,
                FILA_BACKEND_MAP_BUFFER_ACCESS_INVALIDATE_RANGE_BIT)) {
        printf("DriverEnums bitmask helper mismatch\n");
        return 1;
    }

    if (FilaBackendAsyncCallId_getInvalid() != 0xFFFFFFFFu) {
        printf("AsyncCallId invalid sentinel mismatch\n");
        return 1;
    }

    FilaBackendAttributeData attribute;
    FilaBackendAttribute_setDefaults(&attribute);
    if (attribute.offset != 0u ||
            attribute.stride != 0u ||
            attribute.buffer != FILA_BACKEND_ATTRIBUTE_BUFFER_UNUSED ||
            attribute.type != FILA_BACKEND_ELEMENT_TYPE_BYTE ||
            attribute.flags != 0u) {
        printf("Attribute defaults mismatch\n");
        return 1;
    }
    attribute.flags = FILA_BACKEND_ATTRIBUTE_FLAG_NORMALIZED | FILA_BACKEND_ATTRIBUTE_FLAG_INTEGER_TARGET;
    if (!FilaBackendAttribute_hasFlag(&attribute, FILA_BACKEND_ATTRIBUTE_FLAG_NORMALIZED) ||
            !FilaBackendAttribute_hasFlag(&attribute, FILA_BACKEND_ATTRIBUTE_FLAG_INTEGER_TARGET) ||
            FilaBackendAttribute_hasFlag(&attribute, FILA_BACKEND_ATTRIBUTE_BUFFER_UNUSED) ||
            FilaBackendAttribute_hasFlag((const FilaBackendAttributeData*)0,
                FILA_BACKEND_ATTRIBUTE_FLAG_NORMALIZED)) {
        printf("Attribute flag helper mismatch\n");
        return 1;
    }

    printf("backend_driver_enums functionality program completed\n");
    return 0;
}

