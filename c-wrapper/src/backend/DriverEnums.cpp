#include "../../include/backend/DriverEnums.h"

#include <backend/DriverEnums.h>

namespace {
static_assert((uint32_t)filament::backend::TargetBufferFlags::COLOR0 == FILA_BACKEND_TARGET_BUFFER_COLOR0,
    "FilaBackendTargetBufferFlags must stay aligned with filament::backend::TargetBufferFlags");
static_assert((uint32_t)filament::backend::TargetBufferFlags::ALL == FILA_BACKEND_TARGET_BUFFER_ALL,
    "FilaBackendTargetBufferFlags must stay aligned with filament::backend::TargetBufferFlags");
static_assert((int)filament::backend::FeatureLevel::FEATURE_LEVEL_3 == FILA_BACKEND_FEATURE_LEVEL_3,
    "FilaBackendFeatureLevel must stay aligned with filament::backend::FeatureLevel");
static_assert((int)filament::backend::Backend::NOOP == FILA_BACKEND_NOOP,
    "FilaBackendBackend must stay aligned with filament::backend::Backend");
static_assert((int)filament::backend::TimerQueryResult::ERROR == FILA_BACKEND_TIMER_QUERY_ERROR,
    "FilaBackendTimerQueryResult must stay aligned with filament::backend::TimerQueryResult");
static_assert((int)filament::backend::ShaderLanguage::UNSPECIFIED == FILA_BACKEND_SHADER_LANGUAGE_UNSPECIFIED,
    "FilaBackendShaderLanguage must stay aligned with filament::backend::ShaderLanguage");
static_assert((uint8_t)filament::backend::ShaderStageFlags::ALL_SHADER_STAGE_FLAGS ==
        FILA_BACKEND_SHADER_STAGE_FLAGS_ALL,
    "FilaBackendShaderStageFlags must stay aligned with filament::backend::ShaderStageFlags");
static_assert((int)filament::backend::TextureType::DEPTH_STENCIL == FILA_BACKEND_TEXTURE_TYPE_DEPTH_STENCIL,
    "FilaBackendTextureType must stay aligned with filament::backend::TextureType");
static_assert((uint8_t)filament::backend::DescriptorType::INPUT_ATTACHMENT ==
        FILA_BACKEND_DESCRIPTOR_TYPE_INPUT_ATTACHMENT,
    "FilaBackendDescriptorType must stay aligned with filament::backend::DescriptorType");
static_assert((uint8_t)filament::backend::DescriptorFlags::UNFILTERABLE ==
        FILA_BACKEND_DESCRIPTOR_FLAGS_UNFILTERABLE,
    "FilaBackendDescriptorFlags must stay aligned with filament::backend::DescriptorFlags");
static_assert((int)filament::backend::TextureCubemapFace::NEGATIVE_Z ==
        FILA_BACKEND_TEXTURE_CUBEMAP_FACE_NEGATIVE_Z,
    "FilaBackendTextureCubemapFace must stay aligned with filament::backend::TextureCubemapFace");
static_assert((uint16_t)filament::backend::TextureUsage::DEFAULT == FILA_BACKEND_TEXTURE_USAGE_DEFAULT,
    "FilaBackendTextureUsage must stay aligned with filament::backend::TextureUsage");
static_assert((uint16_t)filament::backend::TextureFormat::SRGB_ALPHA_BPTC_UNORM ==
        FILA_BACKEND_TEXTURE_FORMAT_SRGB_ALPHA_BPTC_UNORM,
    "FilaBackendTextureFormat must stay aligned with filament::backend::TextureFormat");
static_assert((uint8_t)filament::backend::BufferUsage::SHARED_WRITE_BIT ==
        FILA_BACKEND_BUFFER_USAGE_SHARED_WRITE_BIT,
    "FilaBackendBufferUsage must stay aligned with filament::backend::BufferUsage");
static_assert((uint8_t)filament::backend::MapBufferAccessFlags::INVALIDATE_RANGE_BIT ==
        FILA_BACKEND_MAP_BUFFER_ACCESS_INVALIDATE_RANGE_BIT,
    "FilaBackendMapBufferAccessFlags must stay aligned with filament::backend::MapBufferAccessFlags");
static_assert((int)filament::backend::FenceStatus::ERROR == FILA_BACKEND_FENCE_STATUS_ERROR,
    "FilaBackendFenceStatus must stay aligned with filament::backend::FenceStatus");
static_assert((int)filament::backend::ShaderModel::DESKTOP == FILA_BACKEND_SHADER_MODEL_DESKTOP,
    "FilaBackendShaderModel must stay aligned with filament::backend::ShaderModel");
static_assert((int)filament::backend::PrimitiveType::TRIANGLE_STRIP == FILA_BACKEND_PRIMITIVE_TYPE_TRIANGLE_STRIP,
    "FilaBackendPrimitiveType must stay aligned with filament::backend::PrimitiveType");
static_assert((int)filament::backend::UniformType::STRUCT == FILA_BACKEND_UNIFORM_TYPE_STRUCT,
    "FilaBackendUniformType must stay aligned with filament::backend::UniformType");
static_assert((int)filament::backend::ConstantType::BOOL == FILA_BACKEND_CONSTANT_TYPE_BOOL,
    "FilaBackendConstantType must stay aligned with filament::backend::ConstantType");
static_assert((int)filament::backend::Precision::DEFAULT == FILA_BACKEND_PRECISION_DEFAULT,
    "FilaBackendPrecision must stay aligned with filament::backend::Precision");
static_assert((int)filament::backend::CompilerPriorityQueue::LOW == FILA_BACKEND_COMPILER_PRIORITY_QUEUE_LOW,
    "FilaBackendCompilerPriorityQueue must stay aligned with filament::backend::CompilerPriorityQueue");
static_assert((int)filament::backend::SamplerType::SAMPLER_CUBEMAP_ARRAY ==
        FILA_BACKEND_SAMPLER_TYPE_CUBEMAP_ARRAY,
    "FilaBackendSamplerType must stay aligned with filament::backend::SamplerType");
static_assert((int)filament::backend::BufferObjectBinding::SHADER_STORAGE ==
        FILA_BACKEND_BUFFER_OBJECT_BINDING_SHADER_STORAGE,
    "FilaBackendBufferObjectBinding must stay aligned with filament::backend::BufferObjectBinding");
static_assert((int)filament::backend::SamplerFormat::SHADOW == FILA_BACKEND_SAMPLER_FORMAT_SHADOW,
    "FilaBackendSamplerFormat must stay aligned with filament::backend::SamplerFormat");
static_assert((int)filament::backend::SamplerWrapMode::MIRRORED_REPEAT ==
        FILA_BACKEND_SAMPLER_WRAP_MODE_MIRRORED_REPEAT,
    "FilaBackendSamplerWrapMode must stay aligned with filament::backend::SamplerWrapMode");
static_assert((int)filament::backend::SamplerMinFilter::LINEAR_MIPMAP_LINEAR ==
        FILA_BACKEND_SAMPLER_MIN_FILTER_LINEAR_MIPMAP_LINEAR,
    "FilaBackendSamplerMinFilter must stay aligned with filament::backend::SamplerMinFilter");
static_assert((int)filament::backend::SamplerMagFilter::LINEAR == FILA_BACKEND_SAMPLER_MAG_FILTER_LINEAR,
    "FilaBackendSamplerMagFilter must stay aligned with filament::backend::SamplerMagFilter");
static_assert((int)filament::backend::SamplerCompareMode::COMPARE_TO_TEXTURE ==
        FILA_BACKEND_SAMPLER_COMPARE_MODE_COMPARE_TO_TEXTURE,
    "FilaBackendSamplerCompareMode must stay aligned with filament::backend::SamplerCompareMode");
static_assert((int)filament::backend::SamplerCompareFunc::N == FILA_BACKEND_SAMPLER_COMPARE_FUNC_N,
    "FilaBackendSamplerCompareFunc must stay aligned with filament::backend::SamplerCompareFunc");
static_assert((int)filament::backend::ElementType::HALF4 == FILA_BACKEND_ELEMENT_TYPE_HALF4,
    "FilaBackendElementType must stay aligned with filament::backend::ElementType");
static_assert((int)filament::backend::CullingMode::FRONT_AND_BACK == FILA_BACKEND_CULLING_MODE_FRONT_AND_BACK,
    "FilaBackendCullingMode must stay aligned with filament::backend::CullingMode");
static_assert((int)filament::backend::TextureSwizzle::CHANNEL_3 == FILA_BACKEND_TEXTURE_SWIZZLE_CHANNEL_3,
    "FilaBackendTextureSwizzle must stay aligned with filament::backend::TextureSwizzle");
static_assert((int)filament::backend::BlendEquation::MAX == FILA_BACKEND_BLEND_EQUATION_MAX,
    "FilaBackendBlendEquation must stay aligned with filament::backend::BlendEquation");
static_assert((int)filament::backend::BlendFunction::SRC_ALPHA_SATURATE ==
        FILA_BACKEND_BLEND_FUNCTION_SRC_ALPHA_SATURATE,
    "FilaBackendBlendFunction must stay aligned with filament::backend::BlendFunction");
static_assert((int)filament::backend::StencilOperation::INVERT == FILA_BACKEND_STENCIL_OPERATION_INVERT,
    "FilaBackendStencilOperation must stay aligned with filament::backend::StencilOperation");
static_assert((int)filament::backend::StencilFace::FRONT_AND_BACK == FILA_BACKEND_STENCIL_FACE_FRONT_AND_BACK,
    "FilaBackendStencilFace must stay aligned with filament::backend::StencilFace");
static_assert((int)filament::backend::StreamType::ACQUIRED == FILA_BACKEND_STREAM_TYPE_ACQUIRED,
    "FilaBackendStreamType must stay aligned with filament::backend::StreamType");
} // namespace

extern "C" {

FilaBackendTargetBufferFlags FilaBackendTargetBufferFlags_at(size_t index) {
    return (FilaBackendTargetBufferFlags)filament::backend::getTargetBufferFlagsAt(index);
}

FilaBackendRenderTargetAttachmentPoint FilaBackendRenderTargetAttachmentPoint_at(size_t index) {
    if (index <= 8u) {
        return (FilaBackendRenderTargetAttachmentPoint)index;
    }
    return FILA_BACKEND_RENDER_TARGET_ATTACHMENT_COLOR0;
}

bool FilaBackendPrimitiveType_isStrip(FilaBackendPrimitiveType type) {
    return filament::backend::isStripPrimitiveType(static_cast<filament::backend::PrimitiveType>(type));
}

bool FilaBackendShaderStageFlags_hasStage(FilaBackendShaderStageFlags flags, FilaBackendShaderStage stage) {
    return filament::backend::hasShaderType(
        static_cast<filament::backend::ShaderStageFlags>(flags),
        static_cast<filament::backend::ShaderStage>(stage));
}

const char* FilaBackendTextureType_toString(FilaBackendTextureType type) {
    switch (type) {
        case FILA_BACKEND_TEXTURE_TYPE_FLOAT: return "FLOAT";
        case FILA_BACKEND_TEXTURE_TYPE_INT: return "INT";
        case FILA_BACKEND_TEXTURE_TYPE_UINT: return "UINT";
        case FILA_BACKEND_TEXTURE_TYPE_DEPTH: return "DEPTH";
        case FILA_BACKEND_TEXTURE_TYPE_STENCIL: return "STENCIL";
        case FILA_BACKEND_TEXTURE_TYPE_DEPTH_STENCIL: return "DEPTH_STENCIL";
    }
    return "UNKNOWN";
}

const char* FilaBackendBufferObjectBinding_toString(FilaBackendBufferObjectBinding binding) {
    switch (binding) {
        case FILA_BACKEND_BUFFER_OBJECT_BINDING_VERTEX: return "VERTEX";
        case FILA_BACKEND_BUFFER_OBJECT_BINDING_UNIFORM: return "UNIFORM";
        case FILA_BACKEND_BUFFER_OBJECT_BINDING_SHADER_STORAGE: return "SHADER_STORAGE";
    }
    return "UNKNOWN";
}

FilaBackendTextureType FilaBackendTextureFormat_getTextureType(FilaBackendTextureFormat format) {
    return (FilaBackendTextureType)filament::backend::getTextureType(
        static_cast<filament::backend::TextureFormat>(format));
}

bool FilaBackendTextureFormat_isDepth(FilaBackendTextureFormat format) {
    return filament::backend::isDepthFormat(static_cast<filament::backend::TextureFormat>(format));
}

bool FilaBackendTextureFormat_isStencil(FilaBackendTextureFormat format) {
    return filament::backend::isStencilFormat(static_cast<filament::backend::TextureFormat>(format));
}

bool FilaBackendTextureFormat_isColor(FilaBackendTextureFormat format) {
    return filament::backend::isColorFormat(static_cast<filament::backend::TextureFormat>(format));
}

bool FilaBackendTextureFormat_isUnsignedInt(FilaBackendTextureFormat format) {
    return filament::backend::isUnsignedIntFormat(static_cast<filament::backend::TextureFormat>(format));
}

bool FilaBackendTextureFormat_isSignedInt(FilaBackendTextureFormat format) {
    return filament::backend::isSignedIntFormat(static_cast<filament::backend::TextureFormat>(format));
}

bool FilaBackendTextureFormat_isCompressed(FilaBackendTextureFormat format) {
    return filament::backend::isCompressedFormat(static_cast<filament::backend::TextureFormat>(format));
}

bool FilaBackendTextureFormat_isETC2Compression(FilaBackendTextureFormat format) {
    return filament::backend::isETC2Compression(static_cast<filament::backend::TextureFormat>(format));
}

bool FilaBackendTextureFormat_isS3TCCompression(FilaBackendTextureFormat format) {
    return filament::backend::isS3TCCompression(static_cast<filament::backend::TextureFormat>(format));
}

bool FilaBackendTextureFormat_isS3TCSRGBCompression(FilaBackendTextureFormat format) {
    return filament::backend::isS3TCSRGBCompression(static_cast<filament::backend::TextureFormat>(format));
}

bool FilaBackendTextureFormat_isRGTCCompression(FilaBackendTextureFormat format) {
    return filament::backend::isRGTCCompression(static_cast<filament::backend::TextureFormat>(format));
}

bool FilaBackendTextureFormat_isBPTCCompression(FilaBackendTextureFormat format) {
    return filament::backend::isBPTCCompression(static_cast<filament::backend::TextureFormat>(format));
}

bool FilaBackendTextureFormat_isASTCCompression(FilaBackendTextureFormat format) {
    return filament::backend::isASTCCompression(static_cast<filament::backend::TextureFormat>(format));
}

size_t FilaBackendPipelineStageCount(void) {
    return filament::backend::PIPELINE_STAGE_COUNT;
}

size_t FilaBackendShaderModelCount(void) {
    return filament::backend::SHADER_MODEL_COUNT;
}

bool FilaBackendDescriptorType_isDepth(FilaBackendDescriptorType type) {
    return filament::backend::isDepthDescriptor(static_cast<filament::backend::DescriptorType>(type));
}

bool FilaBackendDescriptorType_isFloat(FilaBackendDescriptorType type) {
    return filament::backend::isFloatDescriptor(static_cast<filament::backend::DescriptorType>(type));
}

bool FilaBackendDescriptorType_isInt(FilaBackendDescriptorType type) {
    return filament::backend::isIntDescriptor(static_cast<filament::backend::DescriptorType>(type));
}

bool FilaBackendDescriptorType_isUnsignedInt(FilaBackendDescriptorType type) {
    return filament::backend::isUnsignedIntDescriptor(static_cast<filament::backend::DescriptorType>(type));
}

bool FilaBackendDescriptorType_is3dType(FilaBackendDescriptorType type) {
    return filament::backend::is3dTypeDescriptor(static_cast<filament::backend::DescriptorType>(type));
}

bool FilaBackendDescriptorType_is2dType(FilaBackendDescriptorType type) {
    return filament::backend::is2dTypeDescriptor(static_cast<filament::backend::DescriptorType>(type));
}

bool FilaBackendDescriptorType_is2dArrayType(FilaBackendDescriptorType type) {
    return filament::backend::is2dArrayTypeDescriptor(static_cast<filament::backend::DescriptorType>(type));
}

bool FilaBackendDescriptorType_isCubeType(FilaBackendDescriptorType type) {
    return filament::backend::isCubeTypeDescriptor(static_cast<filament::backend::DescriptorType>(type));
}

bool FilaBackendDescriptorType_isCubeArrayType(FilaBackendDescriptorType type) {
    return filament::backend::isCubeArrayTypeDescriptor(static_cast<filament::backend::DescriptorType>(type));
}

bool FilaBackendDescriptorType_isMultiSampledType(FilaBackendDescriptorType type) {
    return filament::backend::isMultiSampledTypeDescriptor(static_cast<filament::backend::DescriptorType>(type));
}

const char* FilaBackendDescriptorType_toString(FilaBackendDescriptorType type) {
    switch (type) {
        case FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_2D_FLOAT: return "SAMPLER_2D_FLOAT";
        case FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_2D_INT: return "SAMPLER_2D_INT";
        case FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_2D_UINT: return "SAMPLER_2D_UINT";
        case FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_2D_DEPTH: return "SAMPLER_2D_DEPTH";
        case FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_2D_ARRAY_FLOAT: return "SAMPLER_2D_ARRAY_FLOAT";
        case FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_2D_ARRAY_INT: return "SAMPLER_2D_ARRAY_INT";
        case FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_2D_ARRAY_UINT: return "SAMPLER_2D_ARRAY_UINT";
        case FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_2D_ARRAY_DEPTH: return "SAMPLER_2D_ARRAY_DEPTH";
        case FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_CUBE_FLOAT: return "SAMPLER_CUBE_FLOAT";
        case FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_CUBE_INT: return "SAMPLER_CUBE_INT";
        case FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_CUBE_UINT: return "SAMPLER_CUBE_UINT";
        case FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_CUBE_DEPTH: return "SAMPLER_CUBE_DEPTH";
        case FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_CUBE_ARRAY_FLOAT: return "SAMPLER_CUBE_ARRAY_FLOAT";
        case FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_CUBE_ARRAY_INT: return "SAMPLER_CUBE_ARRAY_INT";
        case FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_CUBE_ARRAY_UINT: return "SAMPLER_CUBE_ARRAY_UINT";
        case FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_CUBE_ARRAY_DEPTH: return "SAMPLER_CUBE_ARRAY_DEPTH";
        case FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_3D_FLOAT: return "SAMPLER_3D_FLOAT";
        case FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_3D_INT: return "SAMPLER_3D_INT";
        case FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_3D_UINT: return "SAMPLER_3D_UINT";
        case FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_2D_MS_FLOAT: return "SAMPLER_2D_MS_FLOAT";
        case FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_2D_MS_INT: return "SAMPLER_2D_MS_INT";
        case FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_2D_MS_UINT: return "SAMPLER_2D_MS_UINT";
        case FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_2D_MS_ARRAY_FLOAT: return "SAMPLER_2D_MS_ARRAY_FLOAT";
        case FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_2D_MS_ARRAY_INT: return "SAMPLER_2D_MS_ARRAY_INT";
        case FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_2D_MS_ARRAY_UINT: return "SAMPLER_2D_MS_ARRAY_UINT";
        case FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_EXTERNAL: return "SAMPLER_EXTERNAL";
        case FILA_BACKEND_DESCRIPTOR_TYPE_UNIFORM_BUFFER: return "UNIFORM_BUFFER";
        case FILA_BACKEND_DESCRIPTOR_TYPE_SHADER_STORAGE_BUFFER: return "SHADER_STORAGE_BUFFER";
        case FILA_BACKEND_DESCRIPTOR_TYPE_INPUT_ATTACHMENT: return "INPUT_ATTACHMENT";
    }
    return "UNKNOWN";
}

bool FilaBackendDescriptorSetLayoutDescriptor_isSamplerType(FilaBackendDescriptorType type) {
    return filament::backend::DescriptorSetLayoutDescriptor::isSampler(
        static_cast<filament::backend::DescriptorType>(type));
}

bool FilaBackendDescriptorSetLayoutDescriptor_isBufferType(FilaBackendDescriptorType type) {
    return filament::backend::DescriptorSetLayoutDescriptor::isBuffer(
        static_cast<filament::backend::DescriptorType>(type));
}

bool FilaBackendDescriptorFlags_has(FilaBackendDescriptorFlags flags, FilaBackendDescriptorFlags bit) {
    return ((uint8_t)flags & (uint8_t)bit) != 0;
}

}

