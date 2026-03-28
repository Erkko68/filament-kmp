#include <filament/Engine.h>
#include <filament/MorphTargetBuffer.h>

#include "../../../include/filament/MorphTargetBuffer.h"

namespace {
using MorphTargetBufferBuilder = filament::MorphTargetBuffer::Builder;
} // namespace

extern "C" {

FilaMorphTargetBufferBuilder* FilaMorphTargetBufferBuilder_create(void) {
    auto builder = new MorphTargetBufferBuilder();
    return reinterpret_cast<FilaMorphTargetBufferBuilder*>(builder);
}

void FilaMorphTargetBufferBuilder_destroy(FilaMorphTargetBufferBuilder* builder) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<MorphTargetBufferBuilder*>(builder);
    delete cppBuilder;
}

void FilaMorphTargetBufferBuilder_vertexCount(FilaMorphTargetBufferBuilder* builder, size_t vertexCount) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<MorphTargetBufferBuilder*>(builder);
    cppBuilder->vertexCount(vertexCount);
}

void FilaMorphTargetBufferBuilder_count(FilaMorphTargetBufferBuilder* builder, size_t count) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<MorphTargetBufferBuilder*>(builder);
    cppBuilder->count(count);
}

void FilaMorphTargetBufferBuilder_withPositions(FilaMorphTargetBufferBuilder* builder, bool enable) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<MorphTargetBufferBuilder*>(builder);
    cppBuilder->withPositions(enable);
}

void FilaMorphTargetBufferBuilder_withTangents(FilaMorphTargetBufferBuilder* builder, bool enable) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<MorphTargetBufferBuilder*>(builder);
    cppBuilder->withTangents(enable);
}

void FilaMorphTargetBufferBuilder_enableCustomMorphing(FilaMorphTargetBufferBuilder* builder, bool enable) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<MorphTargetBufferBuilder*>(builder);
    cppBuilder->enableCustomMorphing(enable);
}

FilaMorphTargetBuffer* FilaMorphTargetBufferBuilder_build(FilaMorphTargetBufferBuilder* builder, FilaEngine* engine) {
    if (!builder || !engine) {
        return nullptr;
    }
    auto cppBuilder = reinterpret_cast<MorphTargetBufferBuilder*>(builder);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaMorphTargetBuffer*>(cppBuilder->build(*cppEngine));
}

size_t FilaMorphTargetBuffer_getVertexCount(const FilaMorphTargetBuffer* morphTargetBuffer) {
    if (!morphTargetBuffer) {
        return 0;
    }
    auto cppMorphTargetBuffer = reinterpret_cast<const filament::MorphTargetBuffer*>(morphTargetBuffer);
    return cppMorphTargetBuffer->getVertexCount();
}

size_t FilaMorphTargetBuffer_getCount(const FilaMorphTargetBuffer* morphTargetBuffer) {
    if (!morphTargetBuffer) {
        return 0;
    }
    auto cppMorphTargetBuffer = reinterpret_cast<const filament::MorphTargetBuffer*>(morphTargetBuffer);
    return cppMorphTargetBuffer->getCount();
}

bool FilaMorphTargetBuffer_hasPositions(const FilaMorphTargetBuffer* morphTargetBuffer) {
    if (!morphTargetBuffer) {
        return false;
    }
    auto cppMorphTargetBuffer = reinterpret_cast<const filament::MorphTargetBuffer*>(morphTargetBuffer);
    return cppMorphTargetBuffer->hasPositions();
}

bool FilaMorphTargetBuffer_hasTangents(const FilaMorphTargetBuffer* morphTargetBuffer) {
    if (!morphTargetBuffer) {
        return false;
    }
    auto cppMorphTargetBuffer = reinterpret_cast<const filament::MorphTargetBuffer*>(morphTargetBuffer);
    return cppMorphTargetBuffer->hasTangents();
}

bool FilaMorphTargetBuffer_isCustomMorphingEnabled(const FilaMorphTargetBuffer* morphTargetBuffer) {
    if (!morphTargetBuffer) {
        return false;
    }
    auto cppMorphTargetBuffer = reinterpret_cast<const filament::MorphTargetBuffer*>(morphTargetBuffer);
    return cppMorphTargetBuffer->isCustomMorphingEnabled();
}

} // extern "C"

