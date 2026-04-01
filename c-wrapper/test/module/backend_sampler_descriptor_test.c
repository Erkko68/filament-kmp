#include "backend/SamplerDescriptor.h"

void backend_sampler_descriptor_test(void) {
    FilaSamplerDescriptor* desc = FilaSamplerDescriptor_create();

    FilaBackendSamplerParams params;
    FilaSamplerParams_setDefaults(&params);
    params.filterMag = FILA_BACKEND_SAMPLER_MAG_LINEAR;
    params.filterMin = FILA_BACKEND_SAMPLER_MIN_LINEAR_MIPMAP_LINEAR;
    params.wrapS = FILA_BACKEND_SAMPLER_WRAP_REPEAT;
    params.wrapT = FILA_BACKEND_SAMPLER_WRAP_MIRRORED_REPEAT;
    params.wrapR = FILA_BACKEND_SAMPLER_WRAP_CLAMP_TO_EDGE;
    params.anisotropyLog2 = 2;
    params.compareMode = FILA_BACKEND_SAMPLER_COMPARE_TO_TEXTURE;
    params.compareFunc = FILA_BACKEND_SAMPLER_COMPARE_LE;

    FilaSamplerParams_isFiltered(&params);

    FilaSamplerDescriptor_setTextureHandleId(desc, 42u);
    FilaSamplerDescriptor_getTextureHandleId(desc);
    FilaSamplerDescriptor_hasTextureHandle(desc);

    FilaSamplerDescriptor_setParams(desc, &params);
    FilaSamplerDescriptor_getParams(desc, &params);
    FilaSamplerDescriptor_isFiltered(desc);

    FilaSamplerDescriptor_clearTextureHandle(desc);
    FilaSamplerDescriptor_setTextureHandleId(desc, FILA_BACKEND_HANDLE_NULL);

    FilaSamplerDescriptor_destroy(desc);
}

