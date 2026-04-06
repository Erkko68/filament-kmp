#ifndef FILAMAT_C_MATERIAL_BUILDER_H
#define FILAMAT_C_MATERIAL_BUILDER_H

#include "../../filament/c/FilaTypes.h"

#ifdef __cplusplus
extern "C" {
#endif

// Matches filamat::MaterialBuilder::MaterialDomain
typedef enum FilaMaterialBuilderMaterialDomain {
    FILA_MATERIAL_BUILDER_MATERIAL_DOMAIN_SURFACE = 0,
    FILA_MATERIAL_BUILDER_MATERIAL_DOMAIN_POST_PROCESS = 1,
} FilaMaterialBuilderMaterialDomain;

// Matches filamat::MaterialBuilder::Shading
typedef enum FilaMaterialBuilderShading {
    FILA_MATERIAL_BUILDER_SHADING_UNLIT = 0,
    FILA_MATERIAL_BUILDER_SHADING_LIT = 1,
    FILA_MATERIAL_BUILDER_SHADING_SUBSURFACE = 2,
    FILA_MATERIAL_BUILDER_SHADING_CLOTH = 3,
} FilaMaterialBuilderShading;

// Matches filamat::MaterialBuilder::Interpolation
typedef enum FilaMaterialBuilderInterpolation {
    FILA_MATERIAL_BUILDER_INTERPOLATION_SMOOTH = 0,
    FILA_MATERIAL_BUILDER_INTERPOLATION_FLAT = 1,
} FilaMaterialBuilderInterpolation;

// Matches filamat::MaterialBuilder::BlendingMode
typedef enum FilaMaterialBuilderBlendingMode {
    FILA_MATERIAL_BUILDER_BLENDING_MODE_OPAQUE = 0,
    FILA_MATERIAL_BUILDER_BLENDING_MODE_TRANSPARENT = 1,
    FILA_MATERIAL_BUILDER_BLENDING_MODE_ADD = 2,
    FILA_MATERIAL_BUILDER_BLENDING_MODE_MASKED = 3,
    FILA_MATERIAL_BUILDER_BLENDING_MODE_FADE = 4,
    FILA_MATERIAL_BUILDER_BLENDING_MODE_MULTIPLY = 5,
    FILA_MATERIAL_BUILDER_BLENDING_MODE_SCREEN = 6,
} FilaMaterialBuilderBlendingMode;

// Matches filamat::MaterialBuilder::VertexDomain
typedef enum FilaMaterialBuilderVertexDomain {
    FILA_MATERIAL_BUILDER_VERTEX_DOMAIN_OBJECT = 0,
    FILA_MATERIAL_BUILDER_VERTEX_DOMAIN_WORLD = 1,
    FILA_MATERIAL_BUILDER_VERTEX_DOMAIN_VIEW = 2,
    FILA_MATERIAL_BUILDER_VERTEX_DOMAIN_DEVICE = 3,
} FilaMaterialBuilderVertexDomain;

// Matches filamat::MaterialBuilder::MaterialCullingMode
typedef enum FilaMaterialBuilderCullingMode {
    FILA_MATERIAL_BUILDER_CULLING_MODE_NONE = 0,
    FILA_MATERIAL_BUILDER_CULLING_MODE_FRONT = 1,
    FILA_MATERIAL_BUILDER_CULLING_MODE_BACK = 2,
    FILA_MATERIAL_BUILDER_CULLING_MODE_FRONT_AND_BACK = 3,
} FilaMaterialBuilderCullingMode;

// Matches filamat::MaterialBuilder::UniformType
typedef enum FilaMaterialBuilderUniformType {
    FILA_MATERIAL_BUILDER_UNIFORM_TYPE_BOOL = 0,
    FILA_MATERIAL_BUILDER_UNIFORM_TYPE_BOOL2 = 1,
    FILA_MATERIAL_BUILDER_UNIFORM_TYPE_BOOL3 = 2,
    FILA_MATERIAL_BUILDER_UNIFORM_TYPE_BOOL4 = 3,
    FILA_MATERIAL_BUILDER_UNIFORM_TYPE_FLOAT = 4,
    FILA_MATERIAL_BUILDER_UNIFORM_TYPE_FLOAT2 = 5,
    FILA_MATERIAL_BUILDER_UNIFORM_TYPE_FLOAT3 = 6,
    FILA_MATERIAL_BUILDER_UNIFORM_TYPE_FLOAT4 = 7,
    FILA_MATERIAL_BUILDER_UNIFORM_TYPE_INT = 8,
    FILA_MATERIAL_BUILDER_UNIFORM_TYPE_INT2 = 9,
    FILA_MATERIAL_BUILDER_UNIFORM_TYPE_INT3 = 10,
    FILA_MATERIAL_BUILDER_UNIFORM_TYPE_INT4 = 11,
    FILA_MATERIAL_BUILDER_UNIFORM_TYPE_UINT = 12,
    FILA_MATERIAL_BUILDER_UNIFORM_TYPE_UINT2 = 13,
    FILA_MATERIAL_BUILDER_UNIFORM_TYPE_UINT3 = 14,
    FILA_MATERIAL_BUILDER_UNIFORM_TYPE_UINT4 = 15,
    FILA_MATERIAL_BUILDER_UNIFORM_TYPE_MAT3 = 16,
    FILA_MATERIAL_BUILDER_UNIFORM_TYPE_MAT4 = 17,
} FilaMaterialBuilderUniformType;

// Matches filamat::MaterialBuilder::SamplerType
typedef enum FilaMaterialBuilderSamplerType {
    FILA_MATERIAL_BUILDER_SAMPLER_TYPE_SAMPLER_2D = 0,
    FILA_MATERIAL_BUILDER_SAMPLER_TYPE_SAMPLER_3D = 1,
    FILA_MATERIAL_BUILDER_SAMPLER_TYPE_SAMPLER_2D_ARRAY = 2,
    FILA_MATERIAL_BUILDER_SAMPLER_TYPE_SAMPLER_CUBE = 3,
    FILA_MATERIAL_BUILDER_SAMPLER_TYPE_SAMPLER_EXTERNAL = 4,
} FilaMaterialBuilderSamplerType;

// Matches filamat::MaterialBuilder::SamplerFormat
typedef enum FilaMaterialBuilderSamplerFormat {
    FILA_MATERIAL_BUILDER_SAMPLER_FORMAT_INT = 0,
    FILA_MATERIAL_BUILDER_SAMPLER_FORMAT_UINT = 1,
    FILA_MATERIAL_BUILDER_SAMPLER_FORMAT_FLOAT = 2,
    FILA_MATERIAL_BUILDER_SAMPLER_FORMAT_SHADOW = 3,
} FilaMaterialBuilderSamplerFormat;

// Matches filamat::MaterialBuilder::ParameterPrecision
typedef enum FilaMaterialBuilderParameterPrecision {
    FILA_MATERIAL_BUILDER_PARAMETER_PRECISION_LOW = 0,
    FILA_MATERIAL_BUILDER_PARAMETER_PRECISION_MEDIUM = 1,
    FILA_MATERIAL_BUILDER_PARAMETER_PRECISION_HIGH = 2,
    FILA_MATERIAL_BUILDER_PARAMETER_PRECISION_DEFAULT = 3,
} FilaMaterialBuilderParameterPrecision;

// Matches filamat::MaterialBuilder::Variable
typedef enum FilaMaterialBuilderVariable {
    FILA_MATERIAL_BUILDER_VARIABLE_CUSTOM0 = 0,
    FILA_MATERIAL_BUILDER_VARIABLE_CUSTOM1 = 1,
    FILA_MATERIAL_BUILDER_VARIABLE_CUSTOM2 = 2,
    FILA_MATERIAL_BUILDER_VARIABLE_CUSTOM3 = 3,
} FilaMaterialBuilderVariable;

// Matches filamat::MaterialBuilder::Platform
typedef enum FilaMaterialBuilderPlatform {
    FILA_MATERIAL_BUILDER_PLATFORM_DESKTOP = 0,
    FILA_MATERIAL_BUILDER_PLATFORM_MOBILE = 1,
    FILA_MATERIAL_BUILDER_PLATFORM_ALL = 2,
} FilaMaterialBuilderPlatform;

// Matches filamat::MaterialBuilder::TargetApi
typedef enum FilaMaterialBuilderTargetApi {
    FILA_MATERIAL_BUILDER_TARGET_API_OPENGL = 0,
    FILA_MATERIAL_BUILDER_TARGET_API_VULKAN = 1,
    FILA_MATERIAL_BUILDER_TARGET_API_METAL = 2,
    FILA_MATERIAL_BUILDER_TARGET_API_ALL = 3,
} FilaMaterialBuilderTargetApi;

// Matches filamat::MaterialBuilder::Optimization
typedef enum FilaMaterialBuilderOptimization {
    FILA_MATERIAL_BUILDER_OPTIMIZATION_NONE = 0,
    FILA_MATERIAL_BUILDER_OPTIMIZATION_SIZE = 1,
    FILA_MATERIAL_BUILDER_OPTIMIZATION_PERFORMANCE = 2,
} FilaMaterialBuilderOptimization;

// Static methods
void FilaMaterialBuilder_init();
void FilaMaterialBuilder_shutdown();

// Builder methods
FilaMaterialBuilder* FilaMaterialBuilder_create();
void FilaMaterialBuilder_destroy(FilaMaterialBuilder* builder);
FilaPackage* FilaMaterialBuilder_build(FilaMaterialBuilder* builder);

void FilaMaterialBuilder_name(FilaMaterialBuilder* builder, const char* name);
void FilaMaterialBuilder_materialDomain(FilaMaterialBuilder* builder, FilaMaterialBuilderMaterialDomain domain);
void FilaMaterialBuilder_shading(FilaMaterialBuilder* builder, FilaMaterialBuilderShading shading);
void FilaMaterialBuilder_interpolation(FilaMaterialBuilder* builder, FilaMaterialBuilderInterpolation interpolation);
void FilaMaterialBuilder_uniformParameter(FilaMaterialBuilder* builder, FilaMaterialBuilderUniformType type, FilaMaterialBuilderParameterPrecision precision, const char* name);
void FilaMaterialBuilder_uniformParameterArray(FilaMaterialBuilder* builder, FilaMaterialBuilderUniformType type, size_t size, FilaMaterialBuilderParameterPrecision precision, const char* name);
void FilaMaterialBuilder_samplerParameter(FilaMaterialBuilder* builder, FilaMaterialBuilderSamplerType type, FilaMaterialBuilderSamplerFormat format, FilaMaterialBuilderParameterPrecision precision, const char* name);
void FilaMaterialBuilder_variable(FilaMaterialBuilder* builder, FilaMaterialBuilderVariable variable, const char* name);
void FilaMaterialBuilder_require(FilaMaterialBuilder* builder, FilaVertexAttribute attribute);
void FilaMaterialBuilder_material(FilaMaterialBuilder* builder, const char* code);
void FilaMaterialBuilder_materialVertex(FilaMaterialBuilder* builder, const char* code);
void FilaMaterialBuilder_blending(FilaMaterialBuilder* builder, FilaMaterialBuilderBlendingMode mode);
void FilaMaterialBuilder_postLightingBlending(FilaMaterialBuilder* builder, FilaMaterialBuilderBlendingMode mode);
void FilaMaterialBuilder_vertexDomain(FilaMaterialBuilder* builder, FilaMaterialBuilderVertexDomain domain);
void FilaMaterialBuilder_culling(FilaMaterialBuilder* builder, FilaMaterialBuilderCullingMode mode);
void FilaMaterialBuilder_colorWrite(FilaMaterialBuilder* builder, bool enable);
void FilaMaterialBuilder_depthWrite(FilaMaterialBuilder* builder, bool enable);
void FilaMaterialBuilder_depthCulling(FilaMaterialBuilder* builder, bool enable);
void FilaMaterialBuilder_doubleSided(FilaMaterialBuilder* builder, bool doubleSided);
void FilaMaterialBuilder_maskThreshold(FilaMaterialBuilder* builder, float threshold);
void FilaMaterialBuilder_alphaToCoverage(FilaMaterialBuilder* builder, bool enable);
void FilaMaterialBuilder_shadowMultiplier(FilaMaterialBuilder* builder, bool enable);
void FilaMaterialBuilder_transparentShadow(FilaMaterialBuilder* builder, bool enable);
void FilaMaterialBuilder_specularAntiAliasing(FilaMaterialBuilder* builder, bool enable);
void FilaMaterialBuilder_specularAntiAliasingVariance(FilaMaterialBuilder* builder, float variance);
void FilaMaterialBuilder_specularAntiAliasingThreshold(FilaMaterialBuilder* builder, float threshold);
void FilaMaterialBuilder_clearCoatIorChange(FilaMaterialBuilder* builder, bool enable);
void FilaMaterialBuilder_flipUV(FilaMaterialBuilder* builder, bool enable);
void FilaMaterialBuilder_customSurfaceShading(FilaMaterialBuilder* builder, bool enable);
void FilaMaterialBuilder_multiBounceAmbientOcclusion(FilaMaterialBuilder* builder, bool enable);
void FilaMaterialBuilder_specularAmbientOcclusion(FilaMaterialBuilder* builder, int32_t mode);
void FilaMaterialBuilder_refractionMode(FilaMaterialBuilder* builder, int32_t mode);
void FilaMaterialBuilder_reflectionMode(FilaMaterialBuilder* builder, int32_t mode);
void FilaMaterialBuilder_refractionType(FilaMaterialBuilder* builder, int32_t type);
void FilaMaterialBuilder_transparencyMode(FilaMaterialBuilder* builder, int32_t mode);
void FilaMaterialBuilder_platform(FilaMaterialBuilder* builder, FilaMaterialBuilderPlatform platform);
void FilaMaterialBuilder_targetApi(FilaMaterialBuilder* builder, FilaMaterialBuilderTargetApi targetApi);
void FilaMaterialBuilder_optimization(FilaMaterialBuilder* builder, FilaMaterialBuilderOptimization optimization);
void FilaMaterialBuilder_variantFilter(FilaMaterialBuilder* builder, uint8_t variantFilter);
void FilaMaterialBuilder_useLegacyMorphing(FilaMaterialBuilder* builder);

// Package methods
void FilaPackage_destroy(FilaPackage* package);
bool FilaPackage_isValid(const FilaPackage* package);
const void* FilaPackage_getData(const FilaPackage* package);
size_t FilaPackage_getSize(const FilaPackage* package);

#ifdef __cplusplus
}
#endif

#endif // FILAMAT_C_MATERIAL_BUILDER_H
