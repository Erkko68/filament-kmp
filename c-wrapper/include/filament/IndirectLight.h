#ifndef FILAMENT_C_INDIRECT_LIGHT_H
#define FILAMENT_C_INDIRECT_LIGHT_H

#include <stdbool.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

FilaIndirectLightBuilder* FilaIndirectLightBuilder_create(void);
void FilaIndirectLightBuilder_destroy(FilaIndirectLightBuilder* builder);
void FilaIndirectLightBuilder_reflections(FilaIndirectLightBuilder* builder, const FilaTexture* cubemap);
void FilaIndirectLightBuilder_intensity(FilaIndirectLightBuilder* builder, float intensity);
void FilaIndirectLightBuilder_radiance(FilaIndirectLightBuilder* builder, uint8_t bands, const float* sh3);
void FilaIndirectLightBuilder_rotationMat3f(FilaIndirectLightBuilder* builder, const float rotation3x3[9]);
FilaIndirectLight* FilaIndirectLightBuilder_build(FilaIndirectLightBuilder* builder, FilaEngine* engine);

void FilaIndirectLight_setIntensity(FilaIndirectLight* indirectLight, float intensity);
float FilaIndirectLight_getIntensity(const FilaIndirectLight* indirectLight);
void FilaIndirectLight_setRotationMat3f(FilaIndirectLight* indirectLight, const float rotation3x3[9]);
bool FilaIndirectLight_getRotationMat3f(const FilaIndirectLight* indirectLight, float outRotation3x3[9]);
bool FilaIndirectLight_getDirectionEstimate(const FilaIndirectLight* indirectLight, float outDirection3[3]);
bool FilaIndirectLight_getColorEstimate(const FilaIndirectLight* indirectLight, const float direction3[3], float outColor4[4]);
const FilaTexture* FilaIndirectLight_getReflectionsTexture(const FilaIndirectLight* indirectLight);
const FilaTexture* FilaIndirectLight_getIrradianceTexture(const FilaIndirectLight* indirectLight);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_INDIRECT_LIGHT_H

