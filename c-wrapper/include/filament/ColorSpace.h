#ifndef FILAMENT_C_COLOR_SPACE_H
#define FILAMENT_C_COLOR_SPACE_H

#ifdef __cplusplus
extern "C" {
#endif

typedef struct FilaColorSpacePrimaries {
    float r[2];
    float g[2];
    float b[2];
} FilaColorSpacePrimaries;

typedef struct FilaColorSpaceWhitePoint {
    float xy[2];
} FilaColorSpaceWhitePoint;

typedef struct FilaColorSpaceTransferFunction {
    double a;
    double b;
    double c;
    double d;
    double e;
    double f;
    double g;
} FilaColorSpaceTransferFunction;

typedef struct FilaColorSpace {
    FilaColorSpacePrimaries primaries;
    FilaColorSpaceTransferFunction transferFunction;
    FilaColorSpaceWhitePoint whitePoint;
} FilaColorSpace;

void FilaColorSpace_setRec709Primaries(FilaColorSpacePrimaries* outPrimaries);
void FilaColorSpace_setD65WhitePoint(FilaColorSpaceWhitePoint* outWhitePoint);
void FilaColorSpace_setLinearTransferFunction(FilaColorSpaceTransferFunction* outTf);
void FilaColorSpace_setSrgbTransferFunction(FilaColorSpaceTransferFunction* outTf);
void FilaColorSpace_set(FilaColorSpace* colorSpace,
        const FilaColorSpacePrimaries* primaries,
        const FilaColorSpaceTransferFunction* transferFunction,
        const FilaColorSpaceWhitePoint* whitePoint);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_COLOR_SPACE_H

