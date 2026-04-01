#ifndef FILAMENT_C_MATERIAL_H
#define FILAMENT_C_MATERIAL_H

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

#include "Types.h"
#include "MaterialEnums.h"
#include "../backend/DriverEnums.h"

#ifdef __cplusplus
extern "C" {
#endif

FilaMaterialBuilder* FilaMaterialBuilder_create(void);
void FilaMaterialBuilder_destroy(FilaMaterialBuilder* builder);
void FilaMaterialBuilder_package(FilaMaterialBuilder* builder, const void* payload, size_t size);

typedef enum FilaMaterialBuilderShadowSamplingQuality {
	FILA_MATERIAL_BUILDER_SHADOW_SAMPLING_QUALITY_HARD = 0,
	FILA_MATERIAL_BUILDER_SHADOW_SAMPLING_QUALITY_LOW = 1,
} FilaMaterialBuilderShadowSamplingQuality;

typedef enum FilaMaterialUboBatchingMode {
	FILA_MATERIAL_UBO_BATCHING_MODE_DEFAULT = 0,
	FILA_MATERIAL_UBO_BATCHING_MODE_DISABLED = 1,
} FilaMaterialUboBatchingMode;

void FilaMaterialBuilder_constantInt(FilaMaterialBuilder* builder, const char* name, int32_t value);
void FilaMaterialBuilder_constantFloat(FilaMaterialBuilder* builder, const char* name, float value);
void FilaMaterialBuilder_constantBool(FilaMaterialBuilder* builder, const char* name, bool value);
void FilaMaterialBuilder_sphericalHarmonicsBandCount(FilaMaterialBuilder* builder, size_t shBandCount);
void FilaMaterialBuilder_shadowSamplingQuality(
	FilaMaterialBuilder* builder,
	FilaMaterialBuilderShadowSamplingQuality quality);
void FilaMaterialBuilder_uboBatching(FilaMaterialBuilder* builder, FilaMaterialUboBatchingMode mode);

FilaMaterial* FilaMaterialBuilder_build(const FilaMaterialBuilder* builder, FilaEngine* engine);

typedef struct FilaMaterialParameterInfo {
	const char* name;
	bool isSampler;
	bool isSubpass;
	FilaBackendUniformType type;
	FilaBackendSamplerType samplerType;
	FilaBackendSubpassType subpassType;
	uint32_t count;
	FilaBackendPrecision precision;
} FilaMaterialParameterInfo;

FilaMaterialInstance* FilaMaterial_createInstance(const FilaMaterial* material);
FilaMaterialInstance* FilaMaterial_createInstanceNamed(const FilaMaterial* material, const char* name);

const char* FilaMaterial_getName(const FilaMaterial* material);
bool FilaMaterial_hasParameter(const FilaMaterial* material, const char* name);
bool FilaMaterial_isSampler(const FilaMaterial* material, const char* name);
size_t FilaMaterial_getParameterCount(const FilaMaterial* material);
size_t FilaMaterial_getParameters(
	const FilaMaterial* material,
	FilaMaterialParameterInfo* outParameters,
	size_t outParameterCount);

FilaMaterialShading FilaMaterial_getShading(const FilaMaterial* material);
FilaMaterialInterpolation FilaMaterial_getInterpolation(const FilaMaterial* material);
FilaMaterialBlendingMode FilaMaterial_getBlendingMode(const FilaMaterial* material);
FilaMaterialVertexDomain FilaMaterial_getVertexDomain(const FilaMaterial* material);
uint8_t FilaMaterial_getSupportedVariants(const FilaMaterial* material);
FilaMaterialDomain FilaMaterial_getMaterialDomain(const FilaMaterial* material);
FilaBackendCullingMode FilaMaterial_getCullingMode(const FilaMaterial* material);
FilaMaterialTransparencyMode FilaMaterial_getTransparencyMode(const FilaMaterial* material);
bool FilaMaterial_isColorWriteEnabled(const FilaMaterial* material);
bool FilaMaterial_isDepthWriteEnabled(const FilaMaterial* material);
bool FilaMaterial_isDepthCullingEnabled(const FilaMaterial* material);
bool FilaMaterial_isDoubleSided(const FilaMaterial* material);
bool FilaMaterial_isAlphaToCoverageEnabled(const FilaMaterial* material);
float FilaMaterial_getMaskThreshold(const FilaMaterial* material);
bool FilaMaterial_hasShadowMultiplier(const FilaMaterial* material);
bool FilaMaterial_hasSpecularAntiAliasing(const FilaMaterial* material);
float FilaMaterial_getSpecularAntiAliasingVariance(const FilaMaterial* material);
float FilaMaterial_getSpecularAntiAliasingThreshold(const FilaMaterial* material);
uint32_t FilaMaterial_getRequiredAttributes(const FilaMaterial* material);
FilaMaterialRefractionMode FilaMaterial_getRefractionMode(const FilaMaterial* material);
FilaMaterialRefractionType FilaMaterial_getRefractionType(const FilaMaterial* material);
FilaMaterialReflectionMode FilaMaterial_getReflectionMode(const FilaMaterial* material);
FilaBackendFeatureLevel FilaMaterial_getFeatureLevel(const FilaMaterial* material);
const char* FilaMaterial_getParameterTransformName(const FilaMaterial* material, const char* samplerName);
FilaMaterialInstance* FilaMaterial_getDefaultInstance(FilaMaterial* material);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_MATERIAL_H

