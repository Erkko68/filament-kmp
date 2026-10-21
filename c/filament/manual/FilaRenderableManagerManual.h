// Hand-written counterparts of ../generated's TODO(handwritten) entries.
#ifndef FILA_MANUAL_FILARENDERABLEMANAGERMANUAL_H
#define FILA_MANUAL_FILARENDERABLEMANAGERMANUAL_H

#include "../generated/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Builder::boneIndicesAndWeights with a list per vertex: vertex i has pairCounts[i] (bone index, weight) pairs, read in
// order from pairs (2 floats each).
FilaRenderableManagerBuilder* FilaRenderableManagerBuilder_boneIndicesAndWeights_FixedCapacityVector(FilaRenderableManagerBuilder* self, uint32_t primitiveIndex,
    const FilaFloat2* pairs, const uint32_t* pairCounts, uint32_t vertexCount);

#ifdef __cplusplus
}
#endif

#endif // FILA_MANUAL_FILARENDERABLEMANAGERMANUAL_H
