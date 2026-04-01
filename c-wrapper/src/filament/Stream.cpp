#include <filament/Engine.h>
#include <filament/Stream.h>

#include <math/mat3.h>

#include "../../include/filament/BufferDescriptor.h"
#include "../../include/filament/Stream.h"

namespace {
using StreamBuilder = filament::Stream::Builder;

filament::backend::StreamType toStreamType(FilaStreamType type) {
    switch (type) {
        case FILA_STREAM_ACQUIRED:
            return filament::backend::StreamType::ACQUIRED;
        case FILA_STREAM_NATIVE:
        default:
            return filament::backend::StreamType::NATIVE;
    }
}

FilaStreamType fromStreamType(filament::backend::StreamType type) {
    switch (type) {
        case filament::backend::StreamType::ACQUIRED:
            return FILA_STREAM_ACQUIRED;
        case filament::backend::StreamType::NATIVE:
        default:
            return FILA_STREAM_NATIVE;
    }
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

void FilaStream_setAcquiredImage(
        FilaStream* stream,
        void* image,
        FilaStreamCallback callback,
        void* userData,
        const float transform[9]) {
    if (!stream || !image || !callback) return;
    auto cppStream = reinterpret_cast<filament::Stream*>(stream);
    filament::math::mat3f t;
    if (transform) {
        t = filament::math::mat3f(
                transform[0], transform[1], transform[2],
                transform[3], transform[4], transform[5],
                transform[6], transform[7], transform[8]);
    }
    cppStream->setAcquiredImage(image, callback, userData, t);
}

void FilaStream_setAcquiredImageWithHandler(
        FilaStream* stream,
        void* image,
        FilaCallbackHandler* handler,
        FilaStreamCallback callback,
        void* userData,
        const float transform[9]) {
    if (!stream || !image || !callback) return;
    auto cppStream = reinterpret_cast<filament::Stream*>(stream);
    auto cppHandler = reinterpret_cast<filament::backend::CallbackHandler*>(handler ? handler->impl : nullptr);
    filament::math::mat3f t;
    if (transform) {
        t = filament::math::mat3f(
                transform[0], transform[1], transform[2],
                transform[3], transform[4], transform[5],
                transform[6], transform[7], transform[8]);
    }
    cppStream->setAcquiredImage(image, cppHandler, callback, userData, t);
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

