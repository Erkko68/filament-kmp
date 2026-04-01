#include <filament/Engine.h>
#include <filament/SkinningBuffer.h>

#include <math/mat4.h>

#include <vector>

#include "../../include/filament/SkinningBuffer.h"

namespace {
using SkinningBufferBuilder = filament::SkinningBuffer::Builder;
} // namespace

extern "C" {

FilaSkinningBufferBuilder* FilaSkinningBufferBuilder_create(void) {
    auto builder = new SkinningBufferBuilder();
    return reinterpret_cast<FilaSkinningBufferBuilder*>(builder);
}

void FilaSkinningBufferBuilder_destroy(FilaSkinningBufferBuilder* builder) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<SkinningBufferBuilder*>(builder);
    delete cppBuilder;
}

void FilaSkinningBufferBuilder_boneCount(FilaSkinningBufferBuilder* builder, uint32_t boneCount) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<SkinningBufferBuilder*>(builder);
    cppBuilder->boneCount(boneCount);
}

void FilaSkinningBufferBuilder_initialize(FilaSkinningBufferBuilder* builder, bool initialize) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<SkinningBufferBuilder*>(builder);
    cppBuilder->initialize(initialize);
}

FilaSkinningBuffer* FilaSkinningBufferBuilder_build(FilaSkinningBufferBuilder* builder, FilaEngine* engine) {
    if (!builder || !engine) {
        return nullptr;
    }
    auto cppBuilder = reinterpret_cast<SkinningBufferBuilder*>(builder);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaSkinningBuffer*>(cppBuilder->build(*cppEngine));
}

void FilaSkinningBuffer_setBonesMat4f(FilaSkinningBuffer* skinningBuffer,
        FilaEngine* engine,
        const float* transforms4x4,
        size_t count,
        size_t offset) {
    if (!skinningBuffer || !engine || !transforms4x4 || count == 0) {
        return;
    }

    std::vector<filament::math::mat4f> transforms(count);
    for (size_t i = 0; i < count; ++i) {
        const float* src = transforms4x4 + i * 16;
        filament::math::float4 c0(src[0], src[1], src[2], src[3]);
        filament::math::float4 c1(src[4], src[5], src[6], src[7]);
        filament::math::float4 c2(src[8], src[9], src[10], src[11]);
        filament::math::float4 c3(src[12], src[13], src[14], src[15]);
        transforms[i] = filament::math::mat4f(c0, c1, c2, c3);
    }

    auto cppSkinningBuffer = reinterpret_cast<filament::SkinningBuffer*>(skinningBuffer);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    cppSkinningBuffer->setBones(*cppEngine, transforms.data(), count, offset);
}

size_t FilaSkinningBuffer_getBoneCount(const FilaSkinningBuffer* skinningBuffer) {
    if (!skinningBuffer) {
        return 0;
    }
    auto cppSkinningBuffer = reinterpret_cast<const filament::SkinningBuffer*>(skinningBuffer);
    return cppSkinningBuffer->getBoneCount();
}

} // extern "C"

