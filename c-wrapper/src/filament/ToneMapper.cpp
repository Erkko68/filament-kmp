#include <filament/ToneMapper.h>

#include "../../include/filament/ToneMapper.h"

extern "C" {

void FilaToneMapper_destroy(FilaToneMapper* toneMapper) {
    delete reinterpret_cast<filament::ToneMapper*>(toneMapper);
}

FilaLinearToneMapper* FilaLinearToneMapper_create(void) {
    return reinterpret_cast<FilaLinearToneMapper*>(new filament::LinearToneMapper());
}
FilaToneMapper* FilaLinearToneMapper_asToneMapper(FilaLinearToneMapper* mapper) {
    return reinterpret_cast<FilaToneMapper*>(static_cast<filament::ToneMapper*>(reinterpret_cast<filament::LinearToneMapper*>(mapper)));
}

FilaACESToneMapper* FilaACESToneMapper_create(void) {
    return reinterpret_cast<FilaACESToneMapper*>(new filament::ACESToneMapper());
}
FilaToneMapper* FilaACESToneMapper_asToneMapper(FilaACESToneMapper* mapper) {
    return reinterpret_cast<FilaToneMapper*>(static_cast<filament::ToneMapper*>(reinterpret_cast<filament::ACESToneMapper*>(mapper)));
}

FilaACESLegacyToneMapper* FilaACESLegacyToneMapper_create(void) {
    return reinterpret_cast<FilaACESLegacyToneMapper*>(new filament::ACESLegacyToneMapper());
}
FilaToneMapper* FilaACESLegacyToneMapper_asToneMapper(FilaACESLegacyToneMapper* mapper) {
    return reinterpret_cast<FilaToneMapper*>(static_cast<filament::ToneMapper*>(reinterpret_cast<filament::ACESLegacyToneMapper*>(mapper)));
}

FilaFilmicToneMapper* FilaFilmicToneMapper_create(void) {
    return reinterpret_cast<FilaFilmicToneMapper*>(new filament::FilmicToneMapper());
}
FilaToneMapper* FilaFilmicToneMapper_asToneMapper(FilaFilmicToneMapper* mapper) {
    return reinterpret_cast<FilaToneMapper*>(static_cast<filament::ToneMapper*>(reinterpret_cast<filament::FilmicToneMapper*>(mapper)));
}

FilaPBRNeutralToneMapper* FilaPBRNeutralToneMapper_create(void) {
    return reinterpret_cast<FilaPBRNeutralToneMapper*>(new filament::PBRNeutralToneMapper());
}
FilaToneMapper* FilaPBRNeutralToneMapper_asToneMapper(FilaPBRNeutralToneMapper* mapper) {
    return reinterpret_cast<FilaToneMapper*>(static_cast<filament::ToneMapper*>(reinterpret_cast<filament::PBRNeutralToneMapper*>(mapper)));
}

FilaGT7ToneMapper* FilaGT7ToneMapper_create(void) {
    return reinterpret_cast<FilaGT7ToneMapper*>(new filament::GT7ToneMapper());
}
FilaToneMapper* FilaGT7ToneMapper_asToneMapper(FilaGT7ToneMapper* mapper) {
    return reinterpret_cast<FilaToneMapper*>(static_cast<filament::ToneMapper*>(reinterpret_cast<filament::GT7ToneMapper*>(mapper)));
}

FilaAgxToneMapper* FilaAgxToneMapper_create(FilaAgxLook look) {
    return reinterpret_cast<FilaAgxToneMapper*>(new filament::AgxToneMapper(static_cast<filament::AgxToneMapper::AgxLook>(look)));
}
FilaToneMapper* FilaAgxToneMapper_asToneMapper(FilaAgxToneMapper* mapper) {
    return reinterpret_cast<FilaToneMapper*>(static_cast<filament::ToneMapper*>(reinterpret_cast<filament::AgxToneMapper*>(mapper)));
}

FilaGenericToneMapper* FilaGenericToneMapper_create(float contrast, float midGrayIn, float midGrayOut, float hdrMax) {
    return reinterpret_cast<FilaGenericToneMapper*>(new filament::GenericToneMapper(contrast, midGrayIn, midGrayOut, hdrMax));
}
FilaToneMapper* FilaGenericToneMapper_asToneMapper(FilaGenericToneMapper* mapper) {
    return reinterpret_cast<FilaToneMapper*>(static_cast<filament::ToneMapper*>(reinterpret_cast<filament::GenericToneMapper*>(mapper)));
}
float FilaGenericToneMapper_getContrast(const FilaGenericToneMapper* mapper) {
    return reinterpret_cast<const filament::GenericToneMapper*>(mapper)->getContrast();
}
float FilaGenericToneMapper_getMidGrayIn(const FilaGenericToneMapper* mapper) {
    return reinterpret_cast<const filament::GenericToneMapper*>(mapper)->getMidGrayIn();
}
float FilaGenericToneMapper_getMidGrayOut(const FilaGenericToneMapper* mapper) {
    return reinterpret_cast<const filament::GenericToneMapper*>(mapper)->getMidGrayOut();
}
float FilaGenericToneMapper_getHdrMax(const FilaGenericToneMapper* mapper) {
    return reinterpret_cast<const filament::GenericToneMapper*>(mapper)->getHdrMax();
}
void FilaGenericToneMapper_setContrast(FilaGenericToneMapper* mapper, float contrast) {
    reinterpret_cast<filament::GenericToneMapper*>(mapper)->setContrast(contrast);
}
void FilaGenericToneMapper_setMidGrayIn(FilaGenericToneMapper* mapper, float midGrayIn) {
    reinterpret_cast<filament::GenericToneMapper*>(mapper)->setMidGrayIn(midGrayIn);
}
void FilaGenericToneMapper_setMidGrayOut(FilaGenericToneMapper* mapper, float midGrayOut) {
    reinterpret_cast<filament::GenericToneMapper*>(mapper)->setMidGrayOut(midGrayOut);
}
void FilaGenericToneMapper_setHdrMax(FilaGenericToneMapper* mapper, float hdrMax) {
    reinterpret_cast<filament::GenericToneMapper*>(mapper)->setHdrMax(hdrMax);
}

FilaDisplayRangeToneMapper* FilaDisplayRangeToneMapper_create(void) {
    return reinterpret_cast<FilaDisplayRangeToneMapper*>(new filament::DisplayRangeToneMapper());
}
FilaToneMapper* FilaDisplayRangeToneMapper_asToneMapper(FilaDisplayRangeToneMapper* mapper) {
    return reinterpret_cast<FilaToneMapper*>(static_cast<filament::ToneMapper*>(reinterpret_cast<filament::DisplayRangeToneMapper*>(mapper)));
}

}
