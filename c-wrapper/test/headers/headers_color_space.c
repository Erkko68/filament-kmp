#include "filament/ColorSpace.h"

void test_headers_color_space(void) {
    FilaColorSpacePrimaries primaries;
    FilaColorSpaceWhitePoint whitePoint;
    FilaColorSpaceTransferFunction transferFn;
    FilaColorSpace colorSpace;

    FilaColorSpacePrimaries_set(&primaries, 0.64f, 0.33f, 0.30f, 0.60f, 0.15f, 0.06f);
    FilaColorSpaceWhitePoint_set(&whitePoint, 0.3127f, 0.3290f);
    FilaColorSpaceTransferFunction_set(&transferFn, 1.0, 0.0, 1.0, 0.5, 0.0, 0.0, 2.2);
    FilaColorSpaceTransferFunction_setType3(&transferFn, 1.0, 0.0, 1.0, 0.5, 2.2);
    FilaColorSpace_setRec709Primaries(&primaries);
    FilaColorSpace_setD65WhitePoint(&whitePoint);
    FilaColorSpace_setLinearTransferFunction(&transferFn);
    FilaColorSpace_setSrgbTransferFunction(&transferFn);
    FilaColorSpace_set(&colorSpace, &primaries, &transferFn, &whitePoint);
}

