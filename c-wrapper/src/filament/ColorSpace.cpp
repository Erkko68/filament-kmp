#include <filament/ColorSpace.h>

#include <math/vec2.h>

#include "../../include/filament/ColorSpace.h"

extern "C" {

void FilaColorSpace_setRec709Primaries(FilaColorSpacePrimaries* outPrimaries) {
    if (!outPrimaries) {
        return;
    }
    outPrimaries->r[0] = filament::color::Rec709.getPrimaries().r.x;
    outPrimaries->r[1] = filament::color::Rec709.getPrimaries().r.y;
    outPrimaries->g[0] = filament::color::Rec709.getPrimaries().g.x;
    outPrimaries->g[1] = filament::color::Rec709.getPrimaries().g.y;
    outPrimaries->b[0] = filament::color::Rec709.getPrimaries().b.x;
    outPrimaries->b[1] = filament::color::Rec709.getPrimaries().b.y;
}

void FilaColorSpace_setD65WhitePoint(FilaColorSpaceWhitePoint* outWhitePoint) {
    if (!outWhitePoint) {
        return;
    }
    outWhitePoint->xy[0] = filament::color::D65.x;
    outWhitePoint->xy[1] = filament::color::D65.y;
}

void FilaColorSpace_setLinearTransferFunction(FilaColorSpaceTransferFunction* outTf) {
    if (!outTf) {
        return;
    }
    outTf->a = filament::color::Linear.a;
    outTf->b = filament::color::Linear.b;
    outTf->c = filament::color::Linear.c;
    outTf->d = filament::color::Linear.d;
    outTf->e = filament::color::Linear.e;
    outTf->f = filament::color::Linear.f;
    outTf->g = filament::color::Linear.g;
}

void FilaColorSpace_setSrgbTransferFunction(FilaColorSpaceTransferFunction* outTf) {
    if (!outTf) {
        return;
    }
    outTf->a = filament::color::sRGB.a;
    outTf->b = filament::color::sRGB.b;
    outTf->c = filament::color::sRGB.c;
    outTf->d = filament::color::sRGB.d;
    outTf->e = filament::color::sRGB.e;
    outTf->f = filament::color::sRGB.f;
    outTf->g = filament::color::sRGB.g;
}

void FilaColorSpace_set(FilaColorSpace* colorSpace,
        const FilaColorSpacePrimaries* primaries,
        const FilaColorSpaceTransferFunction* transferFunction,
        const FilaColorSpaceWhitePoint* whitePoint) {
    if (!colorSpace || !primaries || !transferFunction || !whitePoint) {
        return;
    }
    colorSpace->primaries = *primaries;
    colorSpace->transferFunction = *transferFunction;
    colorSpace->whitePoint = *whitePoint;
}

} // extern "C"

