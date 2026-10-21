#include "../generated/Includes.hpp"
#include "FilaRenderableManagerManual.h"

using filament::math::float2;
using utils::FixedCapacityVector;

extern "C" {

FilaRenderableManagerBuilder* FilaRenderableManagerBuilder_boneIndicesAndWeights_FixedCapacityVector(FilaRenderableManagerBuilder* self, uint32_t primitiveIndex,
    const FilaFloat2* pairs, const uint32_t* pairCounts, uint32_t vertexCount) {
    auto vertices = FixedCapacityVector<FixedCapacityVector<float2>>::with_capacity(vertexCount);
    const float2* next = fila::cpp(pairs);
    for (uint32_t i = 0; i < vertexCount; i++) {
        vertices.emplace_back(utils::Slice<const float2>(next, pairCounts[i]));
        next += pairCounts[i];
    }
    return fila::c(&fila::cpp(self)->boneIndicesAndWeights(primitiveIndex, std::move(vertices)));
}

} // extern "C"
