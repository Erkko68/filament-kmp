#ifndef FILAMENT_C_MATERIAL_INSTANCE_H
#define FILAMENT_C_MATERIAL_INSTANCE_H

#include <stdbool.h>
#include <stdint.h>

#include "Color.h"
#include "MaterialEnums.h"
#include "Types.h"
#include "../backend/DriverEnums.h"

#ifdef __cplusplus
extern "C" {
#endif

// Returns the parent material for this instance.
const FilaMaterial* FilaMaterialInstance_getMaterial(const FilaMaterialInstance* materialInstance);
const char* FilaMaterialInstance_getName(const FilaMaterialInstance* materialInstance);
FilaMaterialInstance* FilaMaterialInstance_duplicate(const FilaMaterialInstance* materialInstance, const char* name);

// Sets a float uniform parameter by name.
void FilaMaterialInstance_setParameterFloat(FilaMaterialInstance* materialInstance, const char* name, float x);

// Sets a float2 uniform parameter by name.
void FilaMaterialInstance_setParameterFloat2(FilaMaterialInstance* materialInstance, const char* name, float x, float y);

// Sets a float3 uniform parameter by name.
void FilaMaterialInstance_setParameterFloat3(FilaMaterialInstance* materialInstance, const char* name, float x, float y, float z);

// Sets a float4 uniform parameter by name.
void FilaMaterialInstance_setParameterFloat4(FilaMaterialInstance* materialInstance, const char* name, float x, float y, float z, float w);

// Sets an int uniform parameter by name.
void FilaMaterialInstance_setParameterInt(FilaMaterialInstance* materialInstance, const char* name, int32_t x);

// Sets an int2/int3/int4 uniform parameter by name.
void FilaMaterialInstance_setParameterInt2(FilaMaterialInstance* materialInstance, const char* name, int32_t x, int32_t y);
void FilaMaterialInstance_setParameterInt3(FilaMaterialInstance* materialInstance, const char* name, int32_t x, int32_t y, int32_t z);
void FilaMaterialInstance_setParameterInt4(FilaMaterialInstance* materialInstance, const char* name, int32_t x, int32_t y, int32_t z, int32_t w);

// Sets an unsigned int uniform parameter by name.
void FilaMaterialInstance_setParameterUint(FilaMaterialInstance* materialInstance, const char* name, uint32_t x);

// Sets an uint2/uint3/uint4 uniform parameter by name.
void FilaMaterialInstance_setParameterUint2(FilaMaterialInstance* materialInstance, const char* name, uint32_t x, uint32_t y);
void FilaMaterialInstance_setParameterUint3(FilaMaterialInstance* materialInstance, const char* name, uint32_t x, uint32_t y, uint32_t z);
void FilaMaterialInstance_setParameterUint4(FilaMaterialInstance* materialInstance, const char* name, uint32_t x, uint32_t y, uint32_t z, uint32_t w);

// Sets a bool/bool2/bool3/bool4 uniform parameter by name.
void FilaMaterialInstance_setParameterBool(FilaMaterialInstance* materialInstance, const char* name, bool x);
void FilaMaterialInstance_setParameterBool2(FilaMaterialInstance* materialInstance, const char* name, bool x, bool y);
void FilaMaterialInstance_setParameterBool3(FilaMaterialInstance* materialInstance, const char* name, bool x, bool y, bool z);
void FilaMaterialInstance_setParameterBool4(FilaMaterialInstance* materialInstance, const char* name, bool x, bool y, bool z, bool w);

// Sets a mat3/mat4 uniform parameter by name (column-major order).
void FilaMaterialInstance_setParameterMat3f(FilaMaterialInstance* materialInstance, const char* name, const float value[9]);
void FilaMaterialInstance_setParameterMat4f(FilaMaterialInstance* materialInstance, const char* name, const float value[16]);

// Sets RGB / RGBA color parameter by name.
void FilaMaterialInstance_setParameterRgb(FilaMaterialInstance* materialInstance, const char* name, FilaRgbType type, float r, float g, float b);
void FilaMaterialInstance_setParameterRgba(FilaMaterialInstance* materialInstance, const char* name, FilaRgbaType type, float r, float g, float b, float a);

// Sets a texture+sampler parameter by name.
void FilaMaterialInstance_setParameterTexture(
    FilaMaterialInstance* materialInstance,
    const char* name,
    const FilaTexture* texture,
    const FilaTextureParams* sampler);

// Gets scalar/vector/matrix uniform parameters by name.
bool FilaMaterialInstance_getParameterFloat(const FilaMaterialInstance* materialInstance, const char* name, float* outX);
bool FilaMaterialInstance_getParameterFloat2(const FilaMaterialInstance* materialInstance, const char* name, float outValue[2]);
bool FilaMaterialInstance_getParameterFloat3(const FilaMaterialInstance* materialInstance, const char* name, float outValue[3]);
bool FilaMaterialInstance_getParameterFloat4(const FilaMaterialInstance* materialInstance, const char* name, float outValue[4]);

bool FilaMaterialInstance_getParameterInt(const FilaMaterialInstance* materialInstance, const char* name, int32_t* outX);
bool FilaMaterialInstance_getParameterInt2(const FilaMaterialInstance* materialInstance, const char* name, int32_t outValue[2]);
bool FilaMaterialInstance_getParameterInt3(const FilaMaterialInstance* materialInstance, const char* name, int32_t outValue[3]);
bool FilaMaterialInstance_getParameterInt4(const FilaMaterialInstance* materialInstance, const char* name, int32_t outValue[4]);

bool FilaMaterialInstance_getParameterUint(const FilaMaterialInstance* materialInstance, const char* name, uint32_t* outX);
bool FilaMaterialInstance_getParameterUint2(const FilaMaterialInstance* materialInstance, const char* name, uint32_t outValue[2]);
bool FilaMaterialInstance_getParameterUint3(const FilaMaterialInstance* materialInstance, const char* name, uint32_t outValue[3]);
bool FilaMaterialInstance_getParameterUint4(const FilaMaterialInstance* materialInstance, const char* name, uint32_t outValue[4]);

bool FilaMaterialInstance_getParameterBool(const FilaMaterialInstance* materialInstance, const char* name, bool* outX);
bool FilaMaterialInstance_getParameterBool2(const FilaMaterialInstance* materialInstance, const char* name, bool outValue[2]);
bool FilaMaterialInstance_getParameterBool3(const FilaMaterialInstance* materialInstance, const char* name, bool outValue[3]);
bool FilaMaterialInstance_getParameterBool4(const FilaMaterialInstance* materialInstance, const char* name, bool outValue[4]);


bool FilaMaterialInstance_getParameterMat3f(const FilaMaterialInstance* materialInstance, const char* name, float outValue[9]);
bool FilaMaterialInstance_getParameterMat4f(const FilaMaterialInstance* materialInstance, const char* name, float outValue[16]);

// Configures a custom scissor rectangle and resets it.
void FilaMaterialInstance_setScissor(FilaMaterialInstance* materialInstance, uint32_t left, uint32_t bottom, uint32_t width, uint32_t height);
void FilaMaterialInstance_unsetScissor(FilaMaterialInstance* materialInstance);

// Per-instance raster/depth/specular overrides.
void FilaMaterialInstance_setPolygonOffset(FilaMaterialInstance* materialInstance, float scale, float constant);
void FilaMaterialInstance_setMaskThreshold(FilaMaterialInstance* materialInstance, float threshold);
float FilaMaterialInstance_getMaskThreshold(const FilaMaterialInstance* materialInstance);
void FilaMaterialInstance_setSpecularAntiAliasingVariance(FilaMaterialInstance* materialInstance, float variance);
float FilaMaterialInstance_getSpecularAntiAliasingVariance(const FilaMaterialInstance* materialInstance);
void FilaMaterialInstance_setSpecularAntiAliasingThreshold(FilaMaterialInstance* materialInstance, float threshold);
float FilaMaterialInstance_getSpecularAntiAliasingThreshold(const FilaMaterialInstance* materialInstance);

// Culling/transparency/write-state overrides.
void FilaMaterialInstance_setDoubleSided(FilaMaterialInstance* materialInstance, bool doubleSided);
bool FilaMaterialInstance_isDoubleSided(const FilaMaterialInstance* materialInstance);
void FilaMaterialInstance_setTransparencyMode(FilaMaterialInstance* materialInstance, FilaMaterialTransparencyMode mode);
FilaMaterialTransparencyMode FilaMaterialInstance_getTransparencyMode(const FilaMaterialInstance* materialInstance);
void FilaMaterialInstance_setCullingMode(FilaMaterialInstance* materialInstance, FilaBackendCullingMode cullingMode);
void FilaMaterialInstance_setCullingModeSeparate(
    FilaMaterialInstance* materialInstance,
    FilaBackendCullingMode colorPassCullingMode,
    FilaBackendCullingMode shadowPassCullingMode);
FilaBackendCullingMode FilaMaterialInstance_getCullingMode(const FilaMaterialInstance* materialInstance);
FilaBackendCullingMode FilaMaterialInstance_getShadowCullingMode(const FilaMaterialInstance* materialInstance);
void FilaMaterialInstance_setColorWrite(FilaMaterialInstance* materialInstance, bool enable);
bool FilaMaterialInstance_isColorWriteEnabled(const FilaMaterialInstance* materialInstance);
void FilaMaterialInstance_setDepthWrite(FilaMaterialInstance* materialInstance, bool enable);
bool FilaMaterialInstance_isDepthWriteEnabled(const FilaMaterialInstance* materialInstance);
void FilaMaterialInstance_setDepthCulling(FilaMaterialInstance* materialInstance, bool enable);
bool FilaMaterialInstance_isDepthCullingEnabled(const FilaMaterialInstance* materialInstance);
void FilaMaterialInstance_setDepthFunc(FilaMaterialInstance* materialInstance, FilaBackendSamplerCompareFunc depthFunc);
FilaBackendSamplerCompareFunc FilaMaterialInstance_getDepthFunc(const FilaMaterialInstance* materialInstance);

// Stencil state overrides.
void FilaMaterialInstance_setStencilWrite(FilaMaterialInstance* materialInstance, bool enable);
bool FilaMaterialInstance_isStencilWriteEnabled(const FilaMaterialInstance* materialInstance);
void FilaMaterialInstance_setStencilCompareFunction(
    FilaMaterialInstance* materialInstance,
    FilaBackendSamplerCompareFunc func,
    FilaBackendStencilFace face);
void FilaMaterialInstance_setStencilOpStencilFail(
    FilaMaterialInstance* materialInstance,
    FilaBackendStencilOperation op,
    FilaBackendStencilFace face);
void FilaMaterialInstance_setStencilOpDepthFail(
    FilaMaterialInstance* materialInstance,
    FilaBackendStencilOperation op,
    FilaBackendStencilFace face);
void FilaMaterialInstance_setStencilOpDepthStencilPass(
    FilaMaterialInstance* materialInstance,
    FilaBackendStencilOperation op,
    FilaBackendStencilFace face);
void FilaMaterialInstance_setStencilReferenceValue(
    FilaMaterialInstance* materialInstance,
    uint8_t value,
    FilaBackendStencilFace face);
void FilaMaterialInstance_setStencilReadMask(
    FilaMaterialInstance* materialInstance,
    uint8_t readMask,
    FilaBackendStencilFace face);
void FilaMaterialInstance_setStencilWriteMask(
    FilaMaterialInstance* materialInstance,
    uint8_t writeMask,
    FilaBackendStencilFace face);

// Commits compute / post-process instance updates.
void FilaMaterialInstance_commit(const FilaMaterialInstance* materialInstance, FilaEngine* engine);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_MATERIAL_INSTANCE_H

