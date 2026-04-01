#include "backend/DriverEnums.h"

void backend_driver_enums_test(void) {
    FilaBackendFeatureLevel featureLevel = FILA_BACKEND_FEATURE_LEVEL_3;
    FilaBackendBackend backend = FILA_BACKEND_VULKAN;
    FilaBackendTimerQueryResult timerQueryResult = FILA_BACKEND_TIMER_QUERY_AVAILABLE;
    FilaBackendShaderLanguage shaderLanguage = FILA_BACKEND_SHADER_LANGUAGE_WGSL;
    (void)featureLevel;
    (void)backend;
    (void)timerQueryResult;
    (void)shaderLanguage;

    FilaBackendShaderStage stage = FILA_BACKEND_SHADER_STAGE_VERTEX;
    FilaBackendShaderStageFlags stageFlags =
        FILA_BACKEND_SHADER_STAGE_FLAGS_VERTEX | FILA_BACKEND_SHADER_STAGE_FLAGS_FRAGMENT;
    FilaBackendShaderStageFlags_hasStage(stageFlags, stage);

    FilaBackendTextureType textureType = FILA_BACKEND_TEXTURE_TYPE_DEPTH_STENCIL;
    FilaBackendTextureType_toString(textureType);
    FilaBackendTextureType_toString((FilaBackendTextureType)255);

    FilaBackendTargetBufferFlags flags = FILA_BACKEND_TARGET_BUFFER_ALL;
    (void)flags;

    flags = FilaBackendTargetBufferFlags_at(0);
    flags = FilaBackendTargetBufferFlags_at(8);
    flags = FilaBackendTargetBufferFlags_at(9);
    flags = FilaBackendTargetBufferFlags_at(100);
    (void)flags;

    FilaBackendTextureCubemapFace face = FILA_BACKEND_TEXTURE_CUBEMAP_FACE_NEGATIVE_Z;
    (void)face;

    FilaBackendRenderTargetAttachmentPoint attachment = FILA_BACKEND_RENDER_TARGET_ATTACHMENT_DEPTH;
    attachment = FilaBackendRenderTargetAttachmentPoint_at(2);
    attachment = FilaBackendRenderTargetAttachmentPoint_at(42);
    (void)attachment;

    FilaBackendTextureUsage usage = FILA_BACKEND_TEXTURE_USAGE_DEFAULT;
    usage = FILA_BACKEND_TEXTURE_USAGE_ALL_ATTACHMENTS;
    (void)usage;

    FilaBackendTextureFormat textureFormat = FILA_BACKEND_TEXTURE_FORMAT_RGBA8;
    textureFormat = FILA_BACKEND_TEXTURE_FORMAT_SRGB_ALPHA_BPTC_UNORM;
    FilaBackendTextureFormat_getTextureType(textureFormat);
    FilaBackendTextureFormat_isDepth(FILA_BACKEND_TEXTURE_FORMAT_DEPTH24);
    FilaBackendTextureFormat_isStencil(FILA_BACKEND_TEXTURE_FORMAT_STENCIL8);
    FilaBackendTextureFormat_isColor(FILA_BACKEND_TEXTURE_FORMAT_RGBA8);
    FilaBackendTextureFormat_isUnsignedInt(FILA_BACKEND_TEXTURE_FORMAT_RGBA32UI);
    FilaBackendTextureFormat_isSignedInt(FILA_BACKEND_TEXTURE_FORMAT_RGBA32I);
    FilaBackendTextureFormat_isCompressed(FILA_BACKEND_TEXTURE_FORMAT_EAC_R11);
    FilaBackendTextureFormat_isETC2Compression(FILA_BACKEND_TEXTURE_FORMAT_ETC2_RGB8);
    FilaBackendTextureFormat_isS3TCCompression(FILA_BACKEND_TEXTURE_FORMAT_DXT1_RGB);
    FilaBackendTextureFormat_isS3TCSRGBCompression(FILA_BACKEND_TEXTURE_FORMAT_DXT1_SRGB);
    FilaBackendTextureFormat_isRGTCCompression(FILA_BACKEND_TEXTURE_FORMAT_RED_RGTC1);
    FilaBackendTextureFormat_isBPTCCompression(FILA_BACKEND_TEXTURE_FORMAT_RGB_BPTC_SIGNED_FLOAT);
    FilaBackendTextureFormat_isASTCCompression(FILA_BACKEND_TEXTURE_FORMAT_RGBA_ASTC_4x4);
    (void)textureFormat;

    FilaBackendBufferUsage bufferUsage = FILA_BACKEND_BUFFER_USAGE_SHARED_WRITE_BIT;
    FilaBackendMapBufferAccessFlags mapAccess = FILA_BACKEND_MAP_BUFFER_ACCESS_INVALIDATE_RANGE_BIT;
    FilaBackendFenceStatus fenceStatus = FILA_BACKEND_FENCE_STATUS_TIMEOUT_EXPIRED;
    FilaBackendShaderModel shaderModel = FILA_BACKEND_SHADER_MODEL_DESKTOP;
    (void)bufferUsage;
    (void)mapAccess;
    (void)fenceStatus;
    (void)shaderModel;

    FilaBackendPrimitiveType primitiveType = FILA_BACKEND_PRIMITIVE_TYPE_TRIANGLE_STRIP;
    FilaBackendPrimitiveType_isStrip(primitiveType);

    FilaBackendUniformType uniformType = FILA_BACKEND_UNIFORM_TYPE_STRUCT;
    FilaBackendConstantType constantType = FILA_BACKEND_CONSTANT_TYPE_BOOL;
    FilaBackendPrecision precision = FILA_BACKEND_PRECISION_DEFAULT;
    FilaBackendCompilerPriorityQueue priorityQueue = FILA_BACKEND_COMPILER_PRIORITY_QUEUE_LOW;
    (void)uniformType;
    (void)constantType;
    (void)precision;
    (void)priorityQueue;

    FilaBackendPipelineStageCount();
    FilaBackendShaderModelCount();

    FilaBackendSamplerType samplerType = FILA_BACKEND_SAMPLER_TYPE_CUBEMAP_ARRAY;
    FilaBackendBufferObjectBinding bufferObjectBinding = FILA_BACKEND_BUFFER_OBJECT_BINDING_SHADER_STORAGE;
    FilaBackendSamplerFormat samplerFormat = FILA_BACKEND_SAMPLER_FORMAT_SHADOW;
    FilaBackendSamplerWrapMode wrapMode = FILA_BACKEND_SAMPLER_WRAP_MODE_MIRRORED_REPEAT;
    FilaBackendSamplerMinFilter minFilter = FILA_BACKEND_SAMPLER_MIN_FILTER_LINEAR_MIPMAP_LINEAR;
    FilaBackendSamplerMagFilter magFilter = FILA_BACKEND_SAMPLER_MAG_FILTER_LINEAR;
    FilaBackendSamplerCompareMode compareMode = FILA_BACKEND_SAMPLER_COMPARE_MODE_COMPARE_TO_TEXTURE;
    FilaBackendSamplerCompareFunc compareFunc = FILA_BACKEND_SAMPLER_COMPARE_FUNC_N;
    FilaBackendElementType elementType = FILA_BACKEND_ELEMENT_TYPE_HALF4;
    FilaBackendCullingMode cullingMode = FILA_BACKEND_CULLING_MODE_FRONT_AND_BACK;
    FilaBackendTextureSwizzle swizzle = FILA_BACKEND_TEXTURE_SWIZZLE_CHANNEL_3;
    FilaBackendBlendEquation blendEquation = FILA_BACKEND_BLEND_EQUATION_MAX;
    FilaBackendBlendFunction blendFunction = FILA_BACKEND_BLEND_FUNCTION_SRC_ALPHA_SATURATE;
    FilaBackendStencilOperation stencilOp = FILA_BACKEND_STENCIL_OPERATION_INVERT;
    FilaBackendStencilFace stencilFace = FILA_BACKEND_STENCIL_FACE_FRONT_AND_BACK;
    FilaBackendStreamType streamType = FILA_BACKEND_STREAM_TYPE_ACQUIRED;
    FilaBackendBufferObjectBinding_toString(bufferObjectBinding);
    FilaBackendBufferObjectBinding_toString((FilaBackendBufferObjectBinding)255);
    (void)samplerType;
    (void)bufferObjectBinding;
    (void)samplerFormat;
    (void)wrapMode;
    (void)minFilter;
    (void)magFilter;
    (void)compareMode;
    (void)compareFunc;
    (void)elementType;
    (void)cullingMode;
    (void)swizzle;
    (void)blendEquation;
    (void)blendFunction;
    (void)stencilOp;
    (void)stencilFace;
    (void)streamType;

    FilaBackendDescriptorType descriptorType = FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_CUBE_DEPTH;
    FilaBackendDescriptorFlags descriptorFlags =
        FILA_BACKEND_DESCRIPTOR_FLAGS_DYNAMIC_OFFSET | FILA_BACKEND_DESCRIPTOR_FLAGS_UNFILTERABLE;
    FilaBackendDescriptorSet descriptorSet = 0;
    FilaBackendDescriptorBinding descriptorBinding = 1;
    FilaBackendDescriptorSetLayoutDescriptorData layout = {
        descriptorType,
        FILA_BACKEND_SHADER_STAGE_FLAGS_FRAGMENT,
        descriptorBinding,
        FILA_BACKEND_DESCRIPTOR_FLAGS_NONE,
        1,
    };
    (void)descriptorSet;
    (void)layout;

    FilaBackendDescriptorType_isDepth(descriptorType);
    FilaBackendDescriptorType_isFloat(FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_2D_FLOAT);
    FilaBackendDescriptorType_isInt(FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_2D_INT);
    FilaBackendDescriptorType_isUnsignedInt(FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_2D_UINT);
    FilaBackendDescriptorType_is3dType(FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_3D_FLOAT);
    FilaBackendDescriptorType_is2dType(FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_2D_FLOAT);
    FilaBackendDescriptorType_is2dArrayType(FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_2D_ARRAY_FLOAT);
    FilaBackendDescriptorType_isCubeType(FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_CUBE_FLOAT);
    FilaBackendDescriptorType_isCubeArrayType(FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_CUBE_ARRAY_FLOAT);
    FilaBackendDescriptorType_isMultiSampledType(FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_2D_MS_FLOAT);
    FilaBackendDescriptorType_toString(descriptorType);
    FilaBackendDescriptorType_toString((FilaBackendDescriptorType)255);

    FilaBackendDescriptorSetLayoutDescriptor_isSamplerType(FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_EXTERNAL);
    FilaBackendDescriptorSetLayoutDescriptor_isBufferType(FILA_BACKEND_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
    FilaBackendDescriptorFlags_has(descriptorFlags, FILA_BACKEND_DESCRIPTOR_FLAGS_UNFILTERABLE);
}

