#include "../../include/backend/SamplerDescriptor.h"

#include <backend/DriverEnums.h>
#include <backend/Handle.h>
#include <backend/SamplerDescriptor.h>

struct FilaSamplerDescriptor {
    filament::backend::SamplerDescriptor impl;
};

namespace {
filament::backend::SamplerParams toCpp(const FilaBackendSamplerParams& params) {
    filament::backend::SamplerParams out{};
    out.filterMag = static_cast<filament::backend::SamplerMagFilter>(params.filterMag);
    out.filterMin = static_cast<filament::backend::SamplerMinFilter>(params.filterMin);
    out.wrapS = static_cast<filament::backend::SamplerWrapMode>(params.wrapS);
    out.wrapT = static_cast<filament::backend::SamplerWrapMode>(params.wrapT);
    out.wrapR = static_cast<filament::backend::SamplerWrapMode>(params.wrapR);
    out.anisotropyLog2 = params.anisotropyLog2;
    out.compareMode = static_cast<filament::backend::SamplerCompareMode>(params.compareMode);
    out.compareFunc = static_cast<filament::backend::SamplerCompareFunc>(params.compareFunc);
    return out;
}

FilaBackendSamplerParams toC(filament::backend::SamplerParams params) {
    FilaBackendSamplerParams out;
    out.filterMag = static_cast<FilaBackendSamplerMagFilter>(params.filterMag);
    out.filterMin = static_cast<FilaBackendSamplerMinFilter>(params.filterMin);
    out.wrapS = static_cast<FilaBackendSamplerWrapMode>(params.wrapS);
    out.wrapT = static_cast<FilaBackendSamplerWrapMode>(params.wrapT);
    out.wrapR = static_cast<FilaBackendSamplerWrapMode>(params.wrapR);
    out.anisotropyLog2 = params.anisotropyLog2;
    out.compareMode = static_cast<FilaBackendSamplerCompareMode>(params.compareMode);
    out.compareFunc = static_cast<FilaBackendSamplerCompareFunc>(params.compareFunc);
    return out;
}

static_assert((int)filament::backend::SamplerWrapMode::CLAMP_TO_EDGE ==
        FILA_BACKEND_SAMPLER_WRAP_CLAMP_TO_EDGE,
    "FilaBackendSamplerWrapMode must stay aligned with filament::backend::SamplerWrapMode");
static_assert((int)filament::backend::SamplerMinFilter::LINEAR_MIPMAP_LINEAR ==
        FILA_BACKEND_SAMPLER_MIN_LINEAR_MIPMAP_LINEAR,
    "FilaBackendSamplerMinFilter must stay aligned with filament::backend::SamplerMinFilter");
static_assert((int)filament::backend::SamplerCompareMode::COMPARE_TO_TEXTURE ==
        FILA_BACKEND_SAMPLER_COMPARE_TO_TEXTURE,
    "FilaBackendSamplerCompareMode must stay aligned with filament::backend::SamplerCompareMode");
static_assert((int)filament::backend::SamplerCompareFunc::N == FILA_BACKEND_SAMPLER_COMPARE_N,
    "FilaBackendSamplerCompareFunc must stay aligned with filament::backend::SamplerCompareFunc");
} // namespace

extern "C" {

FilaSamplerDescriptor* FilaSamplerDescriptor_create(void) {
    return new FilaSamplerDescriptor;
}

void FilaSamplerDescriptor_destroy(FilaSamplerDescriptor* desc) {
    delete desc;
}

void FilaSamplerDescriptor_setTextureHandleId(FilaSamplerDescriptor* desc, uint32_t handleId) {
    if (!desc) {
        return;
    }
    if (handleId == filament::backend::HandleBase::nullid) {
        desc->impl.t.clear();
        return;
    }
    desc->impl.t = filament::backend::TextureHandle(handleId);
}

uint32_t FilaSamplerDescriptor_getTextureHandleId(const FilaSamplerDescriptor* desc) {
    if (!desc) {
        return filament::backend::HandleBase::nullid;
    }
    return desc->impl.t.getId();
}

bool FilaSamplerDescriptor_hasTextureHandle(const FilaSamplerDescriptor* desc) {
    if (!desc) {
        return false;
    }
    return static_cast<bool>(desc->impl.t);
}

void FilaSamplerDescriptor_clearTextureHandle(FilaSamplerDescriptor* desc) {
    if (!desc) {
        return;
    }
    desc->impl.t.clear();
}

void FilaSamplerParams_setDefaults(FilaBackendSamplerParams* params) {
    if (!params) {
        return;
    }
    *params = toC(filament::backend::SamplerParams{});
}

bool FilaSamplerParams_isFiltered(const FilaBackendSamplerParams* params) {
    if (!params) {
        return false;
    }
    return toCpp(*params).isFiltered();
}

void FilaSamplerDescriptor_setParams(FilaSamplerDescriptor* desc, const FilaBackendSamplerParams* params) {
    if (!desc || !params) {
        return;
    }
    desc->impl.s = toCpp(*params);
}

bool FilaSamplerDescriptor_getParams(const FilaSamplerDescriptor* desc, FilaBackendSamplerParams* outParams) {
    if (!desc || !outParams) {
        return false;
    }
    *outParams = toC(desc->impl.s);
    return true;
}

bool FilaSamplerDescriptor_isFiltered(const FilaSamplerDescriptor* desc) {
    if (!desc) {
        return false;
    }
    return desc->impl.s.isFiltered();
}

}

