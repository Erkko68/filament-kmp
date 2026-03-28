#include <filament/Engine.h>
#include <filament/MaterialEnums.h>
#include <filament/VertexBuffer.h>

#include "../../../include/filament/VertexBuffer.h"

namespace {
using VertexBuilder = filament::VertexBuffer::Builder;

filament::VertexAttribute toVertexAttribute(FilaVertexAttribute attribute) {
    switch (attribute) {
        case FILA_VERTEX_ATTRIBUTE_POSITION:
        default:
            return filament::VertexAttribute::POSITION;
    }
}

filament::VertexBuffer::AttributeType toAttributeType(FilaVertexAttributeType type) {
    switch (type) {
        case FILA_VERTEX_ATTRIBUTE_TYPE_FLOAT3:
        default:
            return filament::VertexBuffer::AttributeType::FLOAT3;
    }
}
} // namespace

extern "C" {

FilaVertexBufferBuilder* FilaVertexBufferBuilder_create(void) {
    auto builder = new VertexBuilder();
    return reinterpret_cast<FilaVertexBufferBuilder*>(builder);
}

void FilaVertexBufferBuilder_destroy(FilaVertexBufferBuilder* builder) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<VertexBuilder*>(builder);
    delete cppBuilder;
}

void FilaVertexBufferBuilder_bufferCount(FilaVertexBufferBuilder* builder, uint8_t bufferCount) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<VertexBuilder*>(builder);
    cppBuilder->bufferCount(bufferCount);
}

void FilaVertexBufferBuilder_vertexCount(FilaVertexBufferBuilder* builder, uint32_t vertexCount) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<VertexBuilder*>(builder);
    cppBuilder->vertexCount(vertexCount);
}

void FilaVertexBufferBuilder_attribute(FilaVertexBufferBuilder* builder,
        FilaVertexAttribute attribute,
        uint8_t bufferIndex,
        FilaVertexAttributeType attributeType,
        uint32_t byteOffset,
        uint8_t byteStride) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<VertexBuilder*>(builder);
    cppBuilder->attribute(toVertexAttribute(attribute), bufferIndex, toAttributeType(attributeType), byteOffset, byteStride);
}

FilaVertexBuffer* FilaVertexBufferBuilder_build(FilaVertexBufferBuilder* builder, FilaEngine* engine) {
    if (!builder || !engine) {
        return nullptr;
    }
    auto cppBuilder = reinterpret_cast<VertexBuilder*>(builder);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaVertexBuffer*>(cppBuilder->build(*cppEngine));
}

size_t FilaVertexBuffer_getVertexCount(const FilaVertexBuffer* vertexBuffer) {
    if (!vertexBuffer) {
        return 0;
    }
    auto cppVertexBuffer = reinterpret_cast<const filament::VertexBuffer*>(vertexBuffer);
    return cppVertexBuffer->getVertexCount();
}

} // extern "C"

