#include <math.h>
#include <stdbool.h>
#include <stdio.h>

#include "filament/ColorSpace.h"

static bool allFinite(const float* values, size_t count) {
    for (size_t i = 0; i < count; ++i) {
        if (!isfinite(values[i])) {
            return false;
        }
    }
    return true;
}

int main(void) {
    printf("Running functionality_color_space...\n");

    FilaColorSpacePrimaries primaries;
    FilaColorSpaceWhitePoint whitePoint;
    FilaColorSpaceTransferFunction transferFn;
    FilaColorSpace colorSpace;

    FilaColorSpacePrimaries_set(&primaries, 0.64f, 0.33f, 0.30f, 0.60f, 0.15f, 0.06f);
    if (primaries.r[0] != 0.64f || primaries.g[1] != 0.60f || primaries.b[0] != 0.15f) {
        printf("ColorSpace primaries setter mismatch\n");
        return 1;
    }
    FilaColorSpaceWhitePoint_set(&whitePoint, 0.3127f, 0.3290f);
    if (whitePoint.xy[0] != 0.3127f || whitePoint.xy[1] != 0.3290f) {
        printf("ColorSpace white-point setter mismatch\n");
        return 1;
    }
    FilaColorSpaceTransferFunction_setType3(&transferFn, 1.0, 0.0, 1.0 / 12.92, 0.04045, 2.4);
    if (transferFn.e != 0.0 || transferFn.f != 0.0 || transferFn.g != 2.4) {
        printf("ColorSpace transfer-function type3 setter mismatch\n");
        return 1;
    }

    FilaColorSpace_setRec709Primaries(&primaries);
    FilaColorSpace_setD65WhitePoint(&whitePoint);
    FilaColorSpace_setLinearTransferFunction(&transferFn);

    if (!allFinite(primaries.r, 2u) || !allFinite(primaries.g, 2u) || !allFinite(primaries.b, 2u) ||
            !allFinite(whitePoint.xy, 2u)) {
        printf("ColorSpace primaries/white-point contain non-finite values\n");
        return 1;
    }

    // Ensure the aggregate setter performs a full copy.
    FilaColorSpace_set(&colorSpace, &primaries, &transferFn, &whitePoint);
    if (colorSpace.primaries.r[0] != primaries.r[0] ||
            colorSpace.whitePoint.xy[1] != whitePoint.xy[1] ||
            colorSpace.transferFunction.g != transferFn.g) {
        printf("ColorSpace aggregate copy mismatch\n");
        return 1;
    }

    // Null outputs must be safe no-ops.
    FilaColorSpace_setRec709Primaries((FilaColorSpacePrimaries*)0);
    FilaColorSpacePrimaries_set((FilaColorSpacePrimaries*)0, 0.f, 0.f, 0.f, 0.f, 0.f, 0.f);
    FilaColorSpace_setD65WhitePoint((FilaColorSpaceWhitePoint*)0);
    FilaColorSpaceWhitePoint_set((FilaColorSpaceWhitePoint*)0, 0.f, 0.f);
    FilaColorSpace_setLinearTransferFunction((FilaColorSpaceTransferFunction*)0);
    FilaColorSpaceTransferFunction_set((FilaColorSpaceTransferFunction*)0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    FilaColorSpaceTransferFunction_setType3((FilaColorSpaceTransferFunction*)0, 0.0, 0.0, 0.0, 0.0, 0.0);
    FilaColorSpace_setSrgbTransferFunction((FilaColorSpaceTransferFunction*)0);
    FilaColorSpace_set((FilaColorSpace*)0, &primaries, &transferFn, &whitePoint);

    printf("functionality_color_space completed\n");
    return 0;
}

