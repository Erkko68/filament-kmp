#include "filament/MaterialEnums.h"

void test_headers_material_enums(void) {
    FilaMaterialShading shading = FILA_MATERIAL_SHADING_LIT;
    FilaMaterialInterpolation interpolation = FILA_MATERIAL_INTERPOLATION_SMOOTH;
    FilaMaterialShaderQuality quality = FILA_MATERIAL_SHADER_QUALITY_HIGH;
    FilaMaterialBlendingMode blendingMode = FILA_MATERIAL_BLENDING_MODE_TRANSPARENT;
    FilaMaterialTransparencyMode transparencyMode = FILA_MATERIAL_TRANSPARENCY_MODE_TWO_PASSES_TWO_SIDES;
    FilaMaterialVertexDomain vertexDomain = FILA_MATERIAL_VERTEX_DOMAIN_WORLD;
    FilaMaterialDomain domain = FILA_MATERIAL_DOMAIN_SURFACE;
    FilaMaterialSpecularAmbientOcclusion sao = FILA_MATERIAL_SPECULAR_AMBIENT_OCCLUSION_SIMPLE;
    FilaMaterialRefractionMode refractionMode = FILA_MATERIAL_REFRACTION_MODE_SCREEN_SPACE;
    FilaMaterialRefractionType refractionType = FILA_MATERIAL_REFRACTION_TYPE_THIN;
    FilaMaterialReflectionMode reflectionMode = FILA_MATERIAL_REFLECTION_MODE_SCREEN_SPACE;
    FilaMaterialUserVariantFilterBit variantMask = FILA_MATERIAL_USER_VARIANT_FILTER_BIT_ALL;
    (void)shading;
    (void)interpolation;
    (void)quality;
    (void)blendingMode;
    (void)transparencyMode;
    (void)vertexDomain;
    (void)domain;
    (void)sao;
    (void)refractionMode;
    (void)refractionType;
    (void)reflectionMode;
    (void)variantMask;

    (void)FILA_MATERIAL_VERSION;
    (void)FILA_RELEASED_MATERIAL_API_LEVEL;
    (void)FILA_UNSTABLE_MATERIAL_API_LEVEL;
}

