#include "filament/ColorSpace.h"

void test_headers_color_space(void) {
    FilaColorSpacePrimaries primaries;
    FilaColorSpaceWhitePoint whitePoint;
    FilaColorSpaceTransferFunction transferFn;
    FilaColorSpace colorSpace;

    FilaColorSpace_setRec709Primaries(&primaries);
    FilaColorSpace_setD65WhitePoint(&whitePoint);
    FilaColorSpace_setLinearTransferFunction(&transferFn);
    FilaColorSpace_setSrgbTransferFunction(&transferFn);
    FilaColorSpace_set(&colorSpace, &primaries, &transferFn, &whitePoint);
}

