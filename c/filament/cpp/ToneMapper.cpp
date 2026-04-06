#include <filament/ToneMapper.h>

#include "FilaCommon.h"
#include "../c/ToneMapper.h"

using namespace filament;

extern "C" {

FilaToneMapper* FilaToneMapper_Linear() {
    return reinterpret_cast<FilaToneMapper*>(new LinearToneMapper());
}

FilaToneMapper* FilaToneMapper_ACES() {
    return reinterpret_cast<FilaToneMapper*>(new ACESToneMapper());
}

FilaToneMapper* FilaToneMapper_ACESLegacy() {
    return reinterpret_cast<FilaToneMapper*>(new ACESLegacyToneMapper());
}

FilaToneMapper* FilaToneMapper_Filmic() {
    return reinterpret_cast<FilaToneMapper*>(new FilmicToneMapper());
}

FilaToneMapper* FilaToneMapper_PBRNeutral() {
    return reinterpret_cast<FilaToneMapper*>(new PBRNeutralToneMapper());
}

FilaToneMapper* FilaToneMapper_GT7() {
    return reinterpret_cast<FilaToneMapper*>(new GT7ToneMapper());
}

FilaToneMapper* FilaToneMapper_Agx(FilaAgxLook look) {
    return reinterpret_cast<FilaToneMapper*>(new AgxToneMapper(static_cast<AgxToneMapper::AgxLook>(look)));
}

FilaToneMapper* FilaToneMapper_Generic(float contrast, float midGrayIn, float midGrayOut, float hdrMax) {
    return reinterpret_cast<FilaToneMapper*>(new GenericToneMapper(contrast, midGrayIn, midGrayOut, hdrMax));
}

FilaToneMapper* FilaToneMapper_DisplayRange() {
    return reinterpret_cast<FilaToneMapper*>(new DisplayRangeToneMapper());
}

void FilaToneMapper_destroy(FilaToneMapper* toneMapper) {
    delete reinterpret_cast<ToneMapper*>(toneMapper);
}

} // extern "C"
