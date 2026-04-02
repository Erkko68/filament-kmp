#include <filament/Engine.h>
#include <filament/IndexBuffer.h>

#include "../../include/filament/BufferDescriptor.h"
#include "../../include/filament/IndexBuffer.h"

namespace {
using IndexBuilder = filament::IndexBuffer::Builder;

filament::IndexBuffer::IndexType toIndexType(FilaIndexType type) {
    switch (type) {
        case FILA_INDEX_TYPE_UINT:
            return filament::IndexBuffer::IndexType::UINT;
        case FILA_INDEX_TYPE_USHORT:
        default:
            return filament::IndexBuffer::IndexType::USHORT;
    }
}
} // namespace

extern "C" {

FilaIndexBufferBuilder* FilaIndexBufferBuilder_create(void) {
    auto builder = new IndexBuilder();
    return reinterpret_cast<FilaIndexBufferBuilder*>(builder);
}

void FilaIndexBufferBuilder_destroy(FilaIndexBufferBuilder* builder) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<IndexBuilder*>(builder);
    delete cppBuilder;
}

void FilaIndexBufferBuilder_indexCount(FilaIndexBufferBuilder* builder, uint32_t indexCount) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<IndexBuilder*>(builder);
    cppBuilder->indexCount(indexCount);
}

void FilaIndexBufferBuilder_bufferType(FilaIndexBufferBuilder* builder, FilaIndexType indexType) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<IndexBuilder*>(builder);
    cppBuilder->bufferType(toIndexType(indexType));
}

FilaIndexBuffer* FilaIndexBufferBuilder_build(FilaIndexBufferBuilder* builder, FilaEngine* engine) {
    if (!builder || !engine) {
        return nullptr;
    }
    auto cppBuilder = reinterpret_cast<IndexBuilder*>(builder);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaIndexBuffer*>(cppBuilder->build(*cppEngine));
}

size_t FilaIndexBuffer_getIndexCount(const FilaIndexBuffer* indexBuffer) {
    if (!indexBuffer) {
        return 0;
    }
    auto cppIndexBuffer = reinterpret_cast<const filament::IndexBuffer*>(indexBuffer);
    return cppIndexBuffer->getIndexCount();
}

bool FilaIndexBuffer_isCreationComplete(const FilaIndexBuffer* indexBuffer) {
    if (!indexBuffer) {
        return false;
    }
    auto cppIndexBuffer = reinterpret_cast<const filament::IndexBuffer*>(indexBuffer);
    return cppIndexBuffer->isCreationComplete();
}

void FilaIndexBuffer_setBuffer(FilaIndexBuffer* indexBuffer, FilaEngine* engine, FilaBufferDescriptor* buffer, uint32_t byteOffset) {
    if (!indexBuffer || !engine || !buffer) {
        return;
    }
    if (!buffer->impl) {
        return;
    }
    auto cppIndexBuffer = reinterpret_cast<filament::IndexBuffer*>(indexBuffer);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppBuffer = reinterpret_cast<filament::backend::BufferDescriptor*>(buffer->impl);
    cppIndexBuffer->setBuffer(*cppEngine, std::move(*cppBuffer), byteOffset);
    delete cppBuffer;
    buffer->impl = nullptr;
    buffer->callback = nullptr;
    buffer->user = nullptr;
    buffer->handler = nullptr;
    buffer->consumed = true;
}

} // extern "C"
