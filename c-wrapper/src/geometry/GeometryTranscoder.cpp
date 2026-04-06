#include <geometry/Transcoder.h>

#include <new>

#include "../../include/geometry/Transcoder.h"

namespace {

filament::geometry::ComponentType toComponentType(FilaGeometryComponentType type) {
    switch (type) {
        case FILA_GEOMETRY_COMPONENT_TYPE_BYTE:
            return filament::geometry::ComponentType::BYTE;
        case FILA_GEOMETRY_COMPONENT_TYPE_UBYTE:
            return filament::geometry::ComponentType::UBYTE;
        case FILA_GEOMETRY_COMPONENT_TYPE_SHORT:
            return filament::geometry::ComponentType::SHORT;
        case FILA_GEOMETRY_COMPONENT_TYPE_USHORT:
            return filament::geometry::ComponentType::USHORT;
        case FILA_GEOMETRY_COMPONENT_TYPE_HALF:
            return filament::geometry::ComponentType::HALF;
        case FILA_GEOMETRY_COMPONENT_TYPE_FLOAT:
        default:
            return filament::geometry::ComponentType::FLOAT;
    }
}

} // namespace

extern "C" {

FilaGeometryTranscoder* FilaGeometryTranscoder_create(FilaGeometryTranscoderConfig config) {
    try {
        filament::geometry::Transcoder::Config cppConfig;
        cppConfig.componentType = toComponentType(config.componentType);
        cppConfig.normalized = config.normalized;
        cppConfig.componentCount = config.componentCount;
        cppConfig.inputStrideBytes = config.inputStrideBytes;
        auto* transcoder = new filament::geometry::Transcoder(cppConfig);
        return reinterpret_cast<FilaGeometryTranscoder*>(transcoder);
    } catch (...) {
        return nullptr;
    }
}

void FilaGeometryTranscoder_destroy(FilaGeometryTranscoder* transcoder) {
    if (!transcoder) {
        return;
    }
    auto* cppTranscoder = reinterpret_cast<filament::geometry::Transcoder*>(transcoder);
    delete cppTranscoder;
}

size_t FilaGeometryTranscoder_transcode(const FilaGeometryTranscoder* transcoder,
        float* outTarget,
        const void* source,
        size_t count) {
    if (!transcoder || !source || count == 0u) {
        return 0u;
    }
    auto* cppTranscoder = reinterpret_cast<const filament::geometry::Transcoder*>(transcoder);
    return (*cppTranscoder)(outTarget, source, count);
}

} // extern "C"



