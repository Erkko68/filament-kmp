#ifndef FILAMENT_C_MATERIAL_INSTANCE_H
#define FILAMENT_C_MATERIAL_INSTANCE_H

#include <stdint.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Returns the parent material for this instance.
const FilaMaterial* FilaMaterialInstance_getMaterial(const FilaMaterialInstance* materialInstance);

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

// Sets an unsigned int uniform parameter by name.
void FilaMaterialInstance_setParameterUint(FilaMaterialInstance* materialInstance, const char* name, uint32_t x);

// Sets a texture+sampler parameter by name.
void FilaMaterialInstance_setParameterTexture(
    FilaMaterialInstance* materialInstance,
    const char* name,
    const FilaTexture* texture,
    const FilaTextureParams* sampler);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_MATERIAL_INSTANCE_H

