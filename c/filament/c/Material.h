#ifndef FILAMENT_C_MATERIAL_H
#define FILAMENT_C_MATERIAL_H

#include "FilaTypes.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef enum FilaMaterialShading {
    FILA_MATERIAL_SHADING_UNLIT = 0,
    FILA_MATERIAL_SHADING_LIT = 1,
    FILA_MATERIAL_SHADING_CLOTH = 2,
    FILA_MATERIAL_SHADING_SUBSURFACE = 3,
    FILA_MATERIAL_SHADING_SPECULAR_GLOSSINESS = 4,
} FilaMaterialShading;

typedef enum FilaMaterialInterpolation {
    FILA_MATERIAL_INTERPOLATION_SMOOTH = 0,
    FILA_MATERIAL_INTERPOLATION_FLAT = 1,
} FilaMaterialInterpolation;

typedef enum FilaMaterialBlendingMode {
    FILA_MATERIAL_BLENDING_MODE_OPAQUE = 0,
    FILA_MATERIAL_BLENDING_MODE_TRANSPARENT = 1,
    FILA_MATERIAL_BLENDING_MODE_ADD = 2,
    FILA_MATERIAL_BLENDING_MODE_MASKED = 3,
    FILA_MATERIAL_BLENDING_MODE_FADE = 4,
    FILA_MATERIAL_BLENDING_MODE_MULTIPLY = 5,
    FILA_MATERIAL_BLENDING_MODE_SCREEN = 6,
} FilaMaterialBlendingMode;

typedef enum FilaMaterialTransparencyMode {
    FILA_MATERIAL_TRANSPARENCY_MODE_DEFAULT = 0,
    FILA_MATERIAL_TRANSPARENCY_MODE_TWO_PASSES_ONE_SIDE = 1,
    FILA_MATERIAL_TRANSPARENCY_MODE_TWO_PASSES_TWO_SIDES = 2,
} FilaMaterialTransparencyMode;

typedef enum FilaMaterialCullingMode {
    FILA_MATERIAL_CULLING_MODE_NONE = 0,
    FILA_MATERIAL_CULLING_MODE_FRONT = 1,
    FILA_MATERIAL_CULLING_MODE_BACK = 2,
    FILA_MATERIAL_CULLING_MODE_FRONT_AND_BACK = 3,
} FilaMaterialCullingMode;

typedef enum FilaMaterialVertexDomain {
    FILA_MATERIAL_VERTEX_DOMAIN_OBJECT = 0,
    FILA_MATERIAL_VERTEX_DOMAIN_WORLD = 1,
    FILA_MATERIAL_VERTEX_DOMAIN_VIEW = 2,
    FILA_MATERIAL_VERTEX_DOMAIN_DEVICE = 3,
} FilaMaterialVertexDomain;

typedef enum FilaMaterialProperty {
    FILA_MATERIAL_PROPERTY_BASE_COLOR = 0,
    FILA_MATERIAL_PROPERTY_ROUGHNESS = 1,
    FILA_MATERIAL_PROPERTY_METALLIC = 2,
    FILA_MATERIAL_PROPERTY_REFLECTANCE = 3,
    FILA_MATERIAL_PROPERTY_AMBIENT_OCCLUSION = 4,
    FILA_MATERIAL_PROPERTY_EMISSIVE = 5,
    FILA_MATERIAL_PROPERTY_CLEAR_COAT = 6,
    FILA_MATERIAL_PROPERTY_CLEAR_COAT_ROUGHNESS = 7,
    FILA_MATERIAL_PROPERTY_CLEAR_COAT_NORMAL = 8,
    FILA_MATERIAL_PROPERTY_ANISOTROPY = 9,
    FILA_MATERIAL_PROPERTY_ANISOTROPY_DIRECTION = 10,
    FILA_MATERIAL_PROPERTY_THICKNESS = 11,
    FILA_MATERIAL_PROPERTY_SUBSURFACE_POWER = 12,
    FILA_MATERIAL_PROPERTY_SUBSURFACE_COLOR = 13,
    FILA_MATERIAL_PROPERTY_SHEEN_COLOR = 14,
    FILA_MATERIAL_PROPERTY_SHEEN_ROUGHNESS = 15,
    FILA_MATERIAL_PROPERTY_GLOSSINESS = 16,
    FILA_MATERIAL_PROPERTY_SPECULAR_COLOR = 17,
    FILA_MATERIAL_PROPERTY_NORMAL = 18,
    FILA_MATERIAL_PROPERTY_BENT_NORMAL = 19,
    FILA_MATERIAL_PROPERTY_CLEAR_COAT_BENT_NORMAL = 20,
    FILA_MATERIAL_PROPERTY_IOR = 21,
    FILA_MATERIAL_PROPERTY_TRANSMISSION = 22,
    FILA_MATERIAL_PROPERTY_ABSORPTION = 23,
    FILA_MATERIAL_PROPERTY_MICRO_THICKNESS = 24,
} FilaMaterialProperty;

typedef enum FilaMaterialParameterType {
    FILA_MATERIAL_PARAMETER_TYPE_BOOL = 0,
    FILA_MATERIAL_PARAMETER_TYPE_BOOL2 = 1,
    FILA_MATERIAL_PARAMETER_TYPE_BOOL3 = 2,
    FILA_MATERIAL_PARAMETER_TYPE_BOOL4 = 3,
    FILA_MATERIAL_PARAMETER_TYPE_FLOAT = 4,
    FILA_MATERIAL_PARAMETER_TYPE_FLOAT2 = 5,
    FILA_MATERIAL_PARAMETER_TYPE_FLOAT3 = 6,
    FILA_MATERIAL_PARAMETER_TYPE_FLOAT4 = 7,
    FILA_MATERIAL_PARAMETER_TYPE_INT = 8,
    FILA_MATERIAL_PARAMETER_TYPE_INT2 = 9,
    FILA_MATERIAL_PARAMETER_TYPE_INT3 = 10,
    FILA_MATERIAL_PARAMETER_TYPE_INT4 = 11,
    FILA_MATERIAL_PARAMETER_TYPE_UINT = 12,
    FILA_MATERIAL_PARAMETER_TYPE_UINT2 = 13,
    FILA_MATERIAL_PARAMETER_TYPE_UINT3 = 14,
    FILA_MATERIAL_PARAMETER_TYPE_UINT4 = 15,
    FILA_MATERIAL_PARAMETER_TYPE_MAT3 = 16,
    FILA_MATERIAL_PARAMETER_TYPE_MAT4 = 17,
    FILA_MATERIAL_PARAMETER_TYPE_SAMPLER_2D = 100,
    FILA_MATERIAL_PARAMETER_TYPE_SAMPLER_2D_ARRAY = 101,
    FILA_MATERIAL_PARAMETER_TYPE_SAMPLER_CUBEMAP = 102,
    FILA_MATERIAL_PARAMETER_TYPE_SAMPLER_EXTERNAL = 103,
    FILA_MATERIAL_PARAMETER_TYPE_SAMPLER_3D = 104,
    FILA_MATERIAL_PARAMETER_TYPE_SUBPASS_INPUT = 200,
} FilaMaterialParameterType;

typedef enum FilaMaterialPrecision {
    FILA_MATERIAL_PRECISION_LOW = 0,
    FILA_MATERIAL_PRECISION_MEDIUM = 1,
    FILA_MATERIAL_PRECISION_HIGH = 2,
    FILA_MATERIAL_PRECISION_DEFAULT = 3,
} FilaMaterialPrecision;

typedef struct FilaMaterialParameterInfo {
    const char* name;
    FilaMaterialParameterType type;
    FilaMaterialPrecision precision;
    uint32_t count;
} FilaMaterialParameterInfo;

typedef void (*FilaMaterialCompileCallback)(FilaMaterial* material, void* userData);

// Builder
FilaMaterial* FilaMaterial_Builder_build(FilaEngine* engine, const void* payload, size_t size);

// Material
FilaMaterialInstance* FilaMaterial_getDefaultInstance(const FilaMaterial* material);
FilaMaterialInstance* FilaMaterial_createInstance(FilaMaterial* material);
FilaMaterialInstance* FilaMaterial_createInstanceWithName(FilaMaterial* material, const char* name);

const char* FilaMaterial_getName(const FilaMaterial* material);
FilaMaterialShading FilaMaterial_getShading(const FilaMaterial* material);
FilaMaterialInterpolation FilaMaterial_getInterpolation(const FilaMaterial* material);
FilaMaterialBlendingMode FilaMaterial_getBlendingMode(const FilaMaterial* material);
FilaMaterialTransparencyMode FilaMaterial_getTransparencyMode(const FilaMaterial* material);
int FilaMaterial_getRefractionMode(const FilaMaterial* material);
int FilaMaterial_getRefractionType(const FilaMaterial* material);
int FilaMaterial_getReflectionMode(const FilaMaterial* material);
int FilaMaterial_getFeatureLevel(const FilaMaterial* material);
FilaMaterialVertexDomain FilaMaterial_getVertexDomain(const FilaMaterial* material);
FilaMaterialCullingMode FilaMaterial_getCullingMode(const FilaMaterial* material);

bool FilaMaterial_isColorWriteEnabled(const FilaMaterial* material);
bool FilaMaterial_isDepthWriteEnabled(const FilaMaterial* material);
bool FilaMaterial_isDepthCullingEnabled(const FilaMaterial* material);
bool FilaMaterial_isDoubleSided(const FilaMaterial* material);
bool FilaMaterial_isAlphaToCoverageEnabled(const FilaMaterial* material);

float FilaMaterial_getMaskThreshold(const FilaMaterial* material);
float FilaMaterial_getSpecularAntiAliasingVariance(const FilaMaterial* material);
float FilaMaterial_getSpecularAntiAliasingThreshold(const FilaMaterial* material);

uint32_t FilaMaterial_getParameterCount(const FilaMaterial* material);
void FilaMaterial_getParameters(const FilaMaterial* material, FilaMaterialParameterInfo* out, uint32_t count);

uint32_t FilaMaterial_getRequiredAttributes(const FilaMaterial* material);
bool FilaMaterial_hasParameter(const FilaMaterial* material, const char* name);
const char* FilaMaterial_getParameterTransformName(const FilaMaterial* material, const char* samplerName);

void FilaMaterial_compile(FilaMaterial* material, int priority, int variants, void* handler, FilaMaterialCompileCallback callback, void* userData);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_MATERIAL_H
