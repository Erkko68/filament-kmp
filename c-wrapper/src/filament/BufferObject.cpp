#include "../../include/filament/BufferObject.h"
#include <filament/BufferObject.h>
#include <filament/Engine.h>
#include "../../include/filament/BufferDescriptor.h"

namespace {
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

void FilaBufferObject_setBuffer(FilaBufferObject* bufferObject, FilaEngine* engine, FilaBufferDescriptor* buffer, uint32_t byteOffset) {
    if (!bufferObject || !engine || !buffer) {
        return;
    }
    if (!buffer->impl) {
        return;
    }
    auto cppBufferObject = reinterpret_cast<filament::BufferObject*>(bufferObject);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppBuffer = reinterpret_cast<filament::backend::BufferDescriptor*>(buffer->impl);
    cppBufferObject->setBuffer(*cppEngine, std::move(*cppBuffer), byteOffset);
    delete cppBuffer;
    buffer->impl = nullptr;
    buffer->callback = nullptr;
    buffer->user = nullptr;
    buffer->handler = nullptr;
    buffer->consumed = true;
}

FilaBufferObjectBuilder* FilaBufferObjectBuilder_create(void) {
    auto builder = new filament::BufferObject::Builder();
    return reinterpret_cast<FilaBufferObjectBuilder*>(builder);
}

void FilaBufferObjectBuilder_destroy(FilaBufferObjectBuilder* builder) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<filament::BufferObject::Builder*>(builder);
    delete cppBuilder;
}

void FilaBufferObjectBuilder_size(FilaBufferObjectBuilder* builder, uint32_t byteCount) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<filament::BufferObject::Builder*>(builder);
    cppBuilder->size(byteCount);
}

void FilaBufferObjectBuilder_bindingType(FilaBufferObjectBuilder* builder, FilaBufferObjectBinding bindingType) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<filament::BufferObject::Builder*>(builder);
    cppBuilder->bindingType(toBindingType(bindingType));
}

FilaBufferObject* FilaBufferObjectBuilder_build(FilaBufferObjectBuilder* builder, FilaEngine* engine) {
    if (!builder || !engine) {
        return nullptr;
    }
    auto cppBuilder = reinterpret_cast<filament::BufferObject::Builder*>(builder);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaBufferObject*>(cppBuilder->build(*cppEngine));
}

}
