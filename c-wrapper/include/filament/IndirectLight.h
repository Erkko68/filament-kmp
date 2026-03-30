#ifndef FILAMENT_C_INDIRECT_LIGHT_H
#define FILAMENT_C_INDIRECT_LIGHT_H

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

FilaIndirectLightBuilder* FilaIndirectLightBuilder_create(void);
void FilaIndirectLightBuilder_destroy(FilaIndirectLightBuilder* builder);
void FilaIndirectLightBuilder_reflections(FilaIndirectLightBuilder* builder, const FilaTexture* cubemap);
void FilaIndirectLightBuilder_intensity(FilaIndirectLightBuilder* builder, float intensity);
FilaIndirectLight* FilaIndirectLightBuilder_build(FilaIndirectLightBuilder* builder, FilaEngine* engine);

void FilaIndirectLight_setIntensity(FilaIndirectLight* indirectLight, float intensity);
float FilaIndirectLight_getIntensity(const FilaIndirectLight* indirectLight);
const FilaTexture* FilaIndirectLight_getReflectionsTexture(const FilaIndirectLight* indirectLight);
const FilaTexture* FilaIndirectLight_getIrradianceTexture(const FilaIndirectLight* indirectLight);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_INDIRECT_LIGHT_H

