#ifndef FILAMENT_C_SKYBOX_H
#define FILAMENT_C_SKYBOX_H

#include <stdbool.h>
#include <stdint.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

FilaSkyboxBuilder* FilaSkyboxBuilder_create(void);
void FilaSkyboxBuilder_destroy(FilaSkyboxBuilder* builder);
void FilaSkyboxBuilder_environment(FilaSkyboxBuilder* builder, FilaTexture* cubemap);
void FilaSkyboxBuilder_showSun(FilaSkyboxBuilder* builder, bool show);
void FilaSkyboxBuilder_intensity(FilaSkyboxBuilder* builder, float intensity);
void FilaSkyboxBuilder_color(FilaSkyboxBuilder* builder, float r, float g, float b, float a);
void FilaSkyboxBuilder_priority(FilaSkyboxBuilder* builder, uint8_t priority);
FilaSkybox* FilaSkyboxBuilder_build(FilaSkyboxBuilder* builder, FilaEngine* engine);

void FilaSkybox_setLayerMask(FilaSkybox* skybox, uint8_t select, uint8_t values);
uint8_t FilaSkybox_getLayerMask(const FilaSkybox* skybox);
float FilaSkybox_getIntensity(const FilaSkybox* skybox);
const FilaTexture* FilaSkybox_getTexture(const FilaSkybox* skybox);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_SKYBOX_H

