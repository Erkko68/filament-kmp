#include "../../include/backend/TargetBufferInfo.h"

#include <backend/Handle.h>
#include <backend/TargetBufferInfo.h>

struct FilaBackendTargetBufferInfo {
    filament::backend::TargetBufferInfo impl;
};

struct FilaBackendMRT {
    filament::backend::MRT impl;
};

namespace {
filament::backend::TextureHandle toTextureHandle(uint32_t handleId) {
    if (handleId == filament::backend::HandleBase::nullid) {
        return {};
    }
    return filament::backend::TextureHandle(handleId);
}

FilaBackendTargetBufferInfoData toC(const filament::backend::TargetBufferInfo& info) {
    FilaBackendTargetBufferInfoData out;
    out.textureHandleId = info.handle.getId();
    out.level = info.level;
    out.layer = info.layer;
    return out;
}

filament::backend::TargetBufferInfo toCpp(const FilaBackendTargetBufferInfoData& data) {
    filament::backend::TargetBufferInfo out;
    out.handle = toTextureHandle(data.textureHandleId);
    out.level = data.level;
    out.layer = data.layer;
    return out;
}
} // namespace

extern "C" {

FilaBackendTargetBufferInfo* FilaBackendTargetBufferInfo_create(void) {
    return new FilaBackendTargetBufferInfo;
}

FilaBackendTargetBufferInfo* FilaBackendTargetBufferInfo_createWithHandle(uint32_t textureHandleId) {
    auto info = new FilaBackendTargetBufferInfo;
    info->impl = filament::backend::TargetBufferInfo(toTextureHandle(textureHandleId));
    return info;
}

FilaBackendTargetBufferInfo* FilaBackendTargetBufferInfo_createWithHandleLevel(
        uint32_t textureHandleId, uint8_t level) {
    auto info = new FilaBackendTargetBufferInfo;
    info->impl = filament::backend::TargetBufferInfo(toTextureHandle(textureHandleId), level);
    return info;
}

FilaBackendTargetBufferInfo* FilaBackendTargetBufferInfo_createWithHandleLevelLayer(
        uint32_t textureHandleId, uint8_t level, uint16_t layer) {
    auto info = new FilaBackendTargetBufferInfo;
    info->impl = filament::backend::TargetBufferInfo(toTextureHandle(textureHandleId), level, layer);
    return info;
}

void FilaBackendTargetBufferInfo_destroy(FilaBackendTargetBufferInfo* info) {
    delete info;
}

void FilaBackendTargetBufferInfo_set(FilaBackendTargetBufferInfo* info,
        const FilaBackendTargetBufferInfoData* data) {
    if (!info || !data) {
        return;
    }
    info->impl = toCpp(*data);
}

bool FilaBackendTargetBufferInfo_get(const FilaBackendTargetBufferInfo* info,
        FilaBackendTargetBufferInfoData* outData) {
    if (!info || !outData) {
        return false;
    }
    *outData = toC(info->impl);
    return true;
}

FilaBackendMRT* FilaBackendMRT_create(void) {
    return new FilaBackendMRT;
}

void FilaBackendMRT_destroy(FilaBackendMRT* mrt) {
    delete mrt;
}

uint8_t FilaBackendMRT_getMinSupportedRenderTargetCount(void) {
    return filament::backend::MRT::MIN_SUPPORTED_RENDER_TARGET_COUNT;
}

uint8_t FilaBackendMRT_getMaxSupportedRenderTargetCount(void) {
    return filament::backend::MRT::MAX_SUPPORTED_RENDER_TARGET_COUNT;
}

bool FilaBackendMRT_setTargetBufferAt(FilaBackendMRT* mrt, size_t index,
        const FilaBackendTargetBufferInfoData* data) {
    if (!mrt || !data || index >= filament::backend::MRT::MAX_SUPPORTED_RENDER_TARGET_COUNT) {
        return false;
    }
    mrt->impl[index] = toCpp(*data);
    return true;
}

bool FilaBackendMRT_getTargetBufferAt(const FilaBackendMRT* mrt, size_t index,
        FilaBackendTargetBufferInfoData* outData) {
    if (!mrt || !outData || index >= filament::backend::MRT::MAX_SUPPORTED_RENDER_TARGET_COUNT) {
        return false;
    }
    *outData = toC(mrt->impl[index]);
    return true;
}

}

