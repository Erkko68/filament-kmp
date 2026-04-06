#ifndef FILAMENT_C_TONE_MAPPER_H
#define FILAMENT_C_TONE_MAPPER_H

#include "FilaTypes.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef enum FilaAgxLook {
    FILA_AGX_LOOK_NONE = 0,
    FILA_AGX_LOOK_PUNCHY = 1,
    FILA_AGX_LOOK_GOLDEN = 2,
} FilaAgxLook;

// ToneMapper Factories
FilaToneMapper* FilaToneMapper_Linear();
FilaToneMapper* FilaToneMapper_ACES();
FilaToneMapper* FilaToneMapper_ACESLegacy();
FilaToneMapper* FilaToneMapper_Filmic();
FilaToneMapper* FilaToneMapper_PBRNeutral();
FilaToneMapper* FilaToneMapper_GT7();
FilaToneMapper* FilaToneMapper_Agx(FilaAgxLook look);
FilaToneMapper* FilaToneMapper_Generic(float contrast, float midGrayIn, float midGrayOut, float hdrMax);
FilaToneMapper* FilaToneMapper_DisplayRange();

void FilaToneMapper_destroy(FilaToneMapper* toneMapper);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_TONE_MAPPER_H
