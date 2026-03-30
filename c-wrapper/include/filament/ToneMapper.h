#ifndef FILAMENT_C_TONEMAPPER_H
#define FILAMENT_C_TONEMAPPER_H

#include "Types.h"
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct FilaToneMapper FilaToneMapper;
typedef struct FilaLinearToneMapper FilaLinearToneMapper;
typedef struct FilaACESToneMapper FilaACESToneMapper;
typedef struct FilaACESLegacyToneMapper FilaACESLegacyToneMapper;
typedef struct FilaFilmicToneMapper FilaFilmicToneMapper;
typedef struct FilaPBRNeutralToneMapper FilaPBRNeutralToneMapper;
typedef struct FilaGT7ToneMapper FilaGT7ToneMapper;
typedef struct FilaAgxToneMapper FilaAgxToneMapper;
typedef struct FilaGenericToneMapper FilaGenericToneMapper;
typedef struct FilaDisplayRangeToneMapper FilaDisplayRangeToneMapper;

// AgxLook enum
typedef enum FilaAgxLook {
    FILA_AGX_LOOK_NONE = 0,
    FILA_AGX_LOOK_PUNCHY = 1,
    FILA_AGX_LOOK_GOLDEN = 2
} FilaAgxLook;

// Base ToneMapper functions (if any exist that need exposing)
void FilaToneMapper_destroy(FilaToneMapper* toneMapper);

// LinearToneMapper
FilaLinearToneMapper* FilaLinearToneMapper_create(void);
FilaToneMapper* FilaLinearToneMapper_asToneMapper(FilaLinearToneMapper* mapper);

// ACESToneMapper
FilaACESToneMapper* FilaACESToneMapper_create(void);
FilaToneMapper* FilaACESToneMapper_asToneMapper(FilaACESToneMapper* mapper);

// ACESLegacyToneMapper
FilaACESLegacyToneMapper* FilaACESLegacyToneMapper_create(void);
FilaToneMapper* FilaACESLegacyToneMapper_asToneMapper(FilaACESLegacyToneMapper* mapper);

// FilmicToneMapper
FilaFilmicToneMapper* FilaFilmicToneMapper_create(void);
FilaToneMapper* FilaFilmicToneMapper_asToneMapper(FilaFilmicToneMapper* mapper);

// PBRNeutralToneMapper
FilaPBRNeutralToneMapper* FilaPBRNeutralToneMapper_create(void);
FilaToneMapper* FilaPBRNeutralToneMapper_asToneMapper(FilaPBRNeutralToneMapper* mapper);

// GT7ToneMapper
FilaGT7ToneMapper* FilaGT7ToneMapper_create(void);
FilaToneMapper* FilaGT7ToneMapper_asToneMapper(FilaGT7ToneMapper* mapper);

// AgxToneMapper
FilaAgxToneMapper* FilaAgxToneMapper_create(FilaAgxLook look);
FilaToneMapper* FilaAgxToneMapper_asToneMapper(FilaAgxToneMapper* mapper);

// GenericToneMapper
FilaGenericToneMapper* FilaGenericToneMapper_create(float contrast, float midGrayIn, float midGrayOut, float hdrMax);
FilaToneMapper* FilaGenericToneMapper_asToneMapper(FilaGenericToneMapper* mapper);
float FilaGenericToneMapper_getContrast(const FilaGenericToneMapper* mapper);
float FilaGenericToneMapper_getMidGrayIn(const FilaGenericToneMapper* mapper);
float FilaGenericToneMapper_getMidGrayOut(const FilaGenericToneMapper* mapper);
float FilaGenericToneMapper_getHdrMax(const FilaGenericToneMapper* mapper);
void FilaGenericToneMapper_setContrast(FilaGenericToneMapper* mapper, float contrast);
void FilaGenericToneMapper_setMidGrayIn(FilaGenericToneMapper* mapper, float midGrayIn);
void FilaGenericToneMapper_setMidGrayOut(FilaGenericToneMapper* mapper, float midGrayOut);
void FilaGenericToneMapper_setHdrMax(FilaGenericToneMapper* mapper, float hdrMax);

// DisplayRangeToneMapper
FilaDisplayRangeToneMapper* FilaDisplayRangeToneMapper_create(void);
FilaToneMapper* FilaDisplayRangeToneMapper_asToneMapper(FilaDisplayRangeToneMapper* mapper);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_TONEMAPPER_H