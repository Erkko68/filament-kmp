#include <filament/Engine.h>
#include <filament/Stream.h>

#include "../../../include/filament/Stream.h"

namespace {
using StreamBuilder = filament::Stream::Builder;

filament::backend::StreamType toStreamType(FilaStreamType type) {
    return static_cast<filament::backend::StreamType>(type);
}

FilaStreamType fromStreamType(filament::backend::StreamType type) {
    return static_cast<FilaStreamType>(type);
}
} // namespace

extern "C" {

FilaStreamBuilder* FilaStreamBuilder_create(void) {
    auto builder = new StreamBuilder();
    return reinterpret_cast<FilaStreamBuilder*>(builder);
}

void FilaStreamBuilder_destroy(FilaStreamBuilder* builder) {
    if (!builder) return;
    auto cppBuilder = reinterpret_cast<StreamBuilder*>(builder);
    delete cppBuilder;
}

void FilaStreamBuilder_width(FilaStreamBuilder* builder, uint32_t width) {
    if (!builder) return;
    auto cppBuilder = reinterpret_cast<StreamBuilder*>(builder);
    cppBuilder->width(width);
}

void FilaStreamBuilder_height(FilaStreamBuilder* builder, uint32_t height) {
    if (!builder) return;
    auto cppBuilder = reinterpret_cast<StreamBuilder*>(builder);
    cppBuilder->height(height);
}

FilaStream* FilaStreamBuilder_build(FilaStreamBuilder* builder, FilaEngine* engine) {
    if (!builder || !engine) return nullptr;
    auto cppBuilder = reinterpret_cast<StreamBuilder*>(builder);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaStream*>(cppBuilder->build(*cppEngine));
}

FilaStreamType FilaStream_getStreamType(const FilaStream* stream) {
    if (!stream) return FILA_STREAM_ACQUIRED;
    auto cppStream = reinterpret_cast<const filament::Stream*>(stream);
    return fromStreamType(cppStream->getStreamType());
}

void FilaStream_setDimensions(FilaStream* stream, uint32_t width, uint32_t height) {
    if (!stream) return;
    auto cppStream = reinterpret_cast<filament::Stream*>(stream);
    cppStream->setDimensions(width, height);
}

int64_t FilaStream_getTimestamp(const FilaStream* stream) {
    if (!stream) return 0;
    auto cppStream = reinterpret_cast<const filament::Stream*>(stream);
    return cppStream->getTimestamp();
}

} // extern "C"

