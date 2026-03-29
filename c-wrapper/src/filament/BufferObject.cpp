#include "../../../filament-prebuilts/include/filament/BufferObject.h"
#include "../../../filament-prebuilts/include/filament/Engine.h"

// C wrapper headers
#include "../../../include/filament/BufferObject.h"
#include "filament/Types.h"

namespace {
using BufferBuilder = filament::BufferObject::Builder;

filament::BufferObject::BindingType toBindingType(FilaBufferObjectBinding bindingType) {
    switch (bindingType) {
        case FILA_BUFFER_OBJECT_BINDING_UNIFORM:
            return filament::BufferObject::BindingType::UNIFORM;
        case FILA_BUFFER_OBJECT_BINDING_SHADER_STORAGE:
            return filament::BufferObject::BindingType::SHADER_STORAGE;
        case FILA_BUFFER_OBJECT_BINDING_VERTEX:
        default:
            return filament::BufferObject::BindingType::VERTEX;
    }
}
} // namespace

extern "C" {

size_t FilaBufferObject_getByteCount(const FilaBufferObject* bufferObject) {
    if (!bufferObject) {
        return 0;
    }
    auto cppBufferObject = reinterpret_cast<const filament::BufferObject*>(bufferObject);
    return cppBufferObject->getByteCount();
}

FilaBufferObjectBuilder* FilaBufferObjectBuilder_create(void) {
    auto builder = new BufferBuilder();
    return reinterpret_cast<FilaBufferObjectBuilder*>(builder);
}

void FilaBufferObjectBuilder_destroy(FilaBufferObjectBuilder* builder) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<BufferBuilder*>(builder);
    delete cppBuilder;
}

void FilaBufferObjectBuilder_size(FilaBufferObjectBuilder* builder, uint32_t byteCount) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<BufferBuilder*>(builder);
    cppBuilder->size(byteCount);
}

void FilaBufferObjectBuilder_bindingType(FilaBufferObjectBuilder* builder, FilaBufferObjectBinding bindingType) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<BufferBuilder*>(builder);
    cppBuilder->bindingType(toBindingType(bindingType));
}

FilaBufferObject* FilaBufferObjectBuilder_build(FilaBufferObjectBuilder* builder, FilaEngine* engine) {
    if (!builder || !engine) {
        return nullptr;
    }
    auto cppBuilder = reinterpret_cast<BufferBuilder*>(builder);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaBufferObject*>(cppBuilder->build(*cppEngine));
}

}
