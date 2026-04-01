#include <filament/InstanceBuffer.h>

#include <math/mat4.h>

#include <cstring>
#include <vector>

#include "../../include/filament/InstanceBuffer.h"

namespace {
using InstanceBufferBuilder = filament::InstanceBuffer::Builder;
} // namespace

extern "C" {

FilaInstanceBufferBuilder* FilaInstanceBufferBuilder_create(size_t instanceCount) {
    auto builder = new InstanceBufferBuilder(instanceCount);
    return reinterpret_cast<FilaInstanceBufferBuilder*>(builder);
}

void FilaInstanceBufferBuilder_destroy(FilaInstanceBufferBuilder* builder) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<InstanceBufferBuilder*>(builder);
    delete cppBuilder;
}

FilaInstanceBuffer* FilaInstanceBufferBuilder_build(FilaInstanceBufferBuilder* builder, FilaEngine* engine) {
    if (!builder || !engine) {
        return nullptr;
    }
    auto cppBuilder = reinterpret_cast<InstanceBufferBuilder*>(builder);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaInstanceBuffer*>(cppBuilder->build(*cppEngine));
}

size_t FilaInstanceBuffer_getInstanceCount(const FilaInstanceBuffer* instanceBuffer) {
    if (!instanceBuffer) {
        return 0;
    }
    auto cppInstanceBuffer = reinterpret_cast<const filament::InstanceBuffer*>(instanceBuffer);
    return cppInstanceBuffer->getInstanceCount();
}

void FilaInstanceBuffer_setLocalTransforms(FilaInstanceBuffer* instanceBuffer,
        const float* transforms4x4,
        size_t count,
        size_t offset) {
    if (!instanceBuffer || !transforms4x4 || count == 0) {
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

    auto cppInstanceBuffer = reinterpret_cast<filament::InstanceBuffer*>(instanceBuffer);
    cppInstanceBuffer->setLocalTransforms(transforms.data(), count, offset);
}

bool FilaInstanceBuffer_getLocalTransform(FilaInstanceBuffer* instanceBuffer,
        size_t index,
        float* outTransform4x4) {
    if (!instanceBuffer || !outTransform4x4) {
        return false;
    }
    auto cppInstanceBuffer = reinterpret_cast<filament::InstanceBuffer*>(instanceBuffer);
    const filament::math::mat4f& transform = cppInstanceBuffer->getLocalTransform(index);
    std::memcpy(outTransform4x4, &transform, sizeof(filament::math::mat4f));
    return true;
}

} // extern "C"

