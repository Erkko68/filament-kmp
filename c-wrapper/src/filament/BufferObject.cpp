#include "filament/BufferObject.h"
#include <filament/BufferObject.h>
#include <filament/Engine.h>
#include "backend/BufferDescriptor.h"

#include "filament/Types.h"

struct FilaBufferObject {
    filament::BufferObject* impl;
};

struct FilaBufferObjectBuilder {
    filament::BufferObject::Builder* impl;
};

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
    return bufferObject->impl->getByteCount();
}

void FilaBufferObject_setBuffer(FilaBufferObject* bufferObject, FilaEngine* engine, FilaBufferDescriptor* buffer, uint32_t byteOffset) {
    if (!bufferObject || !engine || !buffer) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppBuffer = reinterpret_cast<filament::backend::BufferDescriptor*>(buffer);
    bufferObject->impl->setBuffer(*cppEngine, std::move(*cppBuffer), byteOffset);
}

FilaBufferObjectBuilder* FilaBufferObjectBuilder_create(void) {
    auto builder = new FilaBufferObjectBuilder;
    builder->impl = new filament::BufferObject::Builder();
    return builder;
}

void FilaBufferObjectBuilder_destroy(FilaBufferObjectBuilder* builder) {
    if (!builder) {
        return;
    }
    delete builder->impl;
    delete builder;
}

void FilaBufferObjectBuilder_size(FilaBufferObjectBuilder* builder, uint32_t byteCount) {
    if (!builder) {
        return;
    }
    builder->impl->size(byteCount);
}

void FilaBufferObjectBuilder_bindingType(FilaBufferObjectBuilder* builder, FilaBufferObjectBinding bindingType) {
    if (!builder) {
        return;
    }
    builder->impl->bindingType(toBindingType(bindingType));
}

FilaBufferObject* FilaBufferObjectBuilder_build(FilaBufferObjectBuilder* builder, FilaEngine* engine) {
    if (!builder || !engine) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto bufferObject = new FilaBufferObject;
    bufferObject->impl = builder->impl->build(*cppEngine);
    return bufferObject;
}

}
